package fi.orkas.rvtest.repository

import fi.orkas.rvtest.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.request
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.serialization.Serializable

@Serializable
data class DatedDiscoveryResponse(
    val dates: Dates,
    val page: Int,
    val results: List<Result>,
    val totalPages: Int,
    val totalResults: Int
)

@Serializable
data class Dates(val maximum: String, val minimum: String)

@Singleton
class MovieListRepository @Inject constructor(private val httpClient: HttpClient) {
    suspend fun nowPlaying(): DatedDiscoveryResponse = httpClient.client.request("movie/now_playing").body()
    suspend fun popular(): DiscoverResponse = httpClient.client.request("movie/popular").body()
    suspend fun topRated(): DiscoverResponse = httpClient.client.request("movie/top_rated").body()
    suspend fun upcoming(): DatedDiscoveryResponse = httpClient.client.request("movie/upcoming").body()
}
