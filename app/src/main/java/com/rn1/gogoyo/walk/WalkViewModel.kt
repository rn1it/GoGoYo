package com.rn1.gogoyo.walk

import android.graphics.Rect
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import com.rn1.gogoyo.GogoyoApplication
import com.rn1.gogoyo.R
import com.rn1.gogoyo.component.MapOutlineProvider
import com.rn1.gogoyo.model.source.GogoyoRepository

class WalkViewModel(val repository: GogoyoRepository): ViewModel() {

    val outlineProvider =  MapOutlineProvider()

    private val _navigateToStartWalk = MutableLiveData<Boolean>()

    val navigateToStartWalk: LiveData<Boolean>
    get() = _navigateToStartWalk

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






    fun onNavigateToStartWalk(){
        _navigateToStartWalk.value = true
    }

    fun onDoneNavigateToStartWalk(){
        _navigateToStartWalk.value = null
    }

}