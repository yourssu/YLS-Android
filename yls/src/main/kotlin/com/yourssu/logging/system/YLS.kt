package com.yourssu.logging.system

import android.content.Context
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.google.gson.Gson
import com.yourssu.logging.system.worker.RemoteLoggingWorker
import java.time.LocalDateTime
import java.time.ZoneOffset

class YLS private constructor() {
    abstract class Logger {
        protected val queue = ArrayList<YLSEventData>()

        open fun enqueue(eventData: YLSEventData) {
            if (queue.isNotEmpty() && queue.last() == eventData) {
                return
            }
            queue.add(eventData)

            if (queue.size >= 10) {
                flush()
            }
        }

        open fun flush() {
            log(queue)
            queue.clear()
        }

        abstract fun log(eventData: List<YLSEventData>)
    }

    open class RemoteLogger(private val url: String, context: Context) : Logger() {

        protected val worker: WorkManager = WorkManager.getInstance(context)

        override fun log(eventData: List<YLSEventData>) {
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

    open class DebugLogger : Logger() {
        var lastlyEventedData: String? = null

        override fun log(eventData: List<YLSEventData>) {
            lastlyEventedData = Gson().toJson(eventData)
            println("YLS.DebugLogger : $lastlyEventedData")
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
            if (!::logger.isInitialized) throw AssertionError("Not Initialized!")

            val eventData = YLSEventData(
                hashedID = hashing(userID),
                timestamp = getTimestampISO8601(),
                event = events.toMap() + defaultMap,
            )
            logger.enqueue(eventData)
        }

        fun flush() {
            logger.flush()
        }

        private fun hashing(id: String) = "asdf"

        fun getTimestampISO8601(): String = if (VERSION.SDK_INT >= VERSION_CODES.O) {
            LocalDateTime.now().atZone(ZoneOffset.UTC).toString()
        } else {
            ""
        }
    }
}
