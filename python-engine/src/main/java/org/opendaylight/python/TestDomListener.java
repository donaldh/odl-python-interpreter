package org.opendaylight.python;

import org.opendaylight.controller.md.sal.common.api.data.AsyncDataBroker.DataChangeScope;
import org.opendaylight.controller.md.sal.common.api.data.AsyncDataChangeEvent;
import org.opendaylight.controller.md.sal.common.api.data.LogicalDatastoreType;
import org.opendaylight.controller.md.sal.dom.api.DOMDataBroker;
import org.opendaylight.controller.md.sal.dom.api.DOMDataChangeListener;
import org.opendaylight.yangtools.concepts.ListenerRegistration;
import org.opendaylight.yangtools.yang.common.QName;
import org.opendaylight.yangtools.yang.data.api.YangInstanceIdentifier;
import org.opendaylight.yangtools.yang.data.api.schema.NormalizedNode;
import org.opendaylight.yangtools.yang.model.api.SchemaContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestDomListener implements DOMDataChangeListener {

    private static final String TOPO_NS = "urn:TBD:params:xml:ns:yang:network-topology";
    private static final String TOPO_REV = "2013-10-21";

    private static final Logger log = LoggerFactory.getLogger(TestDomListener.class);

    public TestDomListener(SchemaContext schemaContext, DOMDataBroker domDataBroker) {

        YangInstanceIdentifier id = YangInstanceIdentifier.builder()
                .node(QName.create(TOPO_NS, TOPO_REV, "network-topology"))
                .node(QName.create(TOPO_NS, TOPO_REV, "topology"))
                .nodeWithKey(QName.create(TOPO_NS, TOPO_REV, "topology"), QName.create(TOPO_NS, TOPO_REV, "topology-id"), "topology-netconf")
                .build();

        ListenerRegistration<DOMDataChangeListener> registration =
                domDataBroker.registerDataChangeListener(LogicalDatastoreType.CONFIGURATION, id, this, DataChangeScope.SUBTREE);
        log.error("Registered {}", registration.toString());
    }

    @Override
    public void onDataChanged(AsyncDataChangeEvent<YangInstanceIdentifier, NormalizedNode<?, ?>> change) {
        log.error("Received change {}", change.toString());
    }
}
