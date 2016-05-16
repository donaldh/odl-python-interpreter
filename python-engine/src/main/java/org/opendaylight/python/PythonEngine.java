package org.opendaylight.python;

import java.net.URI;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.opendaylight.controller.md.sal.common.api.data.LogicalDatastoreType;
import org.opendaylight.controller.md.sal.common.api.data.TransactionChainListener;
import org.opendaylight.controller.md.sal.dom.api.DOMDataBroker;
import org.opendaylight.controller.md.sal.dom.api.DOMDataBrokerExtension;
import org.opendaylight.controller.md.sal.dom.api.DOMDataChangeListener;
import org.opendaylight.controller.md.sal.dom.api.DOMDataReadOnlyTransaction;
import org.opendaylight.controller.md.sal.dom.api.DOMDataReadWriteTransaction;
import org.opendaylight.controller.md.sal.dom.api.DOMDataWriteTransaction;
import org.opendaylight.controller.md.sal.dom.api.DOMTransactionChain;
import org.opendaylight.yangtools.concepts.ListenerRegistration;
import org.opendaylight.yangtools.yang.data.api.YangInstanceIdentifier;
import org.opendaylight.yangtools.yang.model.api.SchemaContext;
import org.python.core.Py;
import org.python.core.PyDictionary;
import org.python.core.PyList;
import org.python.core.PyString;
import org.python.core.PySystemState;
import org.python.util.PythonInterpreter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PythonEngine implements DirectoryMonitor.Listener {

    private final Logger log = LoggerFactory.getLogger(PythonEngine.class);

    protected DOMDataBroker domDataBroker;
    protected SchemaContext schemaContext;

    protected PythonInterpreter python;
    protected Path scriptDir;
    protected DirectoryMonitor monitor;

    protected Map<Path, ScriptState> currentScripts = new HashMap<Path, ScriptState>();

    interface Cleanup {
        public void cleanup();
    }

    class ScriptState {
        private List<Cleanup> cleanups = new ArrayList<Cleanup>();
        public void addCleanup(Cleanup c) { cleanups.add(c); }
        public void cleanup() { for (Cleanup c : cleanups) { c.cleanup(); } }
    }

    class DataBrokerProxy implements DOMDataBroker, Cleanup {
        protected DOMDataBroker delegate;
        protected List<ListenerRegistration<DOMDataChangeListener>> listenerRegistrations =
                new ArrayList<ListenerRegistration<DOMDataChangeListener>>();

        public DataBrokerProxy(DOMDataBroker delegate) {
            this.delegate = delegate;
        }

        @Override
        public ListenerRegistration<DOMDataChangeListener> registerDataChangeListener(LogicalDatastoreType store,
                YangInstanceIdentifier path, DOMDataChangeListener listener,
                org.opendaylight.controller.md.sal.common.api.data.AsyncDataBroker.DataChangeScope triggeringScope) {
            ListenerRegistration<DOMDataChangeListener> registration =
                    delegate.registerDataChangeListener(store, path, listener, triggeringScope);
            listenerRegistrations.add(registration);
            return registration;
        }

        @Override
        public Map<Class<? extends DOMDataBrokerExtension>, DOMDataBrokerExtension> getSupportedExtensions() {
            return delegate.getSupportedExtensions();
        }

        @Override
        public DOMDataReadOnlyTransaction newReadOnlyTransaction() {
            return delegate.newReadOnlyTransaction();
        }

        @Override
        public DOMDataReadWriteTransaction newReadWriteTransaction() {
            return delegate.newReadWriteTransaction();
        }

        @Override
        public DOMDataWriteTransaction newWriteOnlyTransaction() {
            return delegate.newWriteOnlyTransaction();
        }

        @Override
        public DOMTransactionChain createTransactionChain(TransactionChainListener listener) {
            return delegate.createTransactionChain(listener);
        }

        public void cleanup() {
            for (ListenerRegistration<DOMDataChangeListener> r : listenerRegistrations) {
                log.warn("Closing listener registration {}", r);
                r.close();
            }
        }
    }

    public PythonEngine(SchemaContext schemaContext, DOMDataBroker domDataBroker) {
        this.schemaContext = schemaContext;
        this.domDataBroker = domDataBroker;

        try {
            String osgiConfigurationArea = System.getProperty("karaf.home");
            String configurationDir = new URI(osgiConfigurationArea).getPath();

            scriptDir = FileSystems.getDefault().getPath(configurationDir, "python-scripts");
            Files.createDirectories(scriptDir, PosixFilePermissions.asFileAttribute(PosixFilePermissions.fromString("rwxr-xr-x")));
            log.info("Watching for python scripts in {}", scriptDir);

            PySystemState systemState = Py.getSystemState();
            Path libDir = FileSystems.getDefault().getPath(configurationDir, "python-lib");
            systemState.path = new PyList();
            systemState.path.append(new PyString(libDir.toString()));

            monitor = new DirectoryMonitor(scriptDir, this);
            new Thread(monitor, "Python script watcher").start();

        } catch (Throwable t) {
            log.error("Exception while initializing Jython", t);
        }
    }

    @Override
    public void add(Path path) {
        ScriptState state = new ScriptState();
        currentScripts.put(path, state);
        try {
            runScript(path.toString(), state);
        } catch (Throwable t) {
            log.error("Failed to exec script {}", path);
            log.error("Exception: ", t);
        }
    }

    @Override
    public void remove(Path path) {
        ScriptState state = currentScripts.remove(path);
        if (state != null) {
            state.cleanup();
        } else {
            log.warn("Asked to remove a script but not found {}", path);
        }
    }

    public void runScript(String script, ScriptState state) {
        DataBrokerProxy dataBrokerProxy = new DataBrokerProxy(domDataBroker);
        state.addCleanup(dataBrokerProxy);

        PyDictionary namespace = new PyDictionary();
        namespace.__setitem__(new PyString("schemaContext"), Py.java2py(schemaContext));
        namespace.__setitem__(new PyString("dataBroker"), Py.java2py(dataBrokerProxy));
        namespace.__setitem__(new PyString("log"), Py.java2py(log));
        python = PythonInterpreter.threadLocalStateInterpreter(namespace);

        python.execfile(script);
    }
}
