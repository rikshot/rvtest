package fi.orkas.rvtest

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import dagger.hilt.android.lifecycle.HiltViewModel
import fi.orkas.rvtest.repository.ConfigurationRepository
import fi.orkas.rvtest.repository.Details
import fi.orkas.rvtest.repository.DiscoverMovie
import fi.orkas.rvtest.repository.DiscoverRepository
import fi.orkas.rvtest.repository.GenreRepository
import fi.orkas.rvtest.repository.IMedia
import fi.orkas.rvtest.repository.MediaCard
import fi.orkas.rvtest.repository.MovieListPagingSource
import fi.orkas.rvtest.repository.MovieListRepository
import fi.orkas.rvtest.repository.TvListRepository
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

data class Category(val title: String, val flow: Flow<PagingData<MediaCard>>)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val configurationRepository: ConfigurationRepository,
    private val discoverRepository: DiscoverRepository,
    private val genreRepository: GenreRepository,
    private val movieListRepository: MovieListRepository,
    private val tvListRepository: TvListRepository
) : ViewModel() {
    private val mutableCategories = MutableStateFlow<List<Category>>(listOf())
    val categories = mutableCategories.asStateFlow()

    private fun createCategory(
        details: Details,
        title: String,
        pagingSource: MovieListPagingSource<out IMedia>
    ): Category = Category(
        title,
        Pager(PagingConfig(20)) {
            pagingSource
        }.flow.map { data ->
            data.map { result: IMedia ->
                result.toMediaCard(details)
            }
        }.cachedIn(viewModelScope)
    )

    init {
        viewModelScope.launch {
            val details = configurationRepository.details()

            mutableCategories.value += createCategory(details, "Now Playing", movieListRepository.nowPlaying)
            mutableCategories.value += createCategory(details, "TV Airing Today", tvListRepository.airingToday)
            mutableCategories.value += createCategory(details, "Popular", movieListRepository.popular)
            mutableCategories.value += createCategory(details, "TV On The Air", tvListRepository.onTheAir)
            mutableCategories.value += createCategory(details, "Top Rated", movieListRepository.topRated)
            mutableCategories.value += createCategory(details, "TV Popular", tvListRepository.popular)
            mutableCategories.value += createCategory(details, "Upcoming", movieListRepository.upcoming)
            mutableCategories.value += createCategory(details, "TV Top Rated", tvListRepository.topRated)

            genreRepository.movies().genres.forEach { genre ->
                mutableCategories.value += createCategory(
                    details,
                    genre.name,
                    discoverRepository.movie(
                        DiscoverMovie(withGenres = genre.id.toString())
                    )
                )
            }
        }
    }
}
