package com.rn1.gogoyo.walk.end

import android.graphics.Rect
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import com.rn1.gogoyo.GogoyoApplication
import com.rn1.gogoyo.R
import com.rn1.gogoyo.model.source.GogoyoRepository

class WalkEndViewModel(val repository: GogoyoRepository): ViewModel() {





    val decoration = object : RecyclerView.ItemDecoration(){
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            super.getItemOffsets(outRect, view, parent, state)

            // add margin for recyclerView cell
            if (parent.getChildLayoutPosition(view) == 0) {
                outRect.left = 0
            } else {
                outRect.left = GogoyoApplication.instance.resources.getDimensionPixelSize(R.dimen.cell_margin_8dp)
            }
        }
    }

}