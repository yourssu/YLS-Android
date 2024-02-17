package com.yourssu.logging.system

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.PUT

data class YLSResponse(val a: String)

interface HttpService {
    @PUT("/")
    suspend fun putLog(@Body eventData: YLSEventData): Response<YLSResponse>
}
