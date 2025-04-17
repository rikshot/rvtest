package fi.orkas.rvtest

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import fi.orkas.rvtest.databinding.FragmentHomeBinding
import javax.inject.Inject
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {

    private val homeViewModel by viewModels<HomeViewModel>()
    private lateinit var verticalAdapter: VerticalAdapter

    @Inject
    lateinit var viewCache: ViewCache

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewCache.cache(requireContext(), lifecycleScope, R.layout.category, 4)
        viewCache.cache(requireContext(), lifecycleScope, R.layout.card, 21)

        verticalAdapter = VerticalAdapter(this, viewCache) { id ->
            findNavController().navigate(route = Details(id))
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                homeViewModel.categories.collect { categories ->
                    verticalAdapter.submitList(categories)
                }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentHomeBinding.inflate(inflater)
        binding.categories.apply {
            setHasFixedSize(true)
            setItemViewCacheSize(0)
            layoutManager =
                ExtraSpaceLinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            adapter = verticalAdapter
            addOnScrollListener(verticalAdapter.preloader)
        }
        return binding.root
    }
}
