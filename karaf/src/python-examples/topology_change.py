from opendaylight import (iid, DOMDataChangeListener, LogicalDatastoreType, DataChangeScope)

class TopologyChangeListener(DOMDataChangeListener):
    def onDataChanged(self, change):
        for id in change.getCreatedData().keySet():
            log.warn("{} added to topology-netconf", id.getLastPathArgument())
        for id in change.getRemovedPaths():
            log.warn("{} removed from topology-neconf", id.getLastPathArgument())

ns = 'urn:TBD:params:xml:ns:yang:network-topology'
rev = '2013-10-21'
topoId = iid(ns, rev, 'network-topology',
             'topology',
             ("topology", {'topology-id': 'topology-netconf'}),
             'node', 'node')

log.warn("Registering for change with iid {}", topoId)
registration = dataBroker.registerDataChangeListener(LogicalDatastoreType.OPERATIONAL, topoId, TopologyChangeListener(), DataChangeScope.BASE)
log.info("Initialized topology.py {}", registration)
