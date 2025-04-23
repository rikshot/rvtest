package fi.orkas.rvtest

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import fi.orkas.rvtest.databinding.FragmentDetailsBinding
import fi.orkas.rvtest.repository.MovieDetailsResponse
import fi.orkas.rvtest.repository.TvDetailsResponse
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DetailsFragment : Fragment(R.layout.fragment_details) {

    private val viewModel: DetailsViewModel by viewModels()
    private lateinit var binding: FragmentDetailsBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentDetailsBinding.inflate(inflater)
        binding.apply {
            close.setOnClickListener {
                findNavController().popBackStack()
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        lifecycleScope.launch {
            viewModel.details.flowWithLifecycle(lifecycle).collect { details ->
                when (details) {
                    is MovieDetailsResponse -> {
                        binding.title.text = details.title
                        binding.overview.text = details.overview
                        details.backdropPath?.let { backdropPath ->
                            Glide.with(binding.backdrop).load(backdropPath).into(binding.backdrop)
                        }
                    }

                    is TvDetailsResponse -> {
                        binding.title.text = details.name
                        binding.overview.text = details.overview
                        details.backdropPath?.let { backdropPath ->
                            Glide.with(binding.backdrop).load(backdropPath).into(binding.backdrop)
                        }
                    }
                }
            }
        }
    }
}
