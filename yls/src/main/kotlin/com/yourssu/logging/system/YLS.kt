package com.yourssu.logging.system

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Retrofit

class YLS private constructor() {
    abstract class Logger {
        abstract fun log(eventData: YLSEventData)
    }

    open class RemoteLogger(private val url: String) : Logger() {

        protected val service: HttpService by lazy {
            Retrofit.Builder()
                .baseUrl(url)
                .build()
                .create(HttpService::class.java)
        }

        override fun log(eventData: YLSEventData) {
            GlobalScope.launch {
                service.putLog(eventData)
            }
        }
    }

    companion object {
        private lateinit var logger: Logger
        private lateinit var defaultMap: Map<String, Any>
        private lateinit var userID: String

        fun init(
            platform: String,
            serviceName: String,
            user: String,
            logger: Logger,
        ) {
            this.defaultMap = mapOf("platform" to platform, "serviceName" to serviceName)
            this.userID = user
            this.logger = logger
        }

        fun randomId(): String = "aaa"

        fun log(vararg events: Pair<String, Any>) {
            if (!::logger.isInitialized) throw AssertionError()

            val eventData = YLSEventData(
                hashedID = hashing(userID),
                timestamp = "",
                event = events.toMap() + defaultMap,
            )
            logger.log(eventData)
        }

        private fun hashing(id: String) = "asdf"
    }
}
