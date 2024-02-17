package com.yourssu.logging.system

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class LoggingTest {

    @Test
    fun ylsDebugClickLogging() {
        val logger = YLS.DebugLogger()

        // YLS.log() 전에 init 해줘야 한다
        YLS.init(
            platform = "android",
            serviceName = "Soomsil",
            user = YLS.randomId(),
            logger = logger,
        )

        // 버튼 클릭 이벤트
        YLS.log(
            "event" to "ButtonClicked",
            "screen" to "LoginScreen",
        )
        
        // 로깅된 값 확인
        assertEquals(
            """
                {"hashedID":"asdf","timestamp":"","event":{"event":"ButtonClicked","screen":"LoginScreen","platform":"android","serviceName":"Soomsil"}}
            """.trimIndent(),
            logger.lastlyEventedData,
        )
    }
}
