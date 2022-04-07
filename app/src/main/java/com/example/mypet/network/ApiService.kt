package com.example.mypet.network

import com.example.mypet.model.FactModel
import com.example.mypet.model.RandomImageModel
import retrofit2.Call
import retrofit2.http.GET

interface ApiService {

    @GET("api/facts")
    fun getFact(): Call<FactModel>

    @GET("woof.json")
    fun getRandomImage(): Call<RandomImageModel>
}