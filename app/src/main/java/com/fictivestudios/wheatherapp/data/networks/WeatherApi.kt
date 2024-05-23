package com.fictivestudios.wheatherapp.data.networks

import com.fictivestudios.wheatherapp.data.responses.NextFiveWeatherResponse
import com.fictivestudios.wheatherapp.data.responses.TodayWeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query


interface WeatherApi {

    @GET("weather")
    suspend fun getWeather(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "standard"
    ): TodayWeatherResponse

    @GET("forecast")
    suspend fun getWeatherForecast(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "standard"
    ): NextFiveWeatherResponse
}