odl-python-interpreter
======================

This is an experimental project that embeds the Jython interpreter in OpenDaylight. The goal of this project is to
enable DOM data store projects to be written in python, including read/write transactions and data change callbacks.

### Execution environment

Scripts are executed with some things already in scope:

```
schemaContext  # An instance of the YANG SchemaContext
dataBroker     # A DOMDataBroker
log            # A slf4j Logger
```

### Usage

This example shows the DOMDataBroker being used to create a new network topology.
```
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
```

This example shows how you can register for data change notifications.
```
from opendaylight import (iid, DOMDataChangeListener, LogicalDatastoreType, DataChangeScope)

class TopologyChangeListener(DOMDataChangeListener):
    def onDataChanged(self, change):
        for id in change.getCreatedData().keySet():
            log.warn("{} added to topology-netconf", id.getLastPathArgument())
        for id in change.getRemovedPaths():
            log.warn("{} removed from topology-netconf", id.getLastPathArgument())

ns = 'urn:TBD:params:xml:ns:yang:network-topology'
rev = '2013-10-21'
topoId = iid(ns, rev, 'network-topology',
             'topology',
             ("topology", {'topology-id': 'topology-netconf'}),
             'node', 'node')

log.warn("Registering for change with iid {}", topoId)
registration = dataBroker.registerDataChangeListener(LogicalDatastoreType.OPERATIONAL, topoId, TopologyChangeListener(), DataChangeScope.BASE)
log.info("Initialized topology.py {}", registration)
```

### Utility Packages

Two utility packages are also provided, to help with constructing DOM data store parameters.

#### opendaylight

This package provides an *iid* function for constructing instance identifiers.

```
opendaylight.iid(namespace, revision, path-arg ...) returns YangInstanceIdentifier
# namespace - YANG module namespace
# revision  - YANG module revision
# path-arg  - String name of a path segment, or tuple (name, { 'key' : value })
#             for path segments that require keys
```

For example:
```
from opendaylight import iid

nodeId = iid(ns, rev, 'network-topology',
             'topology',
             ('topology', {'topology-id': topoName}),
             'node',
             ('node', {'node-id': nodeName}))
```

Note that *iid* currently only works for instance identifiers that are completely within a single YANG module.

#### nodes

This is an experimental package for constructing NormalizeNode payloads for DOM data store calls:

```
from nodes import Container, List, Leaf

topo = Container(('nt', '2015-12-16', 'network-topology'),
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
```