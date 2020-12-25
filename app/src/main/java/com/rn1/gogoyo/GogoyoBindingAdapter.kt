package com.rn1.gogoyo

import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.rn1.gogoyo.ext.toDisplayFormat
import java.text.DecimalFormat

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

@BindingAdapter("imageUrl")
fun bindImage(imgView: ImageView, imgUrl: String?) {
    val imgUri = imgUrl?.toUri()?.buildUpon()?.scheme("https")?.build()
    Glide.with(imgView.context)
        .load(imgUri)
        .apply(
            RequestOptions()
                .placeholder(R.drawable.dog_place_holder)
                .error(R.drawable.dog_place_holder))
        .into(imgView)

}

@BindingAdapter("petGender")
fun bindGenderSymbol(imageView: ImageView, gender: String){
    imageView.apply {
        if (gender == "男生") {
            setImageResource(R.drawable.mars)
        } else {
            setImageResource(R.drawable.femenine)
        }
    }
}

@BindingAdapter("secondLong")
fun bindFormatTime(textView: TextView, sec: Long){
    textView.apply {
        text = formatTime(sec.toInt())
    }
}

@BindingAdapter("distance")
fun bindFormatKm(textView: TextView, distance: Float){
    textView.apply {
        val decimalFormat = DecimalFormat("#,##0.000")
        text = "${formatFloat(distance)} km"
    }
}

private fun formatTime(second: Int): String{

    val hour = second / 3600
    var secondTime = second % 3600
    val minute = secondTime / 60
    secondTime %= 60

    if (hour == 0) {
        if (minute == 0) {
            return "${addZero(secondTime)}秒"
        }
        return "${addZero(minute)}分${addZero(secondTime)}秒"
    }
    return "${addZero(hour)}時${addZero(minute)}分${addZero(secondTime)}秒"
}

private fun addZero(number: Int): String{
    return if(number.toString().length == 1){
        "0$number"
    } else {
        "$number"
    }
}

private fun formatFloat(float: Float): String{
    val decimalFormat = DecimalFormat("#,##0.000")
    return decimalFormat.format(float)
}