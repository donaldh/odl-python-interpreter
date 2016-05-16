import org.opendaylight.controller.md.sal.dom.api.DOMDataChangeListener as DOMDataChangeListener
import org.opendaylight.controller.md.sal.common.api.data.LogicalDatastoreType as LogicalDatastoreType
import org.opendaylight.yangtools.yang.data.api.YangInstanceIdentifier as YangInstanceIdentifier
import org.opendaylight.yangtools.yang.common.QName as QName
import org.opendaylight.yangtools.concepts.ListenerRegistration as ListenerRegistration
import org.opendaylight.controller.md.sal.common.api.data.AsyncDataChangeEvent
import org.opendaylight.yangtools.yang.data.api.schema.NormalizedNode
import org.opendaylight.yangtools.yang.data.api.schema.NormalizedNodes as NormalizedNodes
import org.opendaylight.controller.md.sal.dom.api.DOMDataWriteTransaction
import org.opendaylight.yangtools.yang.data.api.schema.MapEntryNode as MapEntryNode
import org.opendaylight.yangtools.yang.data.impl.schema.ImmutableNodes as ImmutableNodes
import org.opendaylight.yangtools.yang.data.impl.schema.builder.impl.ImmutableMapEntryNodeBuilder as ImmutableMapEntryNodeBuilder
import java.util.HashMap
import java.lang.String
from org.opendaylight.controller.md.sal.common.api.data.AsyncDataBroker import DataChangeScope
from org.opendaylight.yangtools.yang.data.api.YangInstanceIdentifier import NodeIdentifierWithPredicates

def iid(ns, revision, *elems):
    builder = YangInstanceIdentifier.builder()
    for elem in elems:
        if isinstance(elem, basestring):
            builder.node(QName.create(ns, revision, elem))
        else:
            nodename = elem[0]
            nodekeys = elem[1]
            javakeys = java.util.HashMap()
            for key in nodekeys.keys():
                javakeys.put(QName.create(ns, revision, key), nodekeys[key])
            builder.nodeWithKey(QName.create(ns, revision, nodename), javakeys)
    return builder.build()
