package fi.orkas.rvtest

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import dagger.hilt.android.AndroidEntryPoint
import fi.orkas.rvtest.databinding.FragmentHomeBinding
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var db: Database
    private lateinit var viewCache: ViewCache
    private lateinit var verticalAdapter: VerticalAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        db =
            Room
                .databaseBuilder(requireContext(), Database::class.java, "database.db")
                .createFromAsset("database.db")
                .build()

        viewCache = ViewCache(requireActivity())
        viewCache.cache(R.layout.category, 4)
        viewCache.cache(R.layout.card, 21)

        verticalAdapter = VerticalAdapter(requireActivity(), viewCache)

        lifecycleScope.launch {
            db.mediaDao().getCategoryMedia().collect {
                verticalAdapter.submitList(it)
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
