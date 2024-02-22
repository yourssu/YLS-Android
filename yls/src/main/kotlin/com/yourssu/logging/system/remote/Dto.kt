package com.yourssu.logging.system.remote

import com.google.gson.annotations.SerializedName
import com.yourssu.logging.system.YLSEventData

fun YLSEventData.toLoggingRequest(): LoggingRequest {
    return LoggingRequest(
        hashedId = hashedId,
        timestamp = timestamp,
        event = event,
    )
}

data class LoggingRequest(
    @SerializedName("hashedID")
    val hashedId: String,
    @SerializedName("timestamp")
    val timestamp: String,
    @SerializedName("event")
    val event: Map<String, Any>,
)

data class LoggingResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("result")
    val result: LoggingRequest,
)

fun List<YLSEventData>.toLogListRequest(): LogListRequest {
    return LogListRequest(eventList = this)
}

data class LogListRequest(
    @SerializedName("logRequestList")
    val eventList: List<YLSEventData>,
)

data class LogListResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("result")
    val result: LogListRequest,
)



