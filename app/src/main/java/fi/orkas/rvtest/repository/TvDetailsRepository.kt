package fi.orkas.rvtest.repository

import fi.orkas.rvtest.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.serialization.Serializable

@Serializable
data class TvDetailsResponse(
    val adult: Boolean,
    val backdropPath: String?,
    val createdBy: List<Creator>,
    val episodeRunTime: List<Int>,
    val firstAirDate: String,
    val genres: List<Genre>,
    val homepage: String,
    val id: Int,
    val inProduction: Boolean,
    val languages: List<String>,
    val lastAirDate: String,
    val lastEpisodeToAir: Episode,
    val name: String,
    val nextEpisodeToAir: Episode?,
    val networks: List<Network>,
    val numberOfEpisodes: Int,
    val numberOfSeasons: Int,
    val originCountry: List<String>,
    val originalLanguage: String,
    val originalName: String,
    val overview: String,
    val popularity: Float,
    val posterPath: String,
    val productionCompanies: List<ProductionCompany>,
    val productionCountries: List<ProductionCountry>,
    val seasons: List<Season>,
    val spokenLanguages: List<Language>,
    val status: String,
    val tagline: String,
    val type: String,
    val voteAverage: Float,
    val voteCount: Int
)

@Serializable
data class Creator(
    val id: Int,
    val creditId: String,
    val name: String,
    val originalName: String,
    val gender: Int,
    val profilePath: String?
)

@Serializable
data class Episode(
    val id: Int,
    val name: String,
    val overview: String,
    val voteAverage: Float,
    val voteCount: Int,
    val airDate: String,
    val episodeNumber: Int,
    val episodeType: String,
    val productionCode: String,
    val runtime: Int?,
    val seasonNumber: Int,
    val showId: Int,
    val stillPath: String?
)

@Serializable
data class Network(val id: Int, val logoPath: String?, val name: String, val originCountry: String)

@Serializable
data class Season(
    val airDate: String?,
    val episodeCount: Int,
    val id: Int,
    val name: String,
    val overview: String,
    val posterPath: String?,
    val seasonNumber: Int,
    val voteAverage: Float
)

@Singleton
class TvDetailsRepository @Inject constructor(private val httpClient: HttpClient) {
    suspend fun details(id: Int): TvDetailsResponse = httpClient.client.get("tv/$id").body()
}
