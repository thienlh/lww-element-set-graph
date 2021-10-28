package me.thle.crdt

interface CvRDT<T> {
    /**
     * Merge 2 RDT instances based on their state
     */
    fun merge(other: T): T
}
