package fi.orkas.rvtest

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.cache.HttpCache
import io.ktor.client.plugins.cache.InvalidCacheStateException
import io.ktor.client.plugins.cache.storage.FileStorage
import io.ktor.client.plugins.compression.ContentEncoding
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.ANDROID
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.LoggingFormat
import io.ktor.client.plugins.resources.Resources
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import io.ktor.util.date.getTimeMillis
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNamingStrategy

@Singleton
@OptIn(ExperimentalSerializationApi::class)
class HttpClient @Inject constructor(@ApplicationContext private val context: Context) {
    val client = HttpClient(CIO) {
        expectSuccess = true
        defaultRequest {
            url("https://api.themoviedb.org/3/")
            header("accept", "application/json")
            header(
                HttpHeaders.Authorization,
                "Bearer ${BuildConfig.API_TOKEN}"
            )
        }
        install(HttpCache) {
            publicStorage(FileStorage(File(context.cacheDir, "api")))
        }
        install(HttpRequestRetry) {
            retryOnExceptionIf { request, exception ->
                exception is InvalidCacheStateException
            }
            modifyRequest {
                if (cause is InvalidCacheStateException) {
                    request.url.encodedParameters.append("refresh", getTimeMillis().toString())
                }
            }
        }
        install(ContentEncoding)
        install(ContentNegotiation) {
            json(
                Json {
                    namingStrategy = JsonNamingStrategy.SnakeCase
                }
            )
        }
        install(Resources)
        install(Logging) {
            logger = Logger.ANDROID
            format = LoggingFormat.OkHttp
            level = LogLevel.INFO
            sanitizeHeader { header -> header == HttpHeaders.Authorization }
        }
    }
}
