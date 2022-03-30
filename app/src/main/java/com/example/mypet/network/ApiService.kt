package com.example.mypet.network

import com.example.mypet.model.FactModel
import retrofit2.Call
import retrofit2.http.GET

interface ApiService {

    @GET("api/facts")
    fun getFact(): Call<FactModel>
}