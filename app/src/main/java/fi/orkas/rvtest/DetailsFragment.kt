package fi.orkas.rvtest

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.toRoute
import dagger.hilt.android.AndroidEntryPoint
import fi.orkas.rvtest.databinding.FragmentDetailsBinding

@AndroidEntryPoint
class DetailsFragment : Fragment(R.layout.fragment_details) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentDetailsBinding.inflate(inflater)
        binding.apply {
            id.text = findNavController().getBackStackEntry<Details>().toRoute<Details>().id.toString()
            close.setOnClickListener {
                findNavController().popBackStack()
            }
        }
        return binding.root
    }
}
