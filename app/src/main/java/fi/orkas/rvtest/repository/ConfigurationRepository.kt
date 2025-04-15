package fi.orkas.rvtest.repository

import fi.orkas.rvtest.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.request
import io.ktor.http.Url
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.serialization.Serializable

@Serializable
data class Details(val images: Images, val changeKeys: List<String>)

@Serializable
data class Images(
    val baseUrl: Url,
    val secureBaseUrl: Url,
    val backdropSizes: List<String>,
    val logoSizes: List<String>,
    val posterSizes: List<String>,
    val profileSizes: List<String>,
    val stillSizes: List<String>
)

@Singleton
class ConfigurationRepository @Inject constructor(private val httpClient: HttpClient) {
    suspend fun details(): Details = httpClient.client.request("configuration").body()
}
