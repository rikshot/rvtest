package fi.orkas.rvtest

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import fi.orkas.rvtest.repository.ConfigurationRepository
import fi.orkas.rvtest.repository.Details
import fi.orkas.rvtest.repository.DiscoverMovie
import fi.orkas.rvtest.repository.DiscoverRepository
import fi.orkas.rvtest.repository.GenreRepository
import fi.orkas.rvtest.repository.MovieListRepository
import fi.orkas.rvtest.repository.Result
import javax.inject.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class Category(val title: String, val movies: List<Result>)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val configurationRepository: ConfigurationRepository,
    private val discoverRepository: DiscoverRepository,
    private val genreRepository: GenreRepository,
    private val movieListRepository: MovieListRepository
) : ViewModel() {
    private val mutableCategories = MutableStateFlow<List<Category>>(listOf())
    val categories = mutableCategories.asStateFlow()

    init {
        viewModelScope.launch {
            val details = configurationRepository.details()

            val lists = listOf(
                async { createCategory("Now Playing", details, movieListRepository.nowPlaying().results) },
                async { createCategory("Popular", details, movieListRepository.popular().results) },
                async { createCategory("Top Rated", details, movieListRepository.topRated().results) },
                async { createCategory("Upcoming", details, movieListRepository.upcoming().results) }
            )

            val genres = genreRepository.movies().genres.map { genre ->
                async {
                    discoverRepository.movie(
                        DiscoverMovie(withGenres = genre.id.toString())
                    )?.results?.let { results ->
                        createCategory(
                            genre.name,
                            details,
                            results
                        )
                    }
                }
            }

            mutableCategories.value = (lists + genres).awaitAll().filterNotNull()
        }
    }

    private fun createCategory(title: String, details: Details, movies: List<Result>): Category = Category(
        title,
        movies.map { it ->
            it.posterPath.let { posterPath ->
                it.copy(
                    posterPath = "${details.images.secureBaseUrl}${details.images.posterSizes[3]}$posterPath"
                )
            }
        }
    )
}
