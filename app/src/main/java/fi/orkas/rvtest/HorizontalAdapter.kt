package fi.orkas.rvtest

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.ListPreloader
import com.bumptech.glide.ListPreloader.PreloadSizeProvider
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.integration.recyclerview.RecyclerViewPreloader
import fi.orkas.rvtest.databinding.CardBinding
import fi.orkas.rvtest.repository.MediaCard
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor

const val POSTER_WIDTH = 180
const val POSTER_HEIGHT = 267

class MediaViewHolder(val binding: CardBinding) : RecyclerView.ViewHolder(binding.root)

class HorizontalAdapter(private val parentAdapter: VerticalAdapter) :
    ListAdapter<MediaCard, MediaViewHolder>(
        AsyncDifferConfig.Builder<MediaCard>(diffCallback)
            .setBackgroundThreadExecutor(
                Dispatchers.Default.asExecutor()
            ).build()
    ) {
    internal val preloader: RecyclerViewPreloader<MediaCard>

    init {
        setHasStableIds(true)

        val sizeProvider = object : PreloadSizeProvider<MediaCard> {
            override fun getPreloadSize(item: MediaCard, adapterPosition: Int, perItemPosition: Int): IntArray? =
                listOf(item.width, item.height).toIntArray()
        }
        val modelProvider =
            object : ListPreloader.PreloadModelProvider<MediaCard> {
                override fun getPreloadItems(position: Int): List<MediaCard> = listOf(getItem(position))
                override fun getPreloadRequestBuilder(item: MediaCard): RequestBuilder<*>? = Glide
                    .with(parentAdapter.fragment)
                    .load(item.posterUrl)
                    .override(item.width, item.height)
            }
        preloader = RecyclerViewPreloader<MediaCard>(parentAdapter.fragment, modelProvider, sizeProvider, 7)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaViewHolder {
        val binding =
            parentAdapter.viewCache.getView(parentAdapter.fragment.lifecycleScope, R.layout.card)
                ?.let { CardBinding.bind(it) }
                ?: CardBinding.inflate(
                    LayoutInflater.from(parent.context)
                )
        binding.poster.clipToOutline = true
        return MediaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MediaViewHolder, position: Int) {
        val item = getItem(position)
        holder.binding.apply {
            root.setOnClickListener {
                parentAdapter.onClick(item.title.hashCode())
            }
            poster.layoutParams = poster.layoutParams.apply {
                width = item.width
                height = item.height
            }
            Glide
                .with(poster)
                .load(item.posterUrl)
                .override(item.width, item.height)
                .into(poster)
            title.text = item.title
        }
    }

    override fun onViewRecycled(holder: MediaViewHolder) {
        Glide.with(holder.binding.poster).clear(holder.binding.poster)
    }

    override fun getItemId(position: Int): Long = getItem(position).title.hashCode().toLong()

    companion object {
        private val diffCallback =
            object : DiffUtil.ItemCallback<MediaCard>() {
                override fun areItemsTheSame(oldItem: MediaCard, newItem: MediaCard): Boolean =
                    oldItem.title == newItem.title

                override fun areContentsTheSame(oldItem: MediaCard, newItem: MediaCard): Boolean = oldItem == newItem
            }
    }
}
