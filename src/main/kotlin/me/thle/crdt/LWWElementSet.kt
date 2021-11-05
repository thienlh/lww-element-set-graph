package me.thle.crdt

internal class LWWElementSet<E>() : MutableSet<E>, CvRDT<LWWElementSet<E>> {
    private val add = mutableSetOf<LWWElement<E>>()
    private val tombstone = mutableSetOf<LWWElement<E>>()

    internal constructor(add: Set<LWWElement<E>>, tombstone: Set<LWWElement<E>>) : this() {
        this.add.addAll(add)
        this.tombstone.addAll(tombstone)
    }

    override fun add(element: E) = add.add(LWWElement(element))

    override fun remove(element: E) = tombstone.add(LWWElement(element))

    override fun contains(element: E) = add.contains(element) && !foundLaterInTombstone(element)

    private fun foundLaterInTombstone(element: E): Boolean {
        val fromTombstone = tombstone.find(element)
        return fromTombstone != null && fromTombstone.timestamp > add.largestTimestampFor(element)!!
    }

    override val size: Int
        get() = iterator().asSequence().toSet().size

    override fun containsAll(elements: Collection<E>): Boolean = iterator().asSequence().toSet().containsAll(elements)

    override fun isEmpty() = !iterator().hasNext()

    override fun iterator(): MutableIterator<E> =
        add.map(LWWElement<E>::value).filter { e -> contains(e) }.toMutableList().iterator()

    override fun merge(other: LWWElementSet<E>) = LWWElementSet(add.union(other.add), tombstone.union(other.tombstone))

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as LWWElementSet<*>

        val items = toList()
        val otherItems = other.toList()

        if (items.size != otherItems.size) return false
        if (!items.containsAll(otherItems)) return false

        return true
    }

    override fun hashCode() = iterator().hashCode()

    override fun toString() = "LWWElementSet(add=$add, tombstone=$tombstone)"

    override fun addAll(elements: Collection<E>) = add.addAll(elements.map { LWWElement(it) })

    override fun clear() {
        this.add.clear()
        this.tombstone.clear()
    }

    override fun removeAll(elements: Collection<E>): Boolean {
        tombstone.addAll(elements.map { LWWElement(it) }.toSet())
        return add.values().intersect(tombstone.values()).isNotEmpty()
    }

    override fun retainAll(elements: Collection<E>) = TODO("not yet implemented")

    private fun Collection<LWWElement<E>>.largestTimestampFor(element: E) =
        filter { e -> e.value == element }.maxOfOrNull { e -> e.timestamp }

    private fun Collection<LWWElement<E>>.contains(e: E) = any { element -> element.value == e }

    private fun Collection<LWWElement<E>>.find(e: E) =
        filter { element -> element.value == e }.maxByOrNull { r -> r.timestamp }

    private fun Collection<LWWElement<E>>.values() = map { e -> e.value }.toSet()
}
