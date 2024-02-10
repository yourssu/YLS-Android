package com.yourssu.logging.system

/**
 * Add new key-value pairs from `other` to `this`, ignoring existing keys.
 *
 * ```kotlin
 * // example
 * val a = mapOf(1 to "one", 2 to "two")
 * val b = mapOf(2 to "hi", 3 to "three")
 * a.addWithoutOverwriting(b) // mapOf(1 to "one", 2 to "two", 3 to "three")
 * ```
 */
fun <K, V> Map<K, V>.addWithoutOverwriting(other: Map<K, V>): Map<K, V> {
    val result = HashMap(this)
    other.forEach { (key, value) ->
        if (!result.containsKey(key)) {
            result[key] = value
        }
    }
    return result
}
