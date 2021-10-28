package me.thle.crdt

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Instant

internal class LWWElementSetTest {
    @BeforeEach
    fun setUp() {
    }

    @AfterEach
    fun tearDown() {
    }

    @Test
    internal fun `test contains with empty set`() {
        assertFalse(LWWElementSet<String>().contains("a"))
    }

    @Test
    internal fun `test contains`() {
        val t = Instant.ofEpochMilli(100)
        val s = LWWElementSet(setOf(LWWElement("a", t)), setOf(LWWElement("b", t)))

        assertTrue(s.contains("a"))
        assertFalse(s.contains("b"))
    }


    @Test
    internal fun `test contains with same element found in tombstone`() {
        // element in tombstone has later timestamp compare to
        // the latest timestamp in add set
        val t = Instant.ofEpochMilli(100)
        var s = LWWElementSet(setOf(LWWElement("a", t)), setOf(LWWElement("a", t.plusMillis(100))))

        assertFalse(s.contains("a"))

        // element in tombstone has earlier timestamp compare to
        // the latest timestamp in add set
        s = LWWElementSet(setOf(LWWElement("a", t)), setOf(LWWElement("a", t.minusMillis(100))))

        assertTrue(s.contains("a"))

        // the latest timestamp in add set is from a different element
        s =
            LWWElementSet(
                setOf(LWWElement("a", t), LWWElement("b", t.plusMillis(1000))),
                setOf(LWWElement("a", t.minusMillis(100)))
            )

        assertTrue(s.contains("a"))
    }

    @Test
    internal fun `test add`() {
        val s = LWWElementSet<String>()
        s.add("a")
        s.add("a")
        s.add("a")
        assertTrue(s.contains("a"))
    }

    @Test
    internal fun `test add all`() {
        val s = LWWElementSet<String>()
        s.addAll(listOf("a", "b", "c"))
        assertTrue(s.contains("a"))
        assertTrue(s.contains("b"))
        assertTrue(s.contains("c"))
    }

    @Test
    internal fun `test clear`() {
        val s = LWWElementSet<Int>()
        s.addAll(listOf(1, 2, 3))
        s.clear()
        assertTrue(s.size == 0)
    }

    @Test
    internal fun `test add after remove`() {
        // The advantage of LWW-Element-Set over 2P-Set is that,
        // unlike 2P-Set, LWW-Element-Set allows an element to be reinserted after having been removed.
        // https://en.wikipedia.org/wiki/Conflict-free_replicated_data_type#cite_note-2011CRDTSurvey-2
        val s = LWWElementSet<String>()
        s.add("a")
        s.remove("a")
        s.add("a")

        assert(s.contains("a"))
    }

    @Test
    internal fun `test remove`() {
        val s = LWWElementSet<String>()
        s.add("a")

        s.remove("a")

        assertFalse(s.contains("a"))
    }

    @Test
    internal fun `test remove twice`() {
        val s = LWWElementSet<String>()
        s.add("a")

        s.remove("a")
        s.remove("a")

        assertFalse(s.contains("a"))
    }

    @Test
    internal fun `test size`() {
        val s = LWWElementSet<String>()

        s.add("a")
        assert(s.size == 1)

        s.add("a")
        assert(s.size == 1)

        s.add("b")
        assert(s.size == 2)

        s.remove("a")
        assert(s.size == 1)
    }

    @Test
    internal fun `test contains all`() {
        val s = LWWElementSet<String>()

        s.add("a")
        s.add("b")

        assertTrue(s.containsAll(listOf("a", "b")))
        assertFalse(s.containsAll(listOf("a", "b", "c")))

        s.remove("b")
        assertFalse(s.containsAll(listOf("a", "b")))
    }

    @Test
    internal fun `test is empty`() {
        val s = LWWElementSet<String>()

        assertTrue(s.isEmpty())

        s.add("a")
        assertFalse(s.isEmpty())

        s.remove("a")
        assertTrue(s.isEmpty())
    }

    @Test
    internal fun `test iterator`() {
        val s = LWWElementSet<String>()

        assertFalse(s.iterator().hasNext())

        s.add("a")
        assertTrue(s.iterator().hasNext())

        s.remove("a")
        assertFalse(s.iterator().hasNext())
    }

    @Test
    internal fun `test merge`() {
        val s1 = LWWElementSet<String>()
        val s2 = LWWElementSet<String>()

        s1.add("a")
        s2.add("a")

        s1.add("b")
        s2.add("c")

        assert(s1.merge(s2) == s2.merge(s1))
        assert(s1.merge(s2).containsAll(listOf("a", "b", "c")))

        s2.remove("a")
        assert(s1.merge(s2).containsAll(listOf("b", "c")))

        s1.remove("a")
        s1.remove("b")
        s1.remove("c")
        assert(s1.merge(s2).isEmpty())

        s1.add("a")
        assert(s1.merge(s2).contains("a"))
    }

    @Test
    internal fun `test merge with same timestamp`() {
        val t = Instant.ofEpochMilli(100)
        // add and remove with same timestamp
        val t1 = t.plusMillis(100)
        val s1 = LWWElementSet(setOf(LWWElement("a", t), LWWElement("b", t1)), setOf())
        val s2 = LWWElementSet(setOf(LWWElement("a", t)), setOf(LWWElement("a", t1)))

        // should bias toward add
        assert(s1.merge(s2).containsAll(listOf("a", "b")))

        s2.remove("a")
        assert(s1.merge(s2).containsAll(listOf("b")))
    }
}
