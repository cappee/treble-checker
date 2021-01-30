package dev.cappee.treble.main.recycler

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class ItemDecoration(private val margin: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        with(outRect) {
            top = if (parent.getChildAdapterPosition(view) == 0) {
                margin
            } else {
                margin / 2
            }
            left = margin
            right = margin
            bottom = if (parent.getChildAdapterPosition(view) == state.itemCount - 1) {
                margin
            } else {
                margin / 2
            }
        }
    }

    /*override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val divider = ContextCompat.getDrawable(context, R.drawable.divider)
        val left: Int = (parent.width/2) - parent.paddingLeft - (divider?.intrinsicWidth!!/2)
        val right: Int = (parent.width/2) - parent.paddingRight + (divider.intrinsicWidth/2)

        for (i in 0 until parent.childCount-1) {
            val child: View = parent.getChildAt(i)
            val top = child.bottom + (margin/2) - (divider.intrinsicHeight/2)
            val bottom: Int = child.bottom + (margin/2) + (divider.intrinsicHeight/2)
            println("${divider.intrinsicWidth} - ${divider.intrinsicHeight}")
            divider.setBounds(left, top, right, bottom)
            divider.draw(c)
        }
    }*/
}