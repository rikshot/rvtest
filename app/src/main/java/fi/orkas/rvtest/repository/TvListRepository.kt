package fi.orkas.rvtest.repository

import fi.orkas.rvtest.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.request
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.serialization.Serializable

@Serializable
data class TvResult(
    val adult: Boolean,
    val backdropPath: String?,
    val firstAirDate: String,
    val genreIds: List<Int>,
    val id: Int,
    val name: String,
    val originCountry: List<String>,
    val originalLanguage: String,
    val originalName: String,
    val overview: String,
    val popularity: Float,
    val posterPath: String?,
    val voteAverage: Float,
    val voteCount: Int
) : IMedia

@Singleton
class TvListRepository @Inject constructor(private val httpClient: HttpClient) {
    val airingToday =
        MovieListPagingSource<TvResult>(20) { page ->
            httpClient.client.request("tv/airing_today?page=$page").body<DiscoverResponse<TvResult>>()
        }
}
