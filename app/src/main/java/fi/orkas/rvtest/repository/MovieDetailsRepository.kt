package fi.orkas.rvtest.repository

import fi.orkas.rvtest.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@Serializable
data class MovieDetailsResponse(
    val adult: Boolean,
    val backdropPath: String?,
    val belongsToCollection: Collection?,
    val budget: Int,
    val genres: List<Genre>,
    val homepage: String,
    val id: Int,
    val imdbId: String?,
    val originCountry: List<String>,
    val originalLanguage: String,
    val originalTitle: String,
    val overview: String,
    val popularity: Float,
    val posterPath: String,
    val productionCompanies: List<ProductionCompany>,
    val productionCountries: List<ProductionCountry>,
    val releaseDate: String,
    val revenue: Long,
    val runtime: Int,
    val spokenLanguages: List<Language>,
    val status: String,
    val tagline: String,
    val title: String,
    val video: Boolean,
    val voteAverage: Float,
    val voteCount: Int
)

@Serializable
data class Collection(val id: Int, val name: String, val posterPath: String, val backdropPath: String?)

@Serializable
data class ProductionCompany(val id: Int, val logoPath: String?, val name: String, val originCountry: String)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class ProductionCountry(@JsonNames("iso_3166_1") val iso31661: String, val name: String)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class Language(val englishName: String, @JsonNames("iso_639_1") val iso6391: String, val name: String)

@Singleton
class MovieDetailsRepository @Inject constructor(private val httpClient: HttpClient) {
    suspend fun details(id: Int): MovieDetailsResponse = httpClient.client.get("movie/$id").body()
}
