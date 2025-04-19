package fi.orkas.rvtest

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import fi.orkas.rvtest.repository.ConfigurationRepository
import fi.orkas.rvtest.repository.MovieDetailsRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.flow

@HiltViewModel
class DetailsViewModel @Inject constructor(
    handle: SavedStateHandle,
    private val configurationRepository: ConfigurationRepository,
    private val movieDetailsRepository: MovieDetailsRepository
) : ViewModel() {
    private val route = handle.toRoute<Details>()
    val details = flow {
        val details = configurationRepository.details()
        val response = movieDetailsRepository.details(route.id)
        emit(
            response.copy(
                backdropPath = "${details.images.secureBaseUrl}${details.images.backdropSizes[2]}${response.backdropPath}"
            )
        )
    }
}
