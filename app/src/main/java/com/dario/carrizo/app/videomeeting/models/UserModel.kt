package com.dario.carrizo.app.videomeeting.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * @author Dario Carrizo on 29/8/2020
 **/
@Parcelize
data class UserModel(
    val first_name: String = "",
    val last_name: String = "",
    val email: String = "",
    val fcm_token: String = ""
):Parcelable