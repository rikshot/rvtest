package fi.orkas.rvtest

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.LayoutRes
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Singleton
class ViewCache @Inject constructor() {
    private val cache = HashMap<Int, Pair<() -> Unit, ArrayDeque<View>>>()

    fun cache(context: Context, scope: CoroutineScope, @LayoutRes layoutId: Int, maxViews: Int) {
        val layoutInflater = LayoutInflater.from(context)
        val parent = FrameLayout(context)
        val creator = {
            createView(
                layoutInflater,
                parent,
                layoutId
            )
        }
        cache.put(layoutId, Pair(creator, ArrayDeque(maxViews)))
        scope.launch(Dispatchers.Default) {
            (0..maxViews).forEach {
                creator()
            }
        }
    }

    fun getView(scope: CoroutineScope, @LayoutRes layoutId: Int): View? {
        val (creator, view) = cache[layoutId] ?: throw RuntimeException("layoutId $layoutId not cached")
        scope.launch(Dispatchers.Default) { creator() }
        return view.removeFirstOrNull()
    }

    private fun createView(layoutInflater: LayoutInflater, parent: ViewGroup, @LayoutRes layoutId: Int) {
        val viewQueue = cache[layoutId]?.second
        viewQueue?.addLast(layoutInflater.inflate(layoutId, parent, false))
    }
}
