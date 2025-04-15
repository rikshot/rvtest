package fi.orkas.rvtest.repository

import fi.orkas.rvtest.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.request
import io.ktor.resources.Resource
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

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
class DiscoverMovie constructor(
    val certification: String? = null,
    @JsonNames("certification.gte") val certificationGte: String? = null,
    @JsonNames("certification.lte") val certificationLte: String? = null,
    val certificationCountry: String? = null,
    val includeAdult: Boolean? = false,
    val includeVideo: Boolean? = false,
    val language: String? = "en-US",
    val page: Int? = 1,
    val primaryReleaseYear: Int? = null,
    val primaryReleaseDateGte: String? = null,
    val primaryReleaseDateLte: String? = null,
    val region: String? = null,
    val releaseDateGte: String? = null,
    val releaseDateLte: String? = null,
    val sortBy: String? = "popularity.desc",
    val voteAverageGte: Float? = null,
    val voteAverageLte: Float? = null,
    val voteCountGte: Float? = null,
    val voteCountLte: FLoat?= null,
    val watchRegion: String? = null,
    val withCast: String? = null,

val withCompanies: String? = null,
val withCrew: String? = null,
val withGenres: String? = null,
val with_keywords
    string

    can be a comma (AND) or pipe (OR) separated query
val with_origin_country
    string
val with_original_language
    string
val with_people
    string

    can be a comma (AND) or pipe (OR) separated query
val with_release_type
    int32

    possible values are: [1, 2, 3, 4, 5, 6] can be a comma (AND) or pipe (OR) separated query, can be used in conjunction with region
val with_runtime.gte
    int32
val with_runtime.lte
    int32
val with_watch_monetization_types
    string

    possible values are: [flatrate, free, ads, rent, buy] use in conjunction with watch_region, can be a comma (AND) or pipe (OR) separated query
val with_watch_providers
    string

    use in conjunction with watch_region, can be a comma (AND) or pipe (OR) separated query
val without_companies
    string
val without_genres
    string
val without_keywords
    string
val without_watch_providers
    string
val year
    int32
)

@Singleton
class DiscoverRepository @Inject constructor(private val httpClient: HttpClient) {
    suspend fun movie(): DiscoverResponse = httpClient.client.request("discover/movie").body()
}
