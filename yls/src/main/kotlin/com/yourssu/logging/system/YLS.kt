package com.yourssu.logging.system

import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody

class YLS {
    abstract class Logger {
        protected abstract fun log(user: String, timestamp: String, event: Map<String, Any>)
    }

    open class DefaultLogger(
        val url: String,
        val platform: String,       // ex: android
        val serviceName: String,    // ex: Soomsil
    ) : Logger() {
        private val client = OkHttpClient()

        override fun log(user: String, timestamp: String, event: Map<String, Any>) {
            val eventData = YLSEventData(user, timestamp, event)
            val json = eventData.toJsonString()
            val request = Request.Builder()
                .url(url)
                .put(RequestBody.create(MediaType.parse("application/json"), json))
                .build()
            client.newCall(request).execute()
        }
    }

    companion object YLSLogger {
        private lateinit var logger: Logger

        fun init(logger: Logger) {
            this.logger = logger
        }

        fun log() {
//            this.logger.log()
        }
    }
}
