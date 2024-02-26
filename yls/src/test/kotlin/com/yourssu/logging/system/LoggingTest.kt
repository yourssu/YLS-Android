package com.yourssu.logging.system

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class LoggingTest {

    class TestLogger : YLS.Logger() {
        var lastEventData: YLSEventData? = null

        override fun enqueue(eventData: YLSEventData) {
            lastEventData = eventData
            super.enqueue(eventData)
            println("queue size : ${queue.size}")
        }

        override fun log(eventData: List<YLSEventData>) {
            println("${eventData.size}개 log!")
        }
    }

    lateinit var testLogger: TestLogger

    @Before
    fun ylsInit() {
        testLogger = TestLogger()
        YLS.init(
            platform = "android",
            userId = "abc",
            logger = testLogger,
        )
    }

    @Test
    fun ylsDebugClickLogging() {
        // 버튼 클릭 이벤트
        YLS.version(123).log(
            "event" to "ButtonClicked",
            "screen" to "LoginScreen",
        )

        val e = testLogger.lastEventData
        if (e != null) {
            assertEquals(YLS.hashString("abc"), e.hashedId)
            assertEquals("android", e.event["platform"])
            assertEquals("ButtonClicked", e.event["event"])
            assertEquals("LoginScreen", e.event["screen"])
            assertEquals(123, e.version)
        } else {
            assertTrue(false) // always fail
        }

        // 이후 로깅한 버전은 DEFAULT_VERSION으로 들어간다
        YLS.log()
        val e2 = testLogger.lastEventData
        assertEquals(YLS.DEFAULT_VERSION, e2!!.version)
    }

    @Test
    fun testQueueingLog() {
        /*
         * 출력 결과
         * queue size : 1
         * queue size : 2
         * queue size : 3
         * queue size : 4
         * queue size : 5
         * queue size : 6
         * queue size : 7
         * queue size : 8
         * queue size : 9
         * 10개 log!
         * queue size : 0
         * queue size : 1
         * queue size : 2
         */
        repeat(12) {
            YLS.log("event" to "$it")
        }

        // 함수 테스트
        println(YLS.generateRandomId(10))
        println(YLS.getTimestamp())
        println(YLS.hashString("abc"))
    }
}
