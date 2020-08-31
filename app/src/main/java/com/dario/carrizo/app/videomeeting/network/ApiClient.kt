package com.dario.carrizo.app.videomeeting.network

import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory

/**
 * @author Dario Carrizo on 30/8/2020
 **/
class ApiClient {
    companion object{
        private var retrofit: Retrofit? = null
        fun getclient(): Retrofit{
            if(retrofit==null){
                retrofit = Retrofit.Builder()
                    .baseUrl("https://fcm.googleapis.com/fcm/")
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .build()
            }
            return retrofit!!
        }
    }
}