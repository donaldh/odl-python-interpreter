package org.opendaylight.controller.config.yang.config.python_engine.impl;

import org.opendaylight.controller.md.sal.dom.api.DOMDataBroker;
import org.opendaylight.controller.sal.core.api.model.SchemaService;
import org.opendaylight.python.PythonProvider;

public class PythonEngineModule extends org.opendaylight.controller.config.yang.config.python_engine.impl.AbstractPythonEngineModule {
    public PythonEngineModule(org.opendaylight.controller.config.api.ModuleIdentifier identifier, org.opendaylight.controller.config.api.DependencyResolver dependencyResolver) {
        super(identifier, dependencyResolver);
    }

    public PythonEngineModule(org.opendaylight.controller.config.api.ModuleIdentifier identifier, org.opendaylight.controller.config.api.DependencyResolver dependencyResolver, org.opendaylight.controller.config.yang.config.python_engine.impl.PythonEngineModule oldModule, java.lang.AutoCloseable oldInstance) {
        super(identifier, dependencyResolver, oldModule, oldInstance);
    }

    @Override
    public void customValidation() {
        // add custom validation form module attributes here.
    }

    @Override
    public java.lang.AutoCloseable createInstance() {

        SchemaService schemaService = getSchemaServiceDependency();
        DOMDataBroker domBroker = getDomDataBrokerDependency();

        return new PythonProvider(domBroker, schemaService);
    }

}
