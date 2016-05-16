from opendaylight import iid

nodeId = iid(ns, rev, 'network-topology',
             'topology',
             ('topology', {'topology-id': topoName}),
             'node',
             ('node', {'node-id': nodeName}))
