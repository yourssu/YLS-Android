package com.yourssu.logging.system.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.PUT

interface LoggingService {
    @PUT("/log")
    suspend fun putLog(@Body request: LoggingRequest): Response<LoggingResponse>

    @PUT("/log/list")
    suspend fun putLogList(@Body request: LogListRequest): Response<LogListResponse>
}
