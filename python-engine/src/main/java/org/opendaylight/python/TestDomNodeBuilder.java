package org.opendaylight.python;

import org.opendaylight.yangtools.yang.common.QName;
import org.opendaylight.yangtools.yang.data.api.YangInstanceIdentifier.NodeIdentifier;
import org.opendaylight.yangtools.yang.data.api.YangInstanceIdentifier.NodeIdentifierWithPredicates;
import org.opendaylight.yangtools.yang.data.api.YangInstanceIdentifier.NodeWithValue;
import org.opendaylight.yangtools.yang.data.api.schema.ContainerNode;
import org.opendaylight.yangtools.yang.data.api.schema.MapEntryNode;
import org.opendaylight.yangtools.yang.data.api.schema.NormalizedNodes;
import org.opendaylight.yangtools.yang.data.impl.schema.Builders;
import org.opendaylight.yangtools.yang.data.impl.schema.ImmutableNodes;
import org.opendaylight.yangtools.yang.data.impl.schema.builder.impl.ImmutableMapEntryNodeBuilder;

public class TestDomNodeBuilder {

    public static final String TOPO_NS = "urn:TBD:params:xml:ns:yang:network-topology";
    public static final String TOPO_REV = "2013-10-21";

    public static final String IGP_TOPO_NS = "urn:TBD:params:xml:ns:yang:nt:l3-unicast-igp-topology";
    public static final String IGP_TOPO_REV = "2013-10-21";

    public static ContainerNode networkTopology() {
        ContainerNode networkTopology =
                Builders.containerBuilder().withNodeIdentifier(nodeIdent(TOPO_NS, TOPO_REV, "network-topology"))
                .withChild(Builders.mapBuilder().withNodeIdentifier(nodeIdent(TOPO_NS, TOPO_REV, "topology"))
                        .withChild(Builders.mapEntryBuilder()
                                .withNodeIdentifier(nodeIdent(TOPO_NS, TOPO_REV, "topology", "topology-id", "totally-topological"))
                                .withChild(Builders.containerBuilder()
                                        .withNodeIdentifier(nodeIdent(TOPO_NS, TOPO_REV, "topology-types"))
                                        .withChild(Builders.containerBuilder()
                                                .withNodeIdentifier(nodeIdent(IGP_TOPO_NS, IGP_TOPO_REV, "l3-unicast-igp-topology"))
                                                .build())
                                        .build())
                                .withChild(Builders.mapBuilder()
                                        .withNodeIdentifier(nodeIdent(TOPO_NS, TOPO_REV, "node"))
                                        .withChild(Builders.mapEntryBuilder()
                                                .withNodeIdentifier(nodeIdent(TOPO_NS, TOPO_REV, "node", "node-id", "demo-node"))
                                                .withChild(Builders.containerBuilder()
                                                        .withNodeIdentifier(nodeIdent(IGP_TOPO_NS, IGP_TOPO_REV, "igp-node-attributes"))
                                                        .withChild(Builders.leafSetBuilder()
                                                                .withNodeIdentifier(nodeIdent(IGP_TOPO_NS, IGP_TOPO_REV, "router-id"))
                                                                .withChild(Builders.leafSetEntryBuilder()
                                                                        .withNodeIdentifier(new NodeWithValue(QName.create(IGP_TOPO_NS, IGP_TOPO_REV, "router-id"),"192.168.0.1"))
                                                                        .withValue("192.168.0.1")
                                                                        .build())
                                                                .build())
                                                        .build())
                                                .withChild(Builders.mapBuilder()
                                                        .withNodeIdentifier(nodeIdent(TOPO_NS, TOPO_REV, "termination-point"))
                                                        .withChild(Builders.mapEntryBuilder()
                                                                .withNodeIdentifier(nodeIdent(TOPO_NS, TOPO_REV, "termination-point", "tp-id", "GigabitEthernet0/0/0/0"))
                                                                .withChild(Builders.containerBuilder()
                                                                        .withNodeIdentifier(nodeIdent(IGP_TOPO_NS, IGP_TOPO_REV, "igp-termination-point-attributes"))
                                                                        .withChild(Builders.choiceBuilder()
                                                                                .withNodeIdentifier(nodeIdent(IGP_TOPO_NS, IGP_TOPO_REV, "termination-point-type"))
                                                                                .withChild(Builders.leafBuilder()
                                                                                        .withNodeIdentifier(nodeIdent(IGP_TOPO_NS, IGP_TOPO_REV, "unnumbered-id"))
                                                                                        .withValue(new Integer(0))
                                                                                        .build())
                                                                                .build())
                                                                        .build())
                                                                .build())
                                                        .build())
                                                .build())
                                        .build())
                                .build())
                        .build())
                .build();
        return networkTopology;
    }

    private static NodeIdentifier nodeIdent(String ns, String rev, String name) {
        return NodeIdentifier.create(QName.create(ns, rev, name));
    }

    private static NodeIdentifierWithPredicates nodeIdent(String ns, String rev, String name, String keyname, String keyval) {
        return new NodeIdentifierWithPredicates(
                QName.create(ns, rev, name),
                QName.create(ns, rev, keyname),
                keyval);
    }

    public static void main(String[] args) {
        MapEntryNode container = ImmutableNodes.mapEntryBuilder(
                QName.create(TOPO_NS, TOPO_REV, "node"),
                QName.create(TOPO_NS, TOPO_REV, "node-id"),
                "hello")
                .build();
        System.out.println(NormalizedNodes.toStringTree(container));

        MapEntryNode n = ImmutableMapEntryNodeBuilder.create()
                .withNodeIdentifier(new NodeIdentifierWithPredicates(
                        QName.create(TOPO_NS, TOPO_REV, "node"),
                        QName.create(TOPO_NS, TOPO_REV, "node-id"),
                        "hello"))
                .withChild(ImmutableNodes.leafNode(QName.create(TOPO_NS, TOPO_REV, "node-id"), "hello"))
                .build();
        System.out.println(NormalizedNodes.toStringTree(n));

        System.out.println(NormalizedNodes.toStringTree(networkTopology()));
    }
}
