package fi.orkas.rvtest

import android.annotation.SuppressLint
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import fi.orkas.rvtest.databinding.CategoryBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class CategoryViewHolder(val binding: CategoryBinding, val adapter: HorizontalAdapter, var job: Job? = null) :
    RecyclerView.ViewHolder(binding.root)

@SuppressLint("RestrictedApi")
open class VerticalAdapter(
    internal val fragment: Fragment,
    internal val viewCache: ViewCache,
    internal val onClick: (Int) -> Unit
) : ListAdapter<Category, CategoryViewHolder>(
    AsyncDifferConfig.Builder<Category>(diffCallback)
        .setMainThreadExecutor(Dispatchers.Main.asExecutor())
        .setBackgroundThreadExecutor(Dispatchers.Default.asExecutor())
        .build()
) {
    private val recyclerViewPool = RecyclerView.RecycledViewPool()

    private val horizontalStates = HashMap<Int, Parcelable?>()

    init {
        setHasStableIds(true)
        recyclerViewPool.setMaxRecycledViews(0, 21)
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
            itemAnimator = null
            layoutManager =
                ExtraSpaceLinearLayoutManager(parent.context, RecyclerView.HORIZONTAL, false).apply {
                    initialPrefetchItemCount = 7
                }
            setRecycledViewPool(recyclerViewPool)
            setItemViewCacheSize(0)
        }.adapter = adapter
        return CategoryViewHolder(binding, adapter).apply {
            adapter.addOnPagesUpdatedListener {
                binding.category.layoutManager?.let { layoutManager ->
                    if (!layoutManager.isSmoothScrolling) {
                        horizontalStates[bindingAdapterPosition]?.let { state ->
                            binding.category.layoutManager?.onRestoreInstanceState(state)
                        }
                    }
                }
            }
        }
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val item = getItem(position)
        holder.job?.cancel()
        holder.job = fragment.lifecycleScope.launch {
            item.flow.flowWithLifecycle(fragment.lifecycle).collectLatest { data -> holder.adapter.submitData(data) }
        }

        holder.binding.apply {
            title.text = item.title

            category.clearOnScrollListeners()
            category.addOnScrollListener(holder.adapter.preloader)
            category.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        val position = holder.bindingAdapterPosition
                        val layoutManager = holder.binding.category.layoutManager as LinearLayoutManager
                        horizontalStates.put(position, layoutManager.onSaveInstanceState())
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
