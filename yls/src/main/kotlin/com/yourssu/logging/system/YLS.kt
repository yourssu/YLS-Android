package com.yourssu.logging.system

import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody

class YLS {
    abstract class Logger {
        protected var eventData: YLSEventData? = null

        open fun user(name: String): Logger {
            eventData = eventData?.copy(user = name) ?: createDefaultEvent(user = name)
            return this
        }

        open fun timestamp(timestamp: String): Logger {
            eventData = eventData?.copy(timestamp = timestamp) ?: createDefaultEvent(timestamp = timestamp)
            return this
        }

        open fun event(event: Map<String, Any>): Logger {
            eventData = eventData?.let {
                it.copy(event = it.event.addWithoutOverwriting(event))
            } ?: createDefaultEvent(event = event)
            return this
        }

        protected abstract fun createDefaultEvent(
            user: String? = null,
            timestamp: String? = null,
            event: Map<String, Any>? = null,
        ): YLSEventData

        abstract fun log()
    }

    open class DefaultLogger(
        val url: String,
        val platform: String,       // ex: android
        val serviceName: String,    // ex: Soomsil
    ) : Logger() {
        private val client = OkHttpClient()

        override fun createDefaultEvent(
            user: String?,
            timestamp: String?,
            event: Map<String, Any>?,
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
            val e = eventData ?: createDefaultEvent()
            println(e.toJsonString())
//            val request = createRequest(e.toJsonString())
//            client.newCall(request).execute()
        }

        private fun createRequest(json: String): Request {
            return Request.Builder()
                .url(url)
                .put(RequestBody.create(MediaType.parse("application/json"), json))
                .build()
        }
    }

    companion object YLSLogger {
        private lateinit var logger: Logger

        fun init(logger: Logger) {
            this.logger = logger
        }

        fun log() {
            this.logger.log()
        }
    }
}
