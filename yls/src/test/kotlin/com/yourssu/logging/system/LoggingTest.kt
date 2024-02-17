package com.yourssu.logging.system

import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class LoggingTest {
    @Test
    fun yls_init() {
        YLS.init(
            platform = "android",
            serviceName = "Soomsil",
            user = YLS.randomId(),
            logger = YLS.RemoteLogger(url = "https://example.com"),
        )
    }

    @Test
    fun yls_default_logging() {
        // 버튼 클릭
        YLS.log(
            "event" to "ButtonClicked",
            "screen" to "LoginScreen",
        )
    }
}
