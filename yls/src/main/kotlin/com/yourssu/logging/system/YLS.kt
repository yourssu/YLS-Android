package com.yourssu.logging.system

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.google.gson.Gson
import com.yourssu.logging.system.HashUtil.hashId
import com.yourssu.logging.system.worker.RemoteLoggingWorker
import java.text.SimpleDateFormat
import java.util.Date

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
            if (queue.isEmpty()) return
            log(queue)
            queue.clear()
        }

        protected abstract fun log(eventData: List<YLSEventData>)
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
        override fun log(eventData: List<YLSEventData>) {
            Log.d("YLS.DebugLogger", Gson().toJson(eventData))
        }
    }

    companion object {
        private lateinit var logger: Logger
        private lateinit var defaultEvent: Map<String, Any>
        private lateinit var userID: String

        fun init(
            platform: String,
            user: String,
            logger: Logger,
        ) {
            this.defaultEvent = mapOf("platform" to platform)
            this.userID = user
            this.logger = logger
        }

        fun log(vararg events: Pair<String, Any>) {
            if (!::logger.isInitialized) {
                throw AssertionError("Not initialized! : YLS.init()을 먼저 호출해 주세요.")
            }

            val eventData = YLSEventData(
                hashedID = hashId(userID),
                timestamp = getTimestampISO8601(),
                event = defaultEvent + events.toMap(),
            )
            logger.enqueue(eventData)
        }

        fun flush() {
            if (!::logger.isInitialized) {
                throw AssertionError("Not initialized! : YLS.init()을 먼저 호출해 주세요.")
            }
            logger.flush()
        }

        fun generateRandomId(length: Int): String {
            val charset = '!'..'~' // ASCII 33 ~ 126
            return (1..length).map { charset.random() }.joinToString("")
        }

        @SuppressLint("SimpleDateFormat")
        fun getTimestampISO8601(): String =
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").format(Date())
    }
}
