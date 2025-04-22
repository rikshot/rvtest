package fi.orkas.rvtest

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.ListPreloader
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.integration.recyclerview.RecyclerViewPreloader
import com.bumptech.glide.util.ViewPreloadSizeProvider
import fi.orkas.rvtest.databinding.CardBinding
import fi.orkas.rvtest.repository.MediaCard

class MediaViewHolder(val binding: CardBinding) : RecyclerView.ViewHolder(binding.root)

class HorizontalAdapter(private val parentAdapter: VerticalAdapter) :
    PagingDataAdapter<MediaCard, MediaViewHolder>(diffCallback) {
    internal val preloader: RecyclerViewPreloader<MediaCard>

    init {
        val binding = CardBinding.inflate(LayoutInflater.from(parentAdapter.fragment.context))
        val sizeProvider = ViewPreloadSizeProvider<MediaCard>(binding.poster)
        val modelProvider =
            object : ListPreloader.PreloadModelProvider<MediaCard> {
                override fun getPreloadItems(position: Int): List<MediaCard?> =
                    peek(position)?.let { listOf(it) } ?: listOf()

                override fun getPreloadRequestBuilder(item: MediaCard): RequestBuilder<*>? =
                    item.poster?.let { poster ->
                        Glide
                            .with(parentAdapter.fragment)
                            .load(item.poster)
                            .thumbnail(Glide.with(parentAdapter.fragment).load(item.thumbnail))
                    }
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
        binding.title.isSelected = true
        return MediaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MediaViewHolder, position: Int) {
        getItem(position)?.let { item ->
            holder.binding.apply {
                root.setOnClickListener {
                    parentAdapter.onClick(item.id)
                }
                Glide
                    .with(poster)
                    .load(item.poster)
                    .thumbnail(Glide.with(poster).load(item.thumbnail))
                    .into(poster)
                title.text = item.title
            }
        }
    }

    override fun onViewRecycled(holder: MediaViewHolder) {
        Glide.with(holder.binding.poster).clear(holder.binding.poster)
    }

    companion object {
        private val diffCallback =
            object : DiffUtil.ItemCallback<MediaCard>() {
                override fun areItemsTheSame(oldItem: MediaCard, newItem: MediaCard): Boolean = oldItem.id == newItem.id
                override fun areContentsTheSame(oldItem: MediaCard, newItem: MediaCard): Boolean = oldItem == newItem
            }
    }
}
