package me.thle.crdt

import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

internal class EdgeTest {
    @Test
    internal fun `test equals`() {
        assert(Edge("a", "b") == Edge("a", "b"))
        assert(Edge("a", "b") == Edge("b", "a"))
        assert(Edge("a", "b") != Edge("a", "c"))
    }

    @Test
    internal fun `test contains`() {
        val edge = Edge("a", "b")
        assert(edge.supportedBy("a"))
        assert(edge.supportedBy("b"))
        assert(!edge.supportedBy("c"))
    }

    @Test
    internal fun `test create edge with same vertex`() {
        assertFailsWith<IllegalArgumentException> { Edge("a", "a") }
    }

    @Test
    internal fun `test to set`() {
        assert(Edge("a", "b").asSet().containsAll(listOf("a", "b")))
    }

    @Test
    internal fun `test from paths`() {
        assert(Edge.from(listOf("a")).isEmpty())
        assert(Edge.from(listOf<String>()).isEmpty())
        assert(Edge.from(listOf("a", "b")) == listOf(Edge("a", "b")))
        assertFailsWith<IllegalArgumentException> { Edge.from(listOf("a", "a")) }

        val edges = Edge.from(listOf("a", "b", "c"))
        assert(edges.size == 2)
        assert(edges[0] == Edge("a", "b"))
        assert(edges[1] == Edge("b", "c"))
    }
}
