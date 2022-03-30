package com.example.mypet.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val BASE_URL = "https://dog-api.kinduff.com/"

class Networking {

    companion object {

        private val okHttpClient = OkHttpClient().newBuilder()
            .addInterceptor {
                it.proceed(it.request().newBuilder().build())
            }
            .build()

        private val retrofit: Retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .baseUrl(BASE_URL)
            .build()

        val factInstance: ApiService = retrofit.create(ApiService::class.java)
    }
}