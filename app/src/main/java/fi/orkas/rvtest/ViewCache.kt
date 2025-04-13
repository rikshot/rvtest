package fi.orkas.rvtest

import android.view.View
import android.widget.FrameLayout
import androidx.annotation.LayoutRes
import androidx.asynclayoutinflater.view.AsyncLayoutInflater
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@OptIn(DelicateCoroutinesApi::class)
class ViewCache(activity: MainActivity) {

    private val asyncLayoutInflater = AsyncLayoutInflater(activity)
    private val parent = FrameLayout(activity)

    private val viewQueue = HashMap<Int, ArrayDeque<View>>()

    fun cache(@LayoutRes layoutId: Int, maxViews: Int) {
        GlobalScope.launch { (0..maxViews).forEach { createView(layoutId, maxViews) } }
    }

    fun getView(@LayoutRes layoutId: Int): View? {
        val view = viewQueue[layoutId]?.removeFirstOrNull()
        GlobalScope.launch { createView(layoutId, viewQueue[layoutId]?.size ?: 10) }
        return view
    }

    private fun createView(@LayoutRes viewId: Int, maxViews: Int) {
        asyncLayoutInflater.inflate(viewId, parent) { view, res, parent ->
            viewQueue.getOrPut(viewId) { ArrayDeque(maxViews) }.addLast(view)
        }
    }
}
