package fi.orkas.rvtest.repository

import fi.orkas.rvtest.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.request
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.serialization.Serializable

@Serializable
data class GenreResponse(val genres: List<Genre>)

@Serializable
data class Genre(val id: Int, val name: String)

@Singleton
class GenreRepository @Inject constructor(private val httpClient: HttpClient) {
    suspend fun movies(): GenreResponse = httpClient.client.request("genre/movie/list").body()
}
