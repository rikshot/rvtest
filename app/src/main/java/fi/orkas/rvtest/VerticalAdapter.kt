package fi.orkas.rvtest

import android.graphics.Rect
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.ListPreloader
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.integration.recyclerview.RecyclerViewPreloader
import com.bumptech.glide.util.FixedPreloadSizeProvider
import fi.orkas.rvtest.databinding.CategoryBinding
import fi.orkas.rvtest.repository.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor

class CategoryViewHolder(val binding: CategoryBinding, val adapter: HorizontalAdapter) :
    RecyclerView.ViewHolder(binding.root)

class VerticalAdapter(internal val fragment: Fragment, internal val viewCache: ViewCache) :
    ListAdapter<Category, CategoryViewHolder>(
        AsyncDifferConfig.Builder<Category>(diffCallback)
            .setBackgroundThreadExecutor(
                Dispatchers.Default.asExecutor()
            ).build()
    ) {
    internal val preloader: RecyclerViewPreloader<Result>
    private val recyclerViewPool = RecyclerView.RecycledViewPool()

    private val horizontalStates = HashMap<Int, Parcelable?>()
    private val horizontalPositions = HashMap<Int, IntRange>()

    init {
        setHasStableIds(true)
        recyclerViewPool.setMaxRecycledViews(0, 21)

        val sizeProvider = FixedPreloadSizeProvider<Result>(POSTER_WIDTH, POSTER_HEIGHT)
        val modelProvider =
            object : ListPreloader.PreloadModelProvider<Result> {
                override fun getPreloadItems(position: Int): List<Result> {
                    val item = getItem(position)
                    return if (item.movies.isNotEmpty()) {
                        val range = horizontalPositions.getOrElse(position) { 0..7 }
                        val validRange = 0..item.movies.size
                        getItem(position).movies.slice(range.intersect(validRange))
                    } else {
                        listOf()
                    }
                }

                override fun getPreloadRequestBuilder(item: Result): RequestBuilder<*>? = Glide
                    .with(fragment)
                    .load(item.posterPath.toUri())
                    .override(POSTER_WIDTH, POSTER_HEIGHT)
            }
        preloader = RecyclerViewPreloader<Result>(fragment, modelProvider, sizeProvider, 3)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding =
            viewCache.getView(fragment.lifecycleScope, R.layout.category)?.let { CategoryBinding.bind(it) }
                ?: CategoryBinding.inflate(
                    LayoutInflater.from(parent.context)
                )
        val adapter = HorizontalAdapter(this)
        binding.category.apply {
            setHasFixedSize(true)
            addItemDecoration(
                object : RecyclerView.ItemDecoration() {
                    override fun getItemOffsets(
                        outRect: Rect,
                        view: View,
                        parent: RecyclerView,
                        state: RecyclerView.State
                    ) {
                        outRect.right = 10
                    }
                }
            )
            layoutManager =
                ExtraSpaceLinearLayoutManager(parent.context, RecyclerView.HORIZONTAL, false).apply {
                    initialPrefetchItemCount = 7
                }
            setRecycledViewPool(recyclerViewPool)
            setItemViewCacheSize(0)
            this.adapter = adapter
        }
        return CategoryViewHolder(binding, adapter)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val item = getItem(position)
        holder.adapter.submitList(item.movies)

        holder.binding.apply {
            horizontalStates[position]?.let { state ->
                category.layoutManager?.onRestoreInstanceState(state)
            }

            title.text = item.title

            category.clearOnScrollListeners()
            category.addOnScrollListener(holder.adapter.preloader)
            category.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        val position = holder.bindingAdapterPosition
                        val layoutManager = holder.binding.category.layoutManager as LinearLayoutManager
                        horizontalStates.put(position, layoutManager.onSaveInstanceState())
                        horizontalPositions.put(
                            position,
                            layoutManager.findFirstVisibleItemPosition()..layoutManager.findLastVisibleItemPosition()
                        )
                    }
                }
            })
        }
    }

    override fun getItemId(position: Int): Long = getItem(position).title.hashCode().toLong()

    companion object {
        private val diffCallback =
            object : DiffUtil.ItemCallback<Category>() {
                override fun areItemsTheSame(oldItem: Category, newItem: Category): Boolean =
                    oldItem.title == newItem.title

                override fun areContentsTheSame(oldItem: Category, newItem: Category): Boolean = oldItem == newItem
            }
    }
}
