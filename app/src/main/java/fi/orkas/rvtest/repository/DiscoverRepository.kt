package fi.orkas.rvtest.repository

import fi.orkas.rvtest.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.resources.get
import io.ktor.resources.Resource
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DiscoverResponse(val page: Int, val results: List<Result>, val totalPages: Int, val totalResults: Int)

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

@OptIn(ExperimentalSerializationApi::class)
@Resource("discover/movie")
data class DiscoverMovie(
    val certification: String? = null,
    @SerialName("certification.gte") val certificationGte: String? = null,
    @SerialName("certification.lte") val certificationLte: String? = null,
    @SerialName("certification_country") val certificationCountry: String? = null,
    @SerialName("include_adult") val includeAdult: Boolean? = null,
    @SerialName("include_video") val includeVideo: Boolean? = null,
    val language: String? = null,
    val page: Int? = null,
    @SerialName("primary_release_year") val primaryReleaseYear: Int? = null,
    @SerialName("primary_release_date.gte") val primaryReleaseDateGte: String? = null,
    @SerialName("primary_release_date.lte") val primaryReleaseDateLte: String? = null,
    val region: String? = null,
    @SerialName("release_date.gte") val releaseDateGte: String? = null,
    @SerialName("release_date.lte") val releaseDateLte: String? = null,
    @SerialName("sort_by") val sortBy: String? = null,
    @SerialName("vote_average.gte") val voteAverageGte: Float? = null,
    @SerialName("vote_average.lte") val voteAverageLte: Float? = null,
    @SerialName("vote_count.gte") val voteCountGte: Float? = null,
    @SerialName("vote_count.lte") val voteCountLte: Float? = null,
    @SerialName("watch_region") val watchRegion: String? = null,
    @SerialName("with_cast") val withCast: String? = null,
    @SerialName("with_companies") val withCompanies: String? = null,
    @SerialName("with_crew") val withCrew: String? = null,
    @SerialName("with_genres") val withGenres: String? = null,
    @SerialName("with_keywords") val withKeywords: String? = null,
    @SerialName("with_origin_country") val withOriginCountry: String? = null,
    @SerialName("with_original_language") val withOriginalLanguage: String? = null,
    @SerialName("with_people") val withPeople: String? = null,
    @SerialName("with_release_type") val withReleaseType: Int? = null,
    @SerialName("with_runtime.gte") val withRuntimeGte: Int? = null,
    @SerialName("with_runtime.lte") val withRuntimeLte: Int? = null,
    @SerialName("with_watch_monetization_types") val withWatchMonetizationTypes: String? = null,
    @SerialName("with_watch_providers") val withWatchProviders: String? = null,
    @SerialName("without_companies") val withoutCompanies: String? = null,
    @SerialName("without_genres") val withoutGenres: String? = null,
    @SerialName("without_keywords") val withoutKeywords: String? = null,
    @SerialName("without_watch_providers") val withoutWatchProviders: String? = null,
    val year: Int? = null
)

@Singleton
class DiscoverRepository @Inject constructor(private val httpClient: HttpClient) {
    suspend fun movie(query: DiscoverMovie): DiscoverResponse? = runCatching {
        httpClient.client.get(query).body<DiscoverResponse>()
    }.getOrNull()
}
