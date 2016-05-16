package org.opendaylight.python;

import org.opendaylight.controller.md.sal.dom.api.DOMDataBroker;
import org.opendaylight.controller.sal.core.api.model.SchemaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PythonProvider implements AutoCloseable {

    private static final Logger log = LoggerFactory.getLogger(PythonProvider.class);

    protected DOMDataBroker domDataBroker;
    protected SchemaService schemaService;

    protected TestDomListener testDomListener;

    public PythonProvider(DOMDataBroker domDataBroker, SchemaService schemaService) {
        this.domDataBroker = domDataBroker;
        this.schemaService = schemaService;

        PythonEngine python = new PythonEngine(schemaService.getGlobalContext(), domDataBroker);
    }

    @Override
    public void close() throws Exception {
        // TODO Auto-generated method stub
    }
}
