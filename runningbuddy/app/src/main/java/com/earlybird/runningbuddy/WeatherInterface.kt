package com.earlybird.runningbuddy

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherInterface {
    @GET("getUltraSrtNcst?serviceKey=DP0tdDGkytW6UzKZENko82wmCGMkElWIE2eJgraICITySpab3uBVke9VoNcnGI%2BOw8bqv9wJQiVGJAxSOwMMgQ%3D%3D")
    fun GetWeather(
        @Query("dataType") data_type : String,
        @Query("numOfRows") num_of_rows : Int,
        @Query("pageNo") page_no : Int,
        @Query("base_date") base_date : String,
        @Query("base_time") base_time : String,
        @Query("nx") nx : String,
        @Query("ny") ny : String
    ): Call<WEATHER> // WEATHER는 DATA CLASS

}