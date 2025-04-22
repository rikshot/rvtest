package fi.orkas.rvtest

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ExtraSpaceLinearLayoutManager(context: Context, orientation: Int, reverseLayout: Boolean) :
    LinearLayoutManager(context, orientation, reverseLayout) {

    private val horizontalExtraSpace = context.resources.displayMetrics.widthPixels / 2
    private val verticalExtraSpace = context.resources.displayMetrics.heightPixels / 2

    override fun calculateExtraLayoutSpace(state: RecyclerView.State, extraLayoutSpace: IntArray) {
        when (orientation) {
            RecyclerView.VERTICAL -> {
                extraLayoutSpace[0] = verticalExtraSpace
                extraLayoutSpace[1] = verticalExtraSpace
            }

            RecyclerView.HORIZONTAL -> {
                extraLayoutSpace[0] = horizontalExtraSpace
                extraLayoutSpace[1] = horizontalExtraSpace
            }
        }
    }
}
