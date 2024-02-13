package com.yourssu.logging.system

import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody

class YLS {
    abstract class Logger {
        protected abstract fun log(eventData: YLSEventData)
    }

    open class DefaultLogger(
        val url: String,
        val platform: String,       // ex: android
        val serviceName: String,    // ex: Soomsil
        val queueCapacity: Int = 10,
    ) : Logger() {
        private val client = OkHttpClient()
        private val cache = ArrayList<YLSEventData>(queueCapacity)

        override fun log(eventData: YLSEventData) {
            if (cache.isNotEmpty() && cache.last() == eventData) {
                return
            }
            cache.add(eventData)

            // todo 디테일하게 할 필요가 있다
            if (cache.size >= queueCapacity) {
                val json = "[" + cache.joinToString(", ") { it.toJsonString() } + "]"
                val request = Request.Builder()
                    .url(url)
                    .put(RequestBody.create(MediaType.parse("application/json"), json))
                    .build()
                client.newCall(request).execute()
                cache.clear()
            }
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
