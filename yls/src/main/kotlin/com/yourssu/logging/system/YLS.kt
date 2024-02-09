package com.yourssu.logging.system

import okhttp3.MediaType
import okhttp3.Request
import okhttp3.RequestBody

abstract class Logger {
    abstract fun log(event: YLSEventData)
}

class YLS {
    open class RemoteLogger(
        val url: String,
        val platform: String,       // ex: android
        val serviceName: String,    // ex: Soomsil
    ) : Logger() {


        override fun log(event: YLSEventData) {

        }

        private fun createRequest(json: String): Request =
            Request.Builder()
                .url(url)
                .put(RequestBody.create(MediaType.parse("application/json"), json))
                .build()
    }

    companion object : Logger() {
        private lateinit var logger: Logger

        fun init(logger: Logger) {
            this.logger = logger
        }

        override fun log(event: YLSEventData) {
            this.logger.log(event)
        }
    }
}
