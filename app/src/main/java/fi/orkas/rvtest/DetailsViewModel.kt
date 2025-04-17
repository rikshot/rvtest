package fi.orkas.rvtest

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import fi.orkas.rvtest.repository.ConfigurationRepository
import fi.orkas.rvtest.repository.MovieDetailsRepository
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val configurationRepository: ConfigurationRepository,
    private val movieDetailsRepository: MovieDetailsRepository
) : ViewModel()
