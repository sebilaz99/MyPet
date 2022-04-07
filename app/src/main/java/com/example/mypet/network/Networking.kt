package com.example.mypet.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val BASE_URL = "https://dog-api.kinduff.com/"

const val DOG_IMAGE_BASE_URL = "https://random.dog/"

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

        private val retrofit2: Retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .baseUrl(DOG_IMAGE_BASE_URL)
            .build()

        val factInstance: ApiService = retrofit.create(ApiService::class.java)

        val imageInstance: ApiService = retrofit2.create(ApiService::class.java)
    }
}