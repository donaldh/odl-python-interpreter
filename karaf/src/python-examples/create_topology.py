from opendaylight import iid, LogicalDatastoreType, NormalizedNodes
from nodes import Container, List, Leaf

ns = 'urn:TBD:params:xml:ns:yang:network-topology'
rev = '2013-10-21'
igpns = 'urn:TBD:params:xml:ns:yang:nt:l3-unicast-igp-topology'
igprev = '2013-10-21'
topoName = 'totally-topological'
nodeName = 'sample-node'

node = Container((ns, rev, 'network-topology'),
                 List('topology', 'topology-id', topoName,
                      Container('topology-types',
                                Container((igpns, igprev, 'l3-unicast-igp-topology'))
                                ),
                      List('node', 'node-id', nodeName,
                           Container((igpns, igprev, 'igp-node-attributes'),
                                     Leaf('router-id', '192.168.0.1')
                                     )
                           )
                      )
                 ).build()

log.warn(NormalizedNodes.toStringTree(node))
transaction = dataBroker.newWriteOnlyTransaction()
transaction.put(LogicalDatastoreType.OPERATIONAL, iid(ns, rev, 'network-topology'), node)
future = transaction.submit()
future.checkedGet()
