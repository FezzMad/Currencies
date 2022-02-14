package com.example.cool_app.model.api

import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Query

interface API {
    @GET("/scripts/XML_daily.asp")
    fun getRuData(@Query("date_req") date: String): Call<ResponseBody>

    @GET("/scripts/XML_daily_eng.asp")
    fun getEngData(@Query("date_req") date: String): Call<ResponseBody>
}

fun cbapi(): API {

    val logging = HttpLoggingInterceptor()
    logging.setLevel(HttpLoggingInterceptor.Level.BODY)
    val client: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    val retrofit = Retrofit.Builder()
            .baseUrl("https://www.cbr.ru")
            .client(client)
            .build()

    return retrofit.create(API::class.java)
}