package com.yourssu.logging.system

import com.google.gson.Gson

data class YLSEventData(
    val user: String,
    val timestamp: String, // ISO 8601
    val event: Map<String, Any>,
) {
    fun toJsonString(): String {
        return Gson().toJson(this)
    }
}
