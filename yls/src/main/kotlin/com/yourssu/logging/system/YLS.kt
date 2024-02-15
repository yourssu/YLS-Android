package com.yourssu.logging.system

class YLS {
    abstract class Logger

    open class DebugLogger : Logger()

    companion object {
        fun init(
            platform: String,
            serviceName: String,
            user: String,
            url: String,
            logger: Logger,
        ) {

        }

        fun randomId(): String = "aaa"

        fun log(vararg events: Pair<String, Any>) {

        }
    }
}
