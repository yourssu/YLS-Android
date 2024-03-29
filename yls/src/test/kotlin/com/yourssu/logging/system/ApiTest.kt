package com.yourssu.logging.system

import com.yourssu.logging.system.remote.LoggingService
import com.yourssu.logging.system.remote.toLogListRequest
import com.yourssu.logging.system.remote.toLoggingRequest
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.HttpURLConnection

class ApiTest {

    lateinit var server: MockWebServer
    lateinit var retrofit: Retrofit
    lateinit var loggingService: LoggingService

    @Before
    fun init() {
        server = MockWebServer()
        server.start()

        val client = OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply { setLevel(HttpLoggingInterceptor.Level.BODY) })
            .build()

        retrofit = Retrofit.Builder()
            .baseUrl(server.url("/"))         // 가짜 Response 테스트
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        loggingService = retrofit.create(LoggingService::class.java)
    }

    @Test
    fun singleLogTest() {
        server.enqueue(successSingleLogResponse)

        val eventData = YLSEventData(
            hashedId = "testtest",
            timestamp = YLS.getTimestamp(),
            version = 1,
            event = mapOf("event" to "test"),
        )
        runBlocking {
            val response = loggingService.putLog(eventData.toLoggingRequest())
            assertTrue(response.body()!!.success)
        }
    }

    @Test
    fun multipleLogTest() {
        server.enqueue(successMultipleLogResponse)

        val eventDataList = listOf(
            YLSEventData(
                hashedId = "testtest",
                timestamp = YLS.getTimestamp(),
                version = 1,
                event = mapOf("event" to "test"),
            ),
        )
        runBlocking {
            val response = loggingService.putLogList(eventDataList.toLogListRequest())
            assertTrue(response.body()!!.success) // Fail: body is null
        }
    }

    // 가짜 Response (success)
    val successSingleLogResponse by lazy {
        MockResponse().apply {
            setResponseCode(HttpURLConnection.HTTP_OK)
            setBody(
                """
                    {
                      "success": true,
                      "result": {
                        "user": "testtest",
                        "timestamp": "2023-12-04T10:30:00Z",
                        "event": {
                            "event": "test"
                        }
                      }
                    }
                """.trimIndent(),
            )
        }
    }

    // 가짜 Response (success)
    val successMultipleLogResponse by lazy {
        MockResponse().apply {
            setResponseCode(HttpURLConnection.HTTP_OK)
            setBody(
                """
                    {
                      "success": true,
                      "result": {}
                    }
                """.trimIndent(),
            )
        }
    }
}
