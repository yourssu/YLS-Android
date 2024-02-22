package com.yourssu.logging.system.remote

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
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
        inputData.getString(KEY_LOGGING_DATA_LIST)?.let { json ->
            val eventDataList = Gson().fromJson<List<YLSEventData>>(
                json,
                object : TypeToken<List<YLSEventData>>() {}.type,
            )
            val response = service?.putLog(eventDataList[0].toLoggingRequest()) ?: return@let Result.failure()

            // 코드에 따른 상세한 처리 필요
            if (response.code() in 200..299) {
                Result.success()
            } else {
                Result.failure()
            }
        } ?: Result.failure()
    }

    companion object {
        const val KEY_LOGGING_DATA_LIST = "logging-data-list-json"
        const val KEY_LOGGING_URL = "logging-url"
    }
}
