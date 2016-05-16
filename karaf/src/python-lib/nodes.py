import org.opendaylight.yangtools.yang.data.impl.schema.Builders as Builders
import org.opendaylight.yangtools.yang.data.api.schema.NormalizedNodes as NormalizedNodes
import org.opendaylight.yangtools.yang.common.QName as QName
from org.opendaylight.yangtools.yang.data.api.YangInstanceIdentifier import NodeIdentifier, NodeIdentifierWithPredicates

class Node(object):
    def __init__(self, name, children):
        self.children = children
        if isinstance(name, basestring):
            self.name = name
            self.module = None
            self.rev = None
        else:
            self.module = name[0]
            self.rev = name[1]
            self.name = name[2]
        
    def ns(self, module, rev):
        if self.module is None:
            self.module = module
        if self.rev is None:
            self.rev = rev
        return self
    
    def genChildren(self):
        children = []
        for child in self.children:
            child.ns(self.module, self.rev)
            node = child.build()
            if node is not None:
                children.append(node)
        return children

class Container(Node):
    def __init__(self, name, *rest):
        Node.__init__(self, name, rest)
    
    def build(self):
        builder = Builders.containerBuilder().withNodeIdentifier(NodeIdentifier.create(QName.create(self.module, self.rev, self.name)))
        children = self.genChildren()
        for child in children:
            builder.withChild(child)
        return builder.build()
    
class List(Node):
    def __init__(self, name, key, value, *rest):
        Node.__init__(self, name, rest)
        self.key = key
        self.value = value
        
    def build(self):
        builder = Builders.mapBuilder().withNodeIdentifier(NodeIdentifier.create(QName.create(self.module, self.rev, self.name)))
        entry = Builders.mapEntryBuilder().withNodeIdentifier(NodeIdentifierWithPredicates(QName.create(self.module, self.rev, self.name),
                                                                                           QName.create(self.module, self.rev, self.key),
                                                                                           self.value
                                                                                           ))
        children = self.genChildren()
        for child in children:
            entry.withChild(child)
        builder.withChild(entry.build())
        return builder.build()

class Leaf(Node):
    def __init__(self, name, value):
        Node.__init__(self, name, [])
        self.value = value

    def build(self):
        builder = Builders.leafBuilder().withNodeIdentifier(NodeIdentifier.create(QName.create(self.module, self.rev, self.name)))
        builder.withValue(self.value)
        return builder.build()
