package fi.orkas.rvtest

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import fi.orkas.rvtest.repository.ConfigurationRepository
import fi.orkas.rvtest.repository.MovieRepository
import fi.orkas.rvtest.repository.Result
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class Category(val title: String, val movies: List<Result>)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val configurationRepository: ConfigurationRepository,
    private val movieRepository: MovieRepository
) : ViewModel() {
    private val mutableCategories = MutableStateFlow<List<Category>>(listOf())
    val categories = mutableCategories.asStateFlow()

    init {
        viewModelScope.launch {
            val details = configurationRepository.details()
            val nowPlaying =
                Category(
                    "Now Playing",
                    movieRepository.nowPlaying().results.map { it ->
                        it.copy(
                            posterPath = "${details.images.secureBaseUrl}/${details.images.posterSizes[2]}${it.posterPath}"
                        )
                    }
                )
            val popular = Category(
                "Popular",
                movieRepository.popular().results.map { it ->
                    it.copy(
                        posterPath = "${details.images.secureBaseUrl}/${details.images.posterSizes[2]}${it.posterPath}"
                    )
                }
            )
            val topRated = Category(
                "Top Rated",
                movieRepository.topRated().results.map { it ->
                    it.copy(
                        posterPath = "${details.images.secureBaseUrl}/${details.images.posterSizes[2]}${it.posterPath}"
                    )
                }
            )
            val upcoming = Category(
                "Upcoming",
                movieRepository.upcoming().results.map { it ->
                    it.copy(
                        posterPath = "${details.images.secureBaseUrl}/${details.images.posterSizes[2]}${it.posterPath}"
                    )
                }
            )

            mutableCategories.value = listOf(nowPlaying, popular, topRated, upcoming)
        }
    }
}
