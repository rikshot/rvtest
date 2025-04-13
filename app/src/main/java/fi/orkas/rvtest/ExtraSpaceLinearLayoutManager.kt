package fi.orkas.rvtest

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ExtraSpaceLinearLayoutManager(context: Context, orientation: Int, reverseLayout: Boolean) :
    LinearLayoutManager(context, orientation, reverseLayout) {

    private val screenWidth = context.resources.displayMetrics.widthPixels
    private val screenHeight = context.resources.displayMetrics.heightPixels

    override fun calculateExtraLayoutSpace(state: RecyclerView.State, extraLayoutSpace: IntArray) {
        when (orientation) {
            RecyclerView.VERTICAL -> {
                extraLayoutSpace[0] = screenHeight
                extraLayoutSpace[1] = screenHeight
            }

            RecyclerView.HORIZONTAL -> {
                extraLayoutSpace[0] = screenWidth
                extraLayoutSpace[1] = screenWidth
            }
        }
    }
}
