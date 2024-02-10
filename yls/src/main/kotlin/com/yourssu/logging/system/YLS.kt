package com.yourssu.logging.system

import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody

class YLS {
    abstract class Logger {
        abstract fun log(event: YLSEventData)
    }

    open class DefaultLogger(
        val url: String,
        val platform: String,       // ex: android
        val serviceName: String,    // ex: Soomsil
    ) : Logger() {
        private val baseEvent = mapOf(
            "platform" to platform,
            "serviceName" to serviceName,
        )
        private val client = OkHttpClient()

        override fun log(event: YLSEventData) {
            val e = event.copy(event = event.event + baseEvent)
            val request = createRequest(e.toJsonString())
            client.newCall(request).execute()
        }

        private fun createRequest(json: String): Request {
            return Request.Builder()
                .url(url)
                .put(RequestBody.create(MediaType.parse("application/json"), json))
                .build()
        }
    }

    companion object YLSLogger : Logger() {
        private lateinit var logger: Logger

        fun init(logger: Logger) {
            this.logger = logger
        }

        override fun log(event: YLSEventData) {
            this.logger.log(event)
        }
    }
}
