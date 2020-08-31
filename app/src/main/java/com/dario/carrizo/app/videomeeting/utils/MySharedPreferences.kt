package com.dario.carrizo.app.videomeeting.utils

import android.content.Context

/**
 * @author Dario Carrizo on 15/7/2020
 **/
class MySharedPreferences(context: Context) {

    companion object{
        private const val KEY_FIRST_NAME = "first_name"
        private const val KEY_LAST_NAME = "last_name"
        private const val KEY_EMAIL = "email"
    }



    //  Nombre del file del Shared Preference
    private val fileName = "videoMeetingPreference"

    //  Instancia del fichero
    private val pref = context.getSharedPreferences(fileName, Context.MODE_PRIVATE)

    // Variables que hacen referencia a los valores que vamos a almacenar
    var isSignedIn: Boolean
        get() = pref.getBoolean("isSignedIn", false)
        set(value) = pref.edit().putBoolean("isSignedIn", value).apply()

    var firstName: String
        get() = pref.getString(KEY_FIRST_NAME, null).toString()
        set(value) = pref.edit().putString(KEY_FIRST_NAME, value).apply()
    var lastName: String
        get() = pref.getString(KEY_LAST_NAME, null).toString()
        set(value) = pref.edit().putString(KEY_LAST_NAME, value).apply()
    var email: String
        get() = pref.getString(KEY_EMAIL, null).toString()
        set(value) = pref.edit().putString(KEY_EMAIL, value).apply()
    var userId: String
        get() = pref.getString(Constants.KEY_USER_ID, null).toString()
        set(value) = pref.edit().putString(Constants.KEY_USER_ID, value).apply()

    fun clearPreferencees(){
        pref.edit().clear().apply()
    }
}