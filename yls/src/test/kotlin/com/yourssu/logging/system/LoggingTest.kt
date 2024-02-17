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
        }

        override fun log(eventData: List<YLSEventData>) = Unit
    }

    lateinit var testLogger: TestLogger

    @Before
    fun ylsInit() {
        testLogger = TestLogger()
        YLS.init(
            platform = "android",
            user = "abc",
            logger = testLogger,
        )
    }

    @Test
    fun ylsDebugClickLogging() {
        // 버튼 클릭 이벤트
        YLS.log(
            "event" to "ButtonClicked",
            "screen" to "LoginScreen",
        )

        testLogger.lastEventData?.let {
            assertEquals("abc".hashString("SHA-256"), it.hashedID)
            assertEquals("android", it.event["platform"])
            assertEquals("Soomsil", it.event["serviceName"])
            assertEquals("ButtonClicked", it.event["event"])
            assertEquals("LoginScreen", it.event["screen"])
        } ?: assertTrue(false) // if null, then fail
    }
}
