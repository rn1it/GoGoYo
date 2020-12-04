package com.rn1.gogoyo

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.rn1.gogoyo.model.Users

object UserManager {

    private const val USER_DATA = "user_data"
    private const val USER_UID = "user_uid"

    private val _user = MutableLiveData<Users>()

    val user: LiveData<Users>
        get() = _user

    var userUID: String? = null
        get() = GogoyoApplication.instance
            .getSharedPreferences(USER_DATA, Context.MODE_PRIVATE)
            .getString(USER_UID, null)
        set(value) {
            field = when(value) {
                null -> {
                    GogoyoApplication.instance
                        .getSharedPreferences(USER_DATA, Context.MODE_PRIVATE).edit()
                        .remove(USER_UID)
                        .apply()
                    null
                }
                else -> {
                    GogoyoApplication.instance
                        .getSharedPreferences(USER_DATA, Context.MODE_PRIVATE).edit()
                        .putString(USER_UID, value)
                        .apply()
                    value
                }
            }
        }

    val isLoggedIn: Boolean
        get() = userUID != null

}