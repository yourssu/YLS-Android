package com.yourssu.logging.system

import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody

class YLS {
    abstract class Logger {
        abstract fun log()
    }

    open class DefaultLogger(
        val url: String,
        val platform: String,       // ex: android
        val serviceName: String,    // ex: Soomsil
    ) : Logger() {
        private val client = OkHttpClient()

        private var eventData: YLSEventData? = null

        fun user(name: String): DefaultLogger {
            eventData = eventData?.copy(user = name) ?: defaultEvent(user = name)
            return this
        }

        fun timestamp(timestamp: String): DefaultLogger {
            eventData = eventData?.copy(timestamp = timestamp) ?: defaultEvent(timestamp = timestamp)
            return this
        }

        fun event(event: Map<String, Any>): DefaultLogger {
            eventData = eventData?.let {
                it.copy(event = it.event.addWithoutOverwriting(event))
            } ?: defaultEvent(event = event)
            return this
        }

        private fun defaultEvent(
            user: String? = null,
            timestamp: String? = null,
            event: Map<String, Any>? = null,
        ): YLSEventData {
            // todo: create default eventdata object
            val e = YLSEventData("", "2024-01-01", mapOf())
            return e.copy(
                user = user ?: e.user,
                timestamp = timestamp ?: e.timestamp,
                event = event ?: e.event,
            )
        }

        override fun log() {
            val e = eventData ?: defaultEvent()
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

        override fun log() {
            this.logger.log()
        }
    }
}
