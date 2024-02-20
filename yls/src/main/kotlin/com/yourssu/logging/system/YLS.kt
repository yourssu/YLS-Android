package com.yourssu.logging.system

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.google.gson.Gson
import com.yourssu.logging.system.HashUtil.hashId
import com.yourssu.logging.system.remote.RemoteLoggingWorker
import java.text.SimpleDateFormat
import java.util.Date

/**
 * Yourssu Logging System, inspired by [Timber](https://github.com/JakeWharton/timber)
 *
 * ```kotlin
 * // 사용 예
 * // Application의 onCreate()에서 초기화
 * YLS.init(
 *      platform = "android",
 *      user = YLS.generateRandomId(10), // 10자리 랜덤 문자열
 *      logger = YLS.RemoteLogger() // Logger 객체 지정
 * )
 *
 * // init 때 지정한 Logger 객체를 사용하여 로깅
 * YLS.log("event" to "click")
 * ```
 * @see [com.yourssu.logging.system.LoggingTest]
 */
class YLS private constructor() {

    /**
     * 실질적인 로깅을 수행하는 클래스. [YLS.init]으로 Logger 객체를 등록할 수 있습니다.
     *
     * 기본적으로 10개의 로그 이벤트를 모아두고 한번에 처리하는 로직이 구현되어 있습니다.
     */
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

    /**
     * 로그 데이터를 `url`로 PUT 요청을 보내는 Logger입니다.
     *
     * [RemoteLoggingWorker]를 사용하여 백그라운드에서 http 요청을 처리합니다.
     *
     * @param url 로깅 요청을 보낼 url
     * @param context WorkManager.getInstance()에 필요한 [Context]
     */
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

    /** 디버깅 할 때 사용할 수 있는 Logger입니다. */
    open class DebugLogger : Logger() {
        override fun log(eventData: List<YLSEventData>) {
            Log.d("YLS.DebugLogger", Gson().toJson(eventData))
        }
    }

    companion object {
        private lateinit var logger: Logger
        private lateinit var defaultEvent: Map<String, Any>
        private lateinit var userId: String

        /**
         * YLS 초기화. 앱의 Application.onCreate()에서 초기화하는 것을 권장합니다.
         *
         * @param platform 플랫폼 이름
         * @param user 유저 ID. 비로그인 상태라면 [YLS.generateRandomId]로 임시 ID 생성 권장
         * @param logger 실질적인 로깅 객체. [YLS.Logger]의 서브 타입
         */
        fun init(
            platform: String,
            user: String,
            logger: Logger,
        ) {
            this.defaultEvent = mapOf("platform" to platform)
            this.userId = user
            this.logger = logger
        }
        
        fun setUserId(id: String) {
            this.userId = id
        }

        fun setDefaultEvent(eventMap: Map<String, Any>) {
            this.defaultEvent = eventMap
        }

        fun setLogger(logger: Logger) {
            this.logger = logger
        }

        /**
         * 기본적인 로그 메서드입니다.
         *
         * ```kotlin
         * YLS.log("event" to "click", "screen" to "mypage")
         * ```
         * @param events 이벤트 key-value 쌍
         */
        fun log(vararg events: Pair<String, Any>) {
            if (!::logger.isInitialized) {
                throw AssertionError("Not initialized! : YLS.init()을 먼저 호출해 주세요.")
            }

            val eventData = YLSEventData(
                hashedID = hashId(userId),
                timestamp = getTimestampISO8601(),
                event = defaultEvent + events.toMap(),
            )
            logger.enqueue(eventData)
        }

        /** Logger에 남아있는 로그 데이터를 모두 내보낸 후 큐를 비웁니다. */
        fun flush() {
            if (!::logger.isInitialized) {
                throw AssertionError("Not initialized! : YLS.init()을 먼저 호출해 주세요.")
            }
            logger.flush()
        }

        /**
         * ASCII 33 ~ 126 사이의 문자로 `length`만큼의 길이의 랜덤 문자열을 반환합니다.
         *
         * @return random string with length `length`
         */
        fun generateRandomId(length: Int): String {
            val charset = '!'..'~' // ASCII 33 ~ 126
            return (1..length).map { charset.random() }.joinToString("")
        }

        /**
         * 현재 시각을 ISO 8601 포맷의 문자열을 반환합니다.
         *
         * @return ISO 8601 format string of current
         */
        @SuppressLint("SimpleDateFormat")
        fun getTimestampISO8601(): String =
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").format(Date())
    }
}
