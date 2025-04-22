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
import fi.orkas.rvtest.repository.DiscoverMovie
import fi.orkas.rvtest.repository.DiscoverRepository
import fi.orkas.rvtest.repository.GenreRepository
import fi.orkas.rvtest.repository.MediaCard
import fi.orkas.rvtest.repository.MovieListRepository
import fi.orkas.rvtest.repository.MovieResult
import fi.orkas.rvtest.repository.TvListRepository
import fi.orkas.rvtest.repository.TvResult
import fi.orkas.rvtest.repository.toMediaCard
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

data class Category(val title: String, val flow: Flow<PagingData<MediaCard>>)

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

    init {
        viewModelScope.launch {
            val details = configurationRepository.details()

            mutableCategories.value +=
                Category(
                    "Now Playing",
                    Pager(PagingConfig(20)) {
                        movieListRepository.nowPlaying
                    }.flow.map { data: PagingData<MovieResult> ->
                        data.map { result: MovieResult ->
                            result.toMediaCard(details)
                        }
                    }.cachedIn(viewModelScope)
                )
            mutableCategories.value +=
                Category(
                    "TV Airing Today",
                    Pager(PagingConfig(20)) {
                        tvListRepository.airingToday
                    }.flow.map { data: PagingData<TvResult> ->
                        data.map { result: TvResult ->
                            result.toMediaCard(details)
                        }
                    }.cachedIn(viewModelScope)
                )
            mutableCategories.value +=
                Category(
                    "Popular",
                    Pager(PagingConfig(20)) {
                        movieListRepository.popular
                    }.flow.map { data: PagingData<MovieResult> ->
                        data.map { result: MovieResult ->
                            result.toMediaCard(details)
                        }
                    }.cachedIn(viewModelScope)
                )
            mutableCategories.value +=
                Category(
                    "Top Rated",
                    Pager(PagingConfig(20)) {
                        movieListRepository.topRated
                    }.flow.map { data: PagingData<MovieResult> ->
                        data.map { result: MovieResult ->
                            result.toMediaCard(details)
                        }
                    }.cachedIn(viewModelScope)
                )
            mutableCategories.value +=
                Category(
                    "Upcoming",
                    Pager(PagingConfig(20)) {
                        movieListRepository.upcoming
                    }.flow.map { data: PagingData<MovieResult> ->
                        data.map { result: MovieResult ->
                            result.toMediaCard(details)
                        }
                    }.cachedIn(viewModelScope)
                )

            genreRepository.movies().genres.forEach { genre ->
                mutableCategories.value += Category(
                    genre.name,
                    Pager(PagingConfig(20)) {
                        discoverRepository.movie(
                            DiscoverMovie(withGenres = genre.id.toString())
                        )
                    }.flow.map { data: PagingData<MovieResult> ->
                        data.map { result: MovieResult ->
                            result.toMediaCard(details)
                        }
                    }.cachedIn(viewModelScope)
                )
            }
        }
    }
}
