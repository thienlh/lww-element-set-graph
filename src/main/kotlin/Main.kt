import me.thle.crdt.CvRDTGraph

fun main(args: Array<String>) {
    val graph = CvRDTGraph<Int>()
    graph.addVertex(1)
    graph.addVertex(2)
    graph.addVertex(3)
    graph.addEdge(1, 2)

    println(graph)
}
