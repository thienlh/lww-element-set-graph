package me.thle.crdt

data class Edge<E>(val u: E, val v: E) {
    init {
        if (u == v) throw IllegalArgumentException("an edge can only be formed from two different vertices")
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Edge<*>

        if (u != other.u && u != other.v) return false
        if (v != other.v && v != other.u) return false

        return true
    }

    fun supportedBy(vertex: E) = vertex == u || vertex == v

    fun asSet() = setOf(u, v)

    override fun hashCode(): Int {
        var result = u?.hashCode() ?: 0
        result = 31 * result + (v?.hashCode() ?: 0)
        return result
    }

    companion object {
        fun <E> from(path: List<E>): List<Edge<E>> {
            if (path.size < 2) return listOf()
            if (path.size == 2) return listOf(Edge(path[0], path[1]))
            val edges = mutableListOf<Edge<E>>()
            for (i in 0..path.size - 2) {
                edges.add(Edge(path[i], path[i + 1]))
            }
            return edges
        }
    }
}
