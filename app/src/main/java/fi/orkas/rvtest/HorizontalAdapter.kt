package fi.orkas.rvtest

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.ListPreloader
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.integration.recyclerview.RecyclerViewPreloader
import com.bumptech.glide.util.FixedPreloadSizeProvider
import fi.orkas.rvtest.databinding.CardBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor

const val POSTER_WIDTH = 180
const val POSTER_HEIGHT = 267

class MediaViewHolder(val binding: CardBinding) : RecyclerView.ViewHolder(binding.root)

class HorizontalAdapter(private val parent: VerticalAdapter, private val viewCache: ViewCache) :
    ListAdapter<Media, MediaViewHolder>(
        AsyncDifferConfig.Builder<Media>(diffCallback)
            .setBackgroundThreadExecutor(
                Dispatchers.Default.asExecutor()
            ).build()
    ) {
    internal val preloader: RecyclerViewPreloader<Media>

    init {
        setHasStableIds(true)

        val sizeProvider = FixedPreloadSizeProvider<Media>(POSTER_WIDTH, POSTER_HEIGHT)
        val modelProvider =
            object : ListPreloader.PreloadModelProvider<Media> {
                override fun getPreloadItems(position: Int): List<Media> = listOf(getItem(position))
                override fun getPreloadRequestBuilder(item: Media): RequestBuilder<*>? = Glide
                    .with(parent.parent)
                    .load("file:///android_asset/${item.poster}".toUri())
                    .override(POSTER_WIDTH, POSTER_HEIGHT)
            }
        preloader = RecyclerViewPreloader<Media>(parent.parent, modelProvider, sizeProvider, 7)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaViewHolder {
        val binding = viewCache.getView(R.layout.card)?.let { CardBinding.bind(it) } ?: CardBinding.inflate(
            LayoutInflater.from(parent.context)
        )
        binding.poster.clipToOutline = true
        return MediaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MediaViewHolder, position: Int) {
        val item = getItem(position)
        holder.binding.apply {
            Glide
                .with(poster)
                .load("file:///android_asset/${item.poster}".toUri())
                .override(POSTER_WIDTH, POSTER_HEIGHT)
                .into(poster)
            title.text = item.title
            genre.text = item.genre
        }
    }

    override fun onViewRecycled(holder: MediaViewHolder) {
        Glide.with(holder.binding.poster).clear(holder.binding.poster)
    }

    override fun getItemId(position: Int): Long = getItem(position).mid.toLong()

    companion object {
        private val diffCallback =
            object : DiffUtil.ItemCallback<Media>() {
                override fun areItemsTheSame(oldItem: Media, newItem: Media): Boolean = oldItem.mid == newItem.mid
                override fun areContentsTheSame(oldItem: Media, newItem: Media): Boolean = oldItem == newItem
            }
    }
}
