package com.rn1.gogoyo

import android.content.res.ColorStateList
import android.media.Image
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rn1.gogoyo.ext.getColor
import com.rn1.gogoyo.ext.toDisplayFormat

@BindingAdapter("addDecoration")
fun bindDecoration(recyclerView: RecyclerView, decoration: RecyclerView.ItemDecoration?) {
    decoration?.let { recyclerView.addItemDecoration(it) }
}

@BindingAdapter("timeToDisplayFormat")
fun bindDisplayFormatTime(textView: TextView, time: Long?) {
    textView.text = time?.toDisplayFormat()
}

@BindingAdapter("changeCollectButtonStatus")
fun bindCollectButton(imageButton: ImageButton, isCollected: Boolean) {
    imageButton.apply {
        when (isCollected) {
            true ->this.setColorFilter(resources.getColor(R.color.red_F44336))
            false -> this.setColorFilter(resources.getColor(R.color.grey_999999))
        }
    }
}

@BindingAdapter("setProfileButton")
fun bindUserProfileButton(button: Button, isLoginUser: Boolean?){
    isLoginUser.let {
        button.apply {
            when (isLoginUser) {
                true -> {
                    isClickable = true
                    text = "修改資料"
                }
                false -> {
                    isClickable = false
                    text = "朋友"
                }
            }
        }
    }
}