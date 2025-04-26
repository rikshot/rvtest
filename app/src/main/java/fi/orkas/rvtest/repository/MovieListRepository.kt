package fi.orkas.rvtest.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import fi.orkas.rvtest.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.serialization.Serializable

@Serializable
data class DatedDiscoveryResponse<T : Any>(
    val dates: Dates,
    override val page: Int,
    override val results: List<T>,
    override val totalPages: Int,
    override val totalResults: Int
) : PagedResponse<T>()

@Serializable
data class Dates(val maximum: String, val minimum: String)

@Singleton
class MovieListRepository @Inject constructor(private val httpClient: HttpClient) {
    val nowPlaying =
        MovieListPagingSource<MovieResult> { page ->
            httpClient.client.get("movie/now_playing?page=$page").body<DatedDiscoveryResponse<MovieResult>>()
        }
    val popular =
        MovieListPagingSource<MovieResult> { page ->
            httpClient.client.get("movie/popular?page=$page").body<DiscoverResponse<MovieResult>>()
        }
    val topRated =
        MovieListPagingSource<MovieResult> { page ->
            httpClient.client.get("movie/top_rated?page=$page").body<DiscoverResponse<MovieResult>>()
        }
    val upcoming =
        MovieListPagingSource<MovieResult> { page ->
            httpClient.client.get("movie/upcoming?page=$page").body<DatedDiscoveryResponse<MovieResult>>()
        }
}

class MovieListPagingSource<T : Any>(private val request: suspend (Int) -> PagedResponse<T>) : PagingSource<Int, T>() {
    override val keyReuseSupported: Boolean
        get() = true

    override fun getRefreshKey(state: PagingState<Int, T>): Int? = state.anchorPosition?.let {
        state.closestPageToPosition(it)?.prevKey?.plus(1)
            ?: state.closestPageToPosition(it)?.nextKey?.minus(1)
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, T> {
        val pageNumber = params.key ?: 1
        val response = request(pageNumber)
        val previousPage = if (pageNumber > 1) pageNumber - 1 else null
        val nextPage = if (pageNumber < response.totalPages) pageNumber + 1 else null
        return LoadResult.Page(
            data = response.results,
            prevKey = previousPage,
            nextKey = nextPage
        )
    }
}
