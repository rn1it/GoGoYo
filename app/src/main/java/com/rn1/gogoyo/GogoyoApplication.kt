package com.rn1.gogoyo

import android.app.Application
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import com.rn1.gogoyo.model.source.GogoyoRepository
import com.rn1.gogoyo.util.ServiceLocator
import kotlin.properties.Delegates

class GogoyoApplication: Application() {

    val repository: GogoyoRepository
        get() = ServiceLocator.provideTasksRepository(this)

    companion object {
        var instance: GogoyoApplication by Delegates.notNull()
        lateinit var requestQueue: RequestQueue
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        requestQueue = Volley.newRequestQueue(applicationContext)
    }
}