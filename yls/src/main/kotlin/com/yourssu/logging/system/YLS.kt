package com.yourssu.logging.system

import android.content.Context
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.google.gson.Gson
import com.yourssu.logging.system.worker.RemoteLoggingWorker

class YLS private constructor() {
    abstract class Logger {
        abstract fun log(eventData: YLSEventData)
    }

    open class RemoteLogger(private val url: String, context: Context) : Logger() {

        protected val worker: WorkManager = WorkManager.getInstance(context)

        override fun log(eventData: YLSEventData) {
            val json = Gson().toJson(eventData)
            val request = OneTimeWorkRequestBuilder<RemoteLoggingWorker>()
                .setInputData(
                    workDataOf(
                        RemoteLoggingWorker.KEY_LOGGING_DATA to json,
                        RemoteLoggingWorker.KEY_LOGGING_URL to url,
                    ),
                ).build()
            worker.enqueue(request)
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
