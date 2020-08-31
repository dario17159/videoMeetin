package com.dario.carrizo.app.videomeeting.utils

import android.app.Application

val preferences: MySharedPreferences by lazy { MyApp.pref!! }

class MyApp: Application() {
    companion object{
        var pref: MySharedPreferences? = null
    }

    override fun onCreate() {
        super.onCreate()
        pref = MySharedPreferences(applicationContext)
    }
}