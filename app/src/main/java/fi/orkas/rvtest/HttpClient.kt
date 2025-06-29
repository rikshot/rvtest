package fi.orkas.rvtest

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
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
import java.io.File
import java.security.cert.X509Certificate
import javax.inject.Inject
import javax.inject.Singleton
import javax.net.ssl.X509TrustManager
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNamingStrategy

@Singleton
@OptIn(ExperimentalSerializationApi::class)
class HttpClient @Inject constructor(@ApplicationContext private val context: Context, engine: HttpClientEngine) {
    val client = HttpClient(engine) {
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
                    request.headers.append("accept-encoding", "")
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

@Module
@InstallIn(SingletonComponent::class)
object HttpClientEngineModule {
    @Provides
    fun httpClientEngine(): HttpClientEngine = CIO.create {
        dispatcher = Dispatchers.IO.limitedParallelism(Runtime.getRuntime().availableProcessors())
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1) {
            https {
                trustManager =
                    @SuppressLint("CustomX509TrustManager")
                    object : X509TrustManager {
                        @SuppressLint("TrustAllX509TrustManager")
                        override fun checkClientTrusted(chain: Array<out X509Certificate?>?, authType: String?) {
                        }

                        @SuppressLint("TrustAllX509TrustManager")
                        override fun checkServerTrusted(chain: Array<out X509Certificate?>?, authType: String?) {
                        }

                        override fun getAcceptedIssuers(): Array<out X509Certificate?>? = null
                    }
            }
        }
    }
}
