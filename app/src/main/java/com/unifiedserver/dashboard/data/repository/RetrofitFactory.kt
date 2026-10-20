package com.unifiedserver.dashboard.data.repository

import com.unifiedserver.dashboard.data.ApiService
import com.unifiedserver.dashboard.data.PreferencesManager
import com.unifiedserver.dashboard.data.models.ApiResponse
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.first
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit

fun normalizeServerUrl(value: String): String {
    val url = value.trim().toHttpUrlOrNull()
        ?: throw IllegalArgumentException("Enter a complete http:// or https:// server URL.")
    require(url.username.isEmpty() && url.password.isEmpty()) { "Do not put credentials in the server URL." }
    require(url.query == null && url.fragment == null) { "Server URL must not contain a query or fragment." }
    return url.toString().trimEnd('/') + "/"
}

/** Shared connection pool; no request/response bodies or credentials are logged. */
object BackendClient {
    val httpClient: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .callTimeout(45, TimeUnit.SECONDS)
        .build()

    private var currentUrl: String? = null
    private var currentService: ApiService? = null

    @Synchronized
    fun forUrl(value: String): ApiService {
        val url = normalizeServerUrl(value)
        if (url != currentUrl) {
            currentService = Retrofit.Builder()
                .baseUrl(url)
                .client(httpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiService::class.java)
            currentUrl = url
        }
        return checkNotNull(currentService)
    }
}

suspend fun buildApiService(preferencesManager: PreferencesManager): ApiService =
    BackendClient.forUrl(preferencesManager.serverUrl.first())

/** Cancellation belongs to the caller, not to a user-visible error state. */
internal inline fun <T> apiResult(block: () -> T): Result<T> = try {
    Result.success(block())
} catch (cancelled: CancellationException) {
    throw cancelled
} catch (error: Exception) {
    Result.failure(error)
}

internal fun <T> Response<ApiResponse<T>>.requireSuccess() {
    if (!isSuccessful) throw IOException("Server request failed (HTTP ${code()}).")
    if (code() == 204) return
    val payload = body() ?: throw IOException("Server returned an empty response.")
    if (!payload.success || payload.error != null) {
        throw IOException(payload.message ?: payload.error ?: "The server rejected this action.")
    }
}
