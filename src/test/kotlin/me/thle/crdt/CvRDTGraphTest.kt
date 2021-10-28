package me.thle.crdt

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class CvRDTGraphTest {

    private lateinit var g: CvRDTGraph<String>

    @BeforeEach
    fun setUp() {
        g = CvRDTGraph()
    }

    @AfterEach
    fun tearDown() {
    }

    @Test
    internal fun `test add vertex`() {
        g.addVertex("a")

        assertTrue(g.contains("a"))
    }

    @Test
    internal fun `test remove vertex`() {
        g.addVertex("a")

        g.removeVertex("b")
        assertTrue(g.contains("a"))

        g.removeVertex("a")
        assertFalse(g.contains("a"))
    }

    @Test
    internal fun `test remove vertex supporting an edge`() {
        g.addVertex("a")
        g.addVertex("b")
        g.addEdge("a", "b")

        g.removeVertex("a")

        assertFalse(g.contains("a"))
        // edge is deleted
        assert(g.neighbors("a").isEmpty())
    }

    @Test
    internal fun `test add edge`() {
        g.addVertex("a")
        g.addVertex("b")

        g.addEdge("a", "b")

        assert(g.neighbors("a")[0] == "b")
        assert(g.neighbors("b")[0] == "a")
    }

    @Test
    internal fun `test add edge with no vertices`() {
        g.addEdge("a", "b")

        // not added
        assert(g.neighbors("a").isEmpty())
    }

    @Test
    internal fun `test neighbors`() {
        g.addVertex("a")
        g.addVertex("b")
        g.addEdge("a", "b")

        val neighbors = g.neighbors("a")
        assert(neighbors.size == 1)
        assert(neighbors[0] == "b")
        assert(g.neighbors("b")[0] == "a")
    }

    @Test
    internal fun `test remove edge`() {
        g.addVertex("a")
        g.addVertex("b")
        g.addEdge("a", "b")

        g.removeEdge("a", "b")

        assert(g.neighbors("a").isEmpty())
    }

    @Test
    internal fun `test remove edge with reversed order`() {
        g.addVertex("a")
        g.addVertex("b")
        g.addEdge("a", "b")

        g.removeEdge("b", "a")

        assert(g.neighbors("a").isEmpty())
    }

    @Test
    internal fun `test get path`() {
        g.addVertex("a")
        g.addVertex("b")
        g.addVertex("c")
        g.addEdge("a", "b")
        assert(g.path("a", "b")[0].joinToString() == "a, b")
        assert(g.path("b", "a")[0].joinToString() == "b, a")
        assert(g.path("a", "c").isEmpty())
        assert(g.path("b", "c").isEmpty())

        g.addEdge("b", "c")
        assert(g.path("a", "c")[0].joinToString() == "a, b, c")

        g.addVertex("d")
        g.addEdge("a", "d")
        assert(g.path("c", "d")[0].joinToString() == "c, b, a, d")

        g.addEdge("b", "d")
        val pathAD = g.path("a", "d")
        assert(pathAD.size == 2)
        assert(pathAD.map { p -> p.joinToString() }.containsAll(listOf("a, d", "a, b, d")))
    }

    @Test
    internal fun `test merge`() {
        g.addVertex("a")
        g.addVertex("b")

        // g1 has the same state as g
        val g1 = CvRDTGraph<String>()
        g1.addVertex("a")
        g1.addVertex("b")

        // start changing
        g.addEdge("a", "b")
        g1.removeVertex("a")

        val merged = g.merge(g1)

        assertTrue(merged == g1.merge(g))
        assertTrue(merged.contains("b"))
        assertFalse(merged.contains("a"))
        assert(merged.neighbors("a").isEmpty())
        assert(merged.path("a", "b").isEmpty())
    }
}
