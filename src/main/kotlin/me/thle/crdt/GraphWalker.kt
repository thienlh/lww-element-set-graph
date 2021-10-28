package me.thle.crdt

// Implementation of Depth-First-Search (DFS) algorithm and Backtracking
// https://www.baeldung.com/cs/simple-paths-between-two-vertices
class GraphWalker<E>(private val graph: Graph<E>) {
    private lateinit var visited: MutableList<E>
    private lateinit var currentPath: MutableList<E>
    private lateinit var simplePaths: MutableList<List<E>>

    fun path(u: E, v: E): List<List<E>> {
        visited = mutableListOf()
        currentPath = mutableListOf()
        simplePaths = mutableListOf()
        dfs(u, v)
        return simplePaths.filterPossiblePathsWithin(graph)
    }

    private fun dfs(u: E, v: E) {
        if (visited.contains(u)) {
            return
        }
        visited.add(u)
        currentPath.add(u)
        if (u == v) {
            simplePaths.add(currentPath.toList())
            visited.remove(u)
            currentPath.removeLast()
            return
        }
        for (next in graph.vertices()) {
            dfs(next, v)
        }
        currentPath.removeLast()
        visited.remove(u)
    }

    private fun MutableList<List<E>>.filterPossiblePathsWithin(graph: Graph<E>) = filter { it.isPossibleWithin(graph) }

    private fun List<E>.isPossibleWithin(graph: Graph<E>) = graph.edges().containsAll(Edge.from(this))
}
