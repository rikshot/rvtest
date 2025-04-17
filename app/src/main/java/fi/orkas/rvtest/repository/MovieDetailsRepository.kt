package fi.orkas.rvtest.repository

import fi.orkas.rvtest.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.request
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class DetailsResponse(
    val adult: Boolean,
    @JsonNames("backdrop_path") val backdropPath: String,
    @JsonNames("belongs_to_collection") val belongsToCollection: String,
    val budget: Int,
    val genres: List<Genre>,
    val homepage: String,
    val id: Int,
    @JsonNames("imdb_id") val imdbId: String,
    @JsonNames("original_language") val originalLanguage: String,
    @JsonNames("original_title") val originalTitle: String,
    val overview: String,
    val popularity: Float,
    @JsonNames("poster_path") val posterPath: String,
    @JsonNames("production_companies") val productionCompanies: List<ProductionCompany>,
    @JsonNames("production_countries") val productionCountries: List<ProductionCountry>,
    @JsonNames("release_date") val releaseDate: String,
    val revenue: Int,
    val runtime: Int,
    @JsonNames("spoken_languages") val spokenLanguages: List<Language>,
    val status: String,
    val tagline: String,
    val title: String,
    val video: Boolean,
    @JsonNames("vote_average") val voteAverage: Float,
    @JsonNames("vote_count") val voteCount: Int
)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class ProductionCompany(
    val id: Int,
    @JsonNames("logo_path") val logoPath: String,
    val name: String,
    @JsonNames("origin_country") val originCountry: String
)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class ProductionCountry(@JsonNames("iso_3166_1") val iso31661: String, val name: String)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class Language(
    @JsonNames("english_name") val englishName: String,
    @JsonNames("iso_639_1") val iso6391: String,
    val name: String
)

@Singleton
class MovieDetailsRepository @Inject constructor(private val httpClient: HttpClient) {
    suspend fun details(id: Int): DetailsResponse? =
        runCatching { httpClient.client.request("movie/$id").body<DetailsResponse>() }.getOrNull()
}
