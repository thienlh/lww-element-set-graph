package me.thle.crdt


// This is an implementation of a state-based Conflict-free replicated (CvRDT) graph
// with a pair of Last Write Win (LWW) Element Sets as implementation for vertices and edges
class CvRDTGraph<E>() : CvRDT<CvRDTGraph<E>>, Graph<E> {
    private var vertices = LWWElementSet<E>()
    private var edges = LWWElementSet<Edge<E>>()
    private val walker = GraphWalker(this)

    internal constructor(vertices: LWWElementSet<E>, edges: LWWElementSet<Edge<E>>) : this() {
        this.vertices = vertices
        this.edges = edges
    }

    override fun vertices() = vertices.toList()

    override fun edges() = edges.toList()

    override fun addVertex(u: E) {
        vertices.add(u)
    }

    override fun removeVertex(u: E) {
        // What should happen upon concurrent addEdge(u,v) || removeVertex(u)?
        // Choose: (i) Give precedence to removeVertex(u): all edges to or from u are removed as a side effect.
        // 3.4 Graphs https://hal.inria.fr/inria-00555588/PDF/techreport.pdf
        for (v in vertices()) if (u != v) removeEdge(u, v)
        vertices.remove(u)
    }

    override fun addEdge(u: E, v: E) {
        if (vertices.containsAll(listOf(u, v))) edges.add(Edge(u, v))
    }

    override fun removeEdge(u: E, v: E) {
        edges.remove(Edge(u, v))
    }

    override fun contains(u: E) = vertices.contains(u)

    override fun neighbors(u: E) =
        edges.filter { e -> e.supportedBy(u) }.flatMap { e -> e.asSet() }.toSet().filter { e -> e != u }

    override fun path(u: E, v: E) = walker.path(u, v)

    override fun merge(other: CvRDTGraph<E>) = CvRDTGraph(vertices.merge(other.vertices), edges.merge(other.edges))

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CvRDTGraph<*>

        if (vertices != other.vertices) return false
        if (edges != other.edges) return false

        return true
    }

    override fun hashCode(): Int {
        var result = vertices.hashCode()
        result = 31 * result + edges.hashCode()
        return result
    }

    override fun toString(): String {
        return "GraphImpl(vertices=$vertices, edges=$edges)"
    }
}


