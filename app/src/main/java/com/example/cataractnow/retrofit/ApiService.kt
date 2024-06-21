package com.example.cataractnow.retrofit
import com.example.cataractnow.response.LoginResponse
import com.example.cataractnow.response.NewsResponse
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiService {
    @Headers("Content-type:application/json")
    @POST("login")
    fun login(@Body requestBody: RequestBody): Call<LoginResponse>

    @Headers("Content-type:application/json")
    @POST("register")
    fun regist(@Body requestBody: RequestBody): Call<LoginResponse>

    @GET("katarak")
    fun getNews(): Call<NewsResponse>
}