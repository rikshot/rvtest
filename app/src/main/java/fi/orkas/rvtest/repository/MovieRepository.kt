package fi.orkas.rvtest.repository

import fi.orkas.rvtest.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.request
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.serialization.Serializable

@Serializable
data class NowPlayingResponse(
    val dates: Dates,
    val page: Int,
    val results: List<Result>,
    val totalPages: Int,
    val totalResults: Int
)

@Serializable
data class PopularResponse(val page: Int, val results: List<Result>, val totalPages: Int, val totalResults: Int)

@Serializable
data class TopRatedResponse(val page: Int, val results: List<Result>, val totalPages: Int, val totalResults: Int)

@Serializable
data class UpcomingResponse(
    val dates: Dates,
    val page: Int,
    val results: List<Result>,
    val totalPages: Int,
    val totalResults: Int
)

@Serializable
data class Dates(val maximum: String, val minimum: String)

@Serializable
data class Result(
    val adult: Boolean,
    val backdropPath: String,
    val genreIds: List<Int>,
    val id: Int,
    val originalLanguage: String,
    val originalTitle: String,
    val overview: String,
    val popularity: Float,
    val posterPath: String,
    val releaseDate: String,
    val title: String,
    val video: Boolean,
    val voteAverage: Float,
    val voteCount: Int
)

@Singleton
class MovieRepository @Inject constructor(private val httpClient: HttpClient) {
    suspend fun nowPlaying(): NowPlayingResponse = httpClient.client.request("movie/now_playing").body()
    suspend fun popular(): PopularResponse = httpClient.client.request("movie/popular").body()
    suspend fun topRated(): TopRatedResponse = httpClient.client.request("movie/top_rated").body()
    suspend fun upcoming(): UpcomingResponse = httpClient.client.request("movie/upcoming").body()
}
