package fi.orkas.rvtest.repository

import androidx.core.net.toUri
import fi.orkas.rvtest.DetailsRoute
import fi.orkas.rvtest.DetailsType
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
) : IMedia {
    override fun toMediaCard(details: Details): MediaCard = MediaCard(
        id,
        name,
        posterPath?.let { posterPath ->
            "${details.images.secureBaseUrl}${details.images.posterSizes[0]}$posterPath".toUri()
        },
        posterPath?.let { posterPath ->
            "${details.images.secureBaseUrl}${details.images.posterSizes[3]}$posterPath".toUri()
        },
        onClick = { navController ->
            navController.navigate(DetailsRoute(id, DetailsType.TV))
        }
    )
}

@Singleton
class TvListRepository @Inject constructor(private val httpClient: HttpClient) {
    val airingToday =
        MovieListPagingSource<TvResult>(20) { page ->
            httpClient.client.request("tv/airing_today?page=$page").body<DiscoverResponse<TvResult>>()
        }
    val onTheAir =
        MovieListPagingSource<TvResult>(20) { page ->
            httpClient.client.request("tv/on_the_air?page=$page").body<DiscoverResponse<TvResult>>()
        }
    val popular =
        MovieListPagingSource<TvResult>(20) { page ->
            httpClient.client.request("tv/popular?page=$page").body<DiscoverResponse<TvResult>>()
        }
    val topRated =
        MovieListPagingSource<TvResult>(20) { page ->
            httpClient.client.request("tv/top_rated?page=$page").body<DiscoverResponse<TvResult>>()
        }
}
