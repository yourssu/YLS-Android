package com.yourssu.logging.system

@JvmInline
value class Params(internal val value: Map<String, Any>) {
    constructor(vararg params: Pair<String, Any>) : this(params.toMap())
}

fun Map<String, Any>.toParams(): Params = Params(this)

fun Pair<String, Any>.toParams(): Params = Params(this)
