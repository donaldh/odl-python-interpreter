from nodes import Container, List, Leaf
import org.opendaylight.yangtools.yang.data.api.schema.NormalizedNodes as NormalizedNodes

node = Container(('nt', '2015-12-16', 'network-topology'),
                 List('topology', 'topology-id', 'totally-topo',
                      Container('topology-types',
                                Container(('igp', '2010-12-10', 'l3-unicast-igp-topology'))
                                ),
                      List('node', 'node-id', 'demo-node',
                           Container(('igp', '2010-12-10', 'igp-node-attributes'),
                                     Leaf('router-id', '192.168.0.1')
                                     )
                           )
                      )
                 ).build()

print
print NormalizedNodes.toStringTree(node)
