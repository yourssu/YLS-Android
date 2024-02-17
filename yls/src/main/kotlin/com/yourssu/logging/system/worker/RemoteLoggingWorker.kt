package com.yourssu.logging.system.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.yourssu.logging.system.HttpService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit

internal class RemoteLoggingWorker(
    appContext: Context,
    params: WorkerParameters,
) : CoroutineWorker(appContext, params) {

    private val service: HttpService? by lazy {
        inputData.getString(KEY_LOGGING_URL)?.let {
            Retrofit.Builder()
                .baseUrl(it)
                .build()
                .create(HttpService::class.java)
        }
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        inputData.getString(KEY_LOGGING_DATA)?.let { json ->
            val response = service?.putLog(json) ?: return@let Result.failure()

            // 코드에 따른 상세한 처리
            if (response.code() in 200..299) {
                Result.success()
            } else {
                Result.failure()
            }
        } ?: Result.failure()
    }

    companion object {
        const val KEY_LOGGING_DATA = "logging-data-json"
        const val KEY_LOGGING_URL = "logging-url"
    }
}
