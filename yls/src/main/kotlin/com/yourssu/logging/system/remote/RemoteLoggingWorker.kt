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
        val eventDataJson = inputData.getString(KEY_LOGGING_SINGLE_DATA)
        val eventDataListJson = inputData.getString(KEY_LOGGING_DATA_LIST)

        return@withContext when {
            service == null -> Result.failure()

            eventDataJson != null -> {
                val eventData = Gson().fromJson(eventDataJson, YLSEventData::class.java)
                val response = service!!.putLog(eventData.toLoggingRequest())

                if (response.isSuccessful && response.body()?.success == true) {
                    Result.success()
                } else {
                    Result.failure()
                }
            }

            eventDataListJson != null -> {
                val eventDataList = Gson().fromJson<List<YLSEventData>>(
                    eventDataListJson,
                    object : TypeToken<List<YLSEventData>>() {}.type,
                )
                val response = service!!.putLogList(eventDataList.toLogListRequest())

                if (response.isSuccessful && response.body()?.success == true) {
                    Result.success()
                } else {
                    Result.failure()
                }
            }

            else -> Result.failure()
        }
    }

    companion object {
        const val KEY_LOGGING_DATA_LIST = "logging-data-list-json"
        const val KEY_LOGGING_SINGLE_DATA = "loggingk-single-data-json"
        const val KEY_LOGGING_URL = "logging-url"
    }
}
