package com.yourssu.logging.system

data class YLSEventData(
    val user: String,
    val timestamp: String, // ISO 8601
    val event: Map<String, Any>,
)
