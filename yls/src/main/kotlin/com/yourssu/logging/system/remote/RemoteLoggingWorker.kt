package com.yourssu.logging.system.remote

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.gson.Gson
import com.yourssu.logging.system.YLSEventData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

internal class RemoteLoggingWorker(
    appContext: Context,
    params: WorkerParameters,
) : CoroutineWorker(appContext, params) {

    private val service: LoggingService? by lazy {
        inputData.getString(KEY_LOGGING_URL)?.let {
            Retrofit.Builder()
                .baseUrl(it)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(LoggingService::class.java)
        }
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        inputData.getString(KEY_LOGGING_DATA)?.let { json ->
            val eventData = Gson().fromJson(json, YLSEventData::class.java)
            val response = service?.putLog(eventData.toLoggingRequest()) ?: return@let Result.failure()

            // 코드에 따른 상세한 처리 필요
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
