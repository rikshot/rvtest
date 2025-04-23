package fi.orkas.rvtest

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import fi.orkas.rvtest.repository.ConfigurationRepository
import fi.orkas.rvtest.repository.MovieDetailsRepository
import fi.orkas.rvtest.repository.TvDetailsRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.flow

enum class DetailsType {
    MOVIE,
    TV
}

@HiltViewModel
class DetailsViewModel @Inject constructor(
    handle: SavedStateHandle,
    private val configurationRepository: ConfigurationRepository,
    private val movieDetailsRepository: MovieDetailsRepository,
    private val tvDetailsRepository: TvDetailsRepository
) : ViewModel() {
    private val route = handle.toRoute<DetailsRoute>()
    val details = flow {
        val details = configurationRepository.details()
        when (route.type) {
            DetailsType.MOVIE -> {
                val response = movieDetailsRepository.details(route.id)
                emit(
                    response.copy(
                        backdropPath = response.backdropPath?.let { backdropPath ->
                            "${details.images.secureBaseUrl}${details.images.backdropSizes[2]}$backdropPath"
                        }
                    )
                )
            }

            DetailsType.TV -> {
                val response = tvDetailsRepository.details(route.id)
                emit(
                    response.copy(
                        backdropPath = response.backdropPath?.let { backdropPath ->
                            "${details.images.secureBaseUrl}${details.images.backdropSizes[2]}$backdropPath"
                        }
                    )
                )
            }
        }
    }
}
