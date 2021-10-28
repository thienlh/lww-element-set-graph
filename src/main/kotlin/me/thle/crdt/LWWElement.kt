package me.thle.crdt

import java.time.Instant

internal data class LWWElement<T>(val value: T, val timestamp: Instant = Instant.now())
