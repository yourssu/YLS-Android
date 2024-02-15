package com.yourssu.logging.system

class YLS private constructor() {
    abstract class Logger {
        abstract fun log(url: String, eventData: YLSEventData)
    }

    open class DebugLogger : Logger() {
        override fun log(url: String, eventData: YLSEventData) {

        }
    }

    companion object {
        private lateinit var logger: Logger
        private lateinit var defaultMap: Map<String, Any>
        private lateinit var url: String
        private lateinit var userID: String

        fun init(
            platform: String,
            serviceName: String,
            user: String,
            url: String,
            logger: Logger,
        ) {
            this.defaultMap = mapOf("platform" to platform, "serviceName" to serviceName)
            this.userID = user
            this.url = url
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
            logger.log(url, eventData)
        }

        private fun hashing(id: String) = "asdf"
    }
}
