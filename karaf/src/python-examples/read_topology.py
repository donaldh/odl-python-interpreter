from opendaylight import iid, LogicalDatastoreType, NormalizedNodes
from nodes import Container, List, Leaf

ns = 'urn:TBD:params:xml:ns:yang:network-topology'
rev = '2013-10-21'
topoName = 'totally-topological'

topoId = iid(ns, rev, 'network-topology', 'topology', ('topology', {'topology-id': topoName}))
transaction = dataBroker.newReadOnlyTransaction()
future = transaction.read(LogicalDatastoreType.OPERATIONAL, topoId)
optional = future.checkedGet()
log.warn(NormalizedNodes.toStringTree(optional.get()))
