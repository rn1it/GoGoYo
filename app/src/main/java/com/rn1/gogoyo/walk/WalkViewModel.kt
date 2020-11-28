package com.rn1.gogoyo.walk

import androidx.lifecycle.ViewModel
import com.rn1.gogoyo.component.MapOutlineProvider
import com.rn1.gogoyo.model.source.GogoyoRepository

class WalkViewModel(val repository: GogoyoRepository): ViewModel() {
    val outlineProvider =  MapOutlineProvider()

}