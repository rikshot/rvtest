package fi.orkas.rvtest

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
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
import fi.orkas.rvtest.repository.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor

const val POSTER_WIDTH = 180
const val POSTER_HEIGHT = 267

class MediaViewHolder(val binding: CardBinding) : RecyclerView.ViewHolder(binding.root)

class HorizontalAdapter(private val parentAdapter: VerticalAdapter) :
    ListAdapter<Result, MediaViewHolder>(
        AsyncDifferConfig.Builder<Result>(diffCallback)
            .setBackgroundThreadExecutor(
                Dispatchers.Default.asExecutor()
            ).build()
    ) {
    internal val preloader: RecyclerViewPreloader<Result>

    init {
        setHasStableIds(true)

        val sizeProvider = FixedPreloadSizeProvider<Result>(POSTER_WIDTH, POSTER_HEIGHT)
        val modelProvider =
            object : ListPreloader.PreloadModelProvider<Result> {
                override fun getPreloadItems(position: Int): List<Result> = listOf(getItem(position))
                override fun getPreloadRequestBuilder(item: Result): RequestBuilder<*>? = Glide
                    .with(parentAdapter.fragment)
                    .load(item.posterPath.toUri())
                    .override(POSTER_WIDTH, POSTER_HEIGHT)
            }
        preloader = RecyclerViewPreloader<Result>(parentAdapter.fragment, modelProvider, sizeProvider, 7)
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
            Glide
                .with(poster)
                .load(item.posterPath.toUri())
                .override(POSTER_WIDTH, POSTER_HEIGHT)
                .into(poster)
            title.text = item.title
            genre.text = item.releaseDate
        }
    }

    override fun onViewRecycled(holder: MediaViewHolder) {
        Glide.with(holder.binding.poster).clear(holder.binding.poster)
    }

    override fun getItemId(position: Int): Long = getItem(position).id.toLong()

    companion object {
        private val diffCallback =
            object : DiffUtil.ItemCallback<Result>() {
                override fun areItemsTheSame(oldItem: Result, newItem: Result): Boolean = oldItem.id == newItem.id
                override fun areContentsTheSame(oldItem: Result, newItem: Result): Boolean = oldItem == newItem
            }
    }
}
