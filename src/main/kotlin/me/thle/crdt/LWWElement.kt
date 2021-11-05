package me.thle.crdt

internal data class LWWElement<T>(val value: T, val timestamp: Long = System.nanoTime())
