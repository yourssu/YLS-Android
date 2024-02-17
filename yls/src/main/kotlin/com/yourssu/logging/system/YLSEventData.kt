package com.yourssu.logging.system

data class YLSEventData(
    val hashedID: String,
    val timestamp: String, // ISO 8601
    val event: Map<String, Any>
)
