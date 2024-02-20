package com.yourssu.logging.system

import android.content.Context
import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.work.Configuration
import androidx.work.WorkManager
import androidx.work.impl.utils.SynchronousExecutor
import androidx.work.testing.TestListenableWorkerBuilder
import androidx.work.testing.WorkManagerTestInitHelper
import androidx.work.workDataOf
import com.yourssu.logging.system.remote.RemoteLoggingWorker
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
class WorkTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.yourssu.logging.system.test", appContext.packageName)
    }

    private lateinit var workManager: WorkManager
    private lateinit var context: Context
    private lateinit var configuration: Configuration

    @Before
    fun setup() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        configuration = Configuration.Builder()
            .setMinimumLoggingLevel(Log.DEBUG)
            .setExecutor(SynchronousExecutor())
            .build()

        // Initialize WorkManager for instrumentation tests.
        WorkManagerTestInitHelper.initializeTestWorkManager(context, configuration)
        workManager = WorkManager.getInstance(context)
    }

    @Test
    fun testLoggingWorker() {
        val json = """
            {
                "hashedID": "aaaabbbbccccddddaaaabbbbccccdddd",
                "timestamp": "2023-12-04T10:30:00Z",
                "event": {
                    "platform": "android",
                    "event": "AppInitialEntry"
                }
            }
            """.trimIndent()
        val worker = TestListenableWorkerBuilder<RemoteLoggingWorker>(
            context = context,
            inputData = workDataOf(
                RemoteLoggingWorker.KEY_LOGGING_URL to "url",
                RemoteLoggingWorker.KEY_LOGGING_DATA to json,
            ),
        ).build()

        // Start the work synchronously
        val future = worker.startWork()
        val result = future.get()

        println(result.toString())
//        assertEquals(result.toString(), )
    }
}
