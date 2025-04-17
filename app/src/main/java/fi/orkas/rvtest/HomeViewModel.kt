package fi.orkas.rvtest

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import fi.orkas.rvtest.repository.ConfigurationRepository
import fi.orkas.rvtest.repository.DiscoverMovie
import fi.orkas.rvtest.repository.DiscoverRepository
import fi.orkas.rvtest.repository.GenreRepository
import fi.orkas.rvtest.repository.MediaCard
import fi.orkas.rvtest.repository.MovieListRepository
import fi.orkas.rvtest.repository.TvListRepository
import fi.orkas.rvtest.repository.toMediaCard
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class Category(val title: String, val movies: List<MediaCard>)

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
                    movieListRepository.nowPlaying().results.map { it.toMediaCard(details) }
                )
            mutableCategories.value +=
                Category(
                    "TV Airing Now",
                    tvListRepository.airingToday().results.map { it.toMediaCard(details) }
                )
            mutableCategories.value +=
                Category(
                    "Popular",
                    movieListRepository.popular().results.map { it.toMediaCard(details) }
                )
            mutableCategories.value +=
                Category(
                    "Top Rated",
                    movieListRepository.topRated().results.map { it.toMediaCard(details) }
                )
            mutableCategories.value +=
                Category(
                    "Upcoming",
                    movieListRepository.upcoming().results.map { it.toMediaCard(details) }
                )

            genreRepository.movies().genres.map { genre ->
                discoverRepository.movie(
                    DiscoverMovie(withGenres = genre.id.toString())
                )?.results?.let { results ->
                    mutableCategories.value += Category(genre.name, results.map { it.toMediaCard(details) })
                }
            }
        }
    }
}
