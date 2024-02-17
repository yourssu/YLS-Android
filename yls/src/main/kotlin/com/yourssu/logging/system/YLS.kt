package com.yourssu.logging.system

import android.content.Context
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.google.gson.Gson
import com.yourssu.logging.system.worker.RemoteLoggingWorker
import java.lang.StringBuilder
import java.security.MessageDigest
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

        fun log(vararg events: Pair<String, Any>) {
            if (!::logger.isInitialized) throw AssertionError("Not Initialized!")

            val eventData = YLSEventData(
                hashedID = hashId(userID),
                timestamp = getTimestampISO8601(),
                event = events.toMap() + defaultMap,
            )
            logger.enqueue(eventData)
        }

        fun flush() {
            logger.flush()
        }

        fun generateRandomId(length: Int): String {
            val charset = '!'..'~' // ASCII 33 ~ 126
            return (1..length).map { charset.random() }.joinToString()
        }

        // hashId() 메서드에서만 사용되는 변수
        private var _id: String? = null
        private var _hashedId: String? = null

        private fun hashId(id: String): String {
            if (_id == id && _hashedId != null) {
                return _hashedId!!
            }
            return id.hashString("SHA-256").also {
                _id = id
                _hashedId = it
            }
        }

        fun getTimestampISO8601(): String = if (VERSION.SDK_INT >= VERSION_CODES.O) {
            LocalDateTime.now().atZone(ZoneOffset.UTC).toString()
        } else {
            ""
        }
    }
}

internal fun String.hashString(algorithm: String): String {
    return MessageDigest.getInstance(algorithm)
        .digest(this.toByteArray())
        .let { bytes ->
            bytes.fold(StringBuilder(bytes.size * 2)) { str, it ->
                str.append("%02x".format(it))
            }
        }.toString()
}
