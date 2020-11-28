package com.rn1.gogoyo.component

import android.graphics.Outline
import android.view.View
import android.view.ViewOutlineProvider
import com.rn1.gogoyo.GogoyoApplication
import com.rn1.gogoyo.R

class MapOutlineProvider: ViewOutlineProvider() {
    override fun getOutline(view: View, outline: Outline) {
        view.clipToOutline = true
        val radius = GogoyoApplication.instance.resources.getDimensionPixelSize(R.dimen.radius_map_outline)
        outline.setOval(0, 0, view.width, view.height)
    }
}