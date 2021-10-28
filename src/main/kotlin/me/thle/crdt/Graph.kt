package me.thle.crdt

interface Graph<T> {
    /**
     * Get all vertices
     */
    fun vertices(): List<T>

    /**
     * Get all edges
     */
    fun edges(): List<Edge<T>>

    /**
     * Add a vertex, if it's not there yet
     */
    fun addVertex(u: T)

    /**
     * Remove a vertex and all edges that it supports
     */
    fun removeVertex(u: T)

    /**
     * Add an edge if u and v is already in the graph
     */
    fun addEdge(u: T, v: T)

    /**
     * Remove an edge if it's alredy in the graph
     */
    fun removeEdge(u: T, v: T)

    /**
     * Returns true if vertex u is in the graph, otherwise returns false
     */
    fun contains(u: T): Boolean

    /**
     * Return all vertices connected to vertex u by an edge
     */
    fun neighbors(u: T): List<T>

    /**
     * Find all paths from u to v
     */
    fun path(u: T, v: T): List<List<T>>
}
