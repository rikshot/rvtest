package fi.orkas.rvtest

import android.graphics.Rect
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
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

class CategoryViewHolder(val binding: CategoryBinding, val adapter: HorizontalAdapter) :
    RecyclerView.ViewHolder(binding.root)

class VerticalAdapter(internal val parent: MainActivity, private val viewCache: ViewCache) :
    ListAdapter<CategoryWithMedia, CategoryViewHolder>(diffCallback) {
    internal val preloader: RecyclerViewPreloader<Media>
    private val recyclerViewPool = RecyclerView.RecycledViewPool()

    private val horizontalStates = HashMap<Int, Parcelable?>()
    private val horizontalPositions = HashMap<Int, IntRange>()

    init {
        setHasStableIds(true)
        recyclerViewPool.setMaxRecycledViews(0, 21)

        val sizeProvider = FixedPreloadSizeProvider<Media>(POSTER_WIDTH, POSTER_HEIGHT)
        val modelProvider =
            object : ListPreloader.PreloadModelProvider<Media> {
                override fun getPreloadItems(position: Int): List<Media> {
                    val item = getItem(position)
                    return if (item.media.isNotEmpty()) {
                        val range = horizontalPositions.getOrElse(position) { 0..7 }
                        val validRange = 0..item.media.size
                        getItem(position).media.slice(range.intersect(validRange))
                    } else {
                        listOf()
                    }
                }

                override fun getPreloadRequestBuilder(item: Media): RequestBuilder<*>? = Glide
                    .with(parent)
                    .load("file:///android_asset/${item.poster}".toUri())
                    .override(POSTER_WIDTH, POSTER_HEIGHT)
            }
        preloader = RecyclerViewPreloader<Media>(parent, modelProvider, sizeProvider, 3)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = viewCache.getView(R.layout.category)?.let { CategoryBinding.bind(it) } ?: CategoryBinding.inflate(
            LayoutInflater.from(parent.context)
        )
        val adapter = HorizontalAdapter(this, viewCache)
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
                        outRect.left = 10
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
        holder.adapter.submitList(item.media)

        holder.binding.apply {
            horizontalStates[position]?.let { state ->
                category.layoutManager?.onRestoreInstanceState(state)
            }

            title.text = item.category.title

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

    override fun getItemId(position: Int): Long = getItem(position).category.cid.toLong()

    companion object {
        private val diffCallback =
            object : DiffUtil.ItemCallback<CategoryWithMedia>() {
                override fun areItemsTheSame(oldItem: CategoryWithMedia, newItem: CategoryWithMedia): Boolean =
                    oldItem.category.cid == newItem.category.cid

                override fun areContentsTheSame(oldItem: CategoryWithMedia, newItem: CategoryWithMedia): Boolean =
                    oldItem == newItem
            }
    }
}
