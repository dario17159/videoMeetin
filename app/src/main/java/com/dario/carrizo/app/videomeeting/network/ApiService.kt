package com.dario.carrizo.app.videomeeting.network

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.HeaderMap
import retrofit2.http.POST

/**
 * @author Dario Carrizo on 30/8/2020
 **/
interface ApiService {

    @POST("send")
    fun sendRemoteMessage(
        @HeaderMap headers: HashMap<String, String>,
        @Body remoteBody: String
    ): Call<String>
}