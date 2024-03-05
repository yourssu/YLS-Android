package com.yourssu.logging.system

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.work.testing.TestListenableWorkerBuilder
import androidx.work.workDataOf
import com.google.gson.Gson
import com.yourssu.logging.system.remote.RemoteLoggingWorker
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class WorkerTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.yourssu.logging.system.test", appContext.packageName)
    }

    private lateinit var context: Context

    @Before
    fun setup() {
        // WorkManager 테스트를 위한 셋업
        context = ApplicationProvider.getApplicationContext()
    }

    @Test
    fun testLoggingWorker_single_log() {
        val eventData = YLSEventData(
            hashedId = "aaaabbbbccccddddaaaabbbbccccdddd",
            timestamp = "2023-12-04T10:30:00Z",
            version = 1,
            event = mapOf("platform" to "android", "event" to "AppInitialEntry"),
        )

        val json = Gson().toJson(eventData)

        val worker = TestListenableWorkerBuilder<RemoteLoggingWorker>(
            context = context,
            inputData = workDataOf(
                RemoteLoggingWorker.KEY_LOGGING_URL to "your api url",
                RemoteLoggingWorker.KEY_LOGGING_SINGLE_DATA to json,
            ),
        ).build()

        runBlocking {
            val result = worker.doWork()
            assertEquals(result.toString(), "Success {mOutputData=Data {}}")
        }
    }

    @Test
    fun testLoggingWorker_log_list() {
        val eventData = listOf(
            YLSEventData(
                hashedId = "aaaabbbbccccddddaaaabbbbccccdddd",
                timestamp = "2023-12-04T10:30:00Z",
                version = 1,
                event = mapOf("platform" to "android", "event" to "AppInitialEntry"),
            ),
        )

        val json = Gson().toJson(eventData)
        val worker = TestListenableWorkerBuilder<RemoteLoggingWorker>(
            context = context,
            inputData = workDataOf(
                RemoteLoggingWorker.KEY_LOGGING_URL to "http://52.78.169.59:8085/",
                RemoteLoggingWorker.KEY_LOGGING_DATA_LIST to json,
            ),
        ).build()

        runBlocking {
            val result = worker.doWork()
            assertEquals(result.toString(), "Success {mOutputData=Data {}}") // Failure
        }
    }
}
