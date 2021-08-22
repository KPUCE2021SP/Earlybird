package com.earlybird.runningbuddy

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class verticalItemDecorator(private val divHeight : Int) :  //아이템크기 조정
    RecyclerView.ItemDecoration() {

    @Override
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        outRect.top = divHeight
        outRect.bottom = divHeight
    }
}
class HorizontalItemDecorator(private val divHeight : Int) :
    RecyclerView.ItemDecoration() {

    @Override
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        outRect.left = divHeight
        outRect.right = divHeight
    }
}