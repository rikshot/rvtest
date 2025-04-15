package fi.orkas.rvtest

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import fi.orkas.rvtest.repository.ConfigurationRepository
import fi.orkas.rvtest.repository.Details
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
            mutableCategories.value = listOf(
                createCategory("Now Playing", details, movieRepository.nowPlaying().results),
                createCategory("Popular", details, movieRepository.popular().results),
                createCategory("Top Rated", details, movieRepository.topRated().results),
                createCategory("Upcoming", details, movieRepository.upcoming().results)
            )
        }
    }

    private fun createCategory(title: String, details: Details, movies: List<Result>): Category = Category(
        title,
        movies.map { it ->
            it.copy(
                posterPath = "${details.images.secureBaseUrl}/${details.images.posterSizes[2]}${it.posterPath}"
            )
        }
    )
}
