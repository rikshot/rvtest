package fi.orkas.rvtest

import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.LayoutRes
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ViewCache(private val activity: MainActivity) {
    private val fakeParent = FrameLayout(activity)
    private val layoutInflater = LayoutInflater.from(activity)

    private val viewQueue = HashMap<Int, ArrayDeque<View>>()

    fun cache(@LayoutRes layoutId: Int, maxViews: Int) {
        activity.lifecycleScope.launch(Dispatchers.Default) { (0..maxViews).forEach { createView(layoutId, maxViews) } }
    }

    fun getView(@LayoutRes layoutId: Int): View? {
        val view = viewQueue[layoutId]?.removeFirstOrNull()
        activity.lifecycleScope.launch(Dispatchers.Default) { createView(layoutId, viewQueue[layoutId]?.size ?: 10) }
        return view
    }

    private fun createView(@LayoutRes viewId: Int, maxViews: Int) {
        viewQueue.getOrPut(viewId) { ArrayDeque(maxViews) }.addLast(layoutInflater.inflate(viewId, fakeParent, false))
    }
}
