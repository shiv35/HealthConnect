package com.example.health_connect

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
        @POST("predict")
        fun getPrediction(@Body request: InputDataClass): Call<ResponseDataClass>
}