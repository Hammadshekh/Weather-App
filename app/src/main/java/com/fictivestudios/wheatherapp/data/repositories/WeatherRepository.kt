package com.fictivestudios.wheatherapp.data.repositories

import com.fictivestudios.wheatherapp.base.preference.DataPreference
import com.fictivestudios.wheatherapp.base.repository.BaseRepository
import com.fictivestudios.wheatherapp.constants.Constants.API_KEY
import com.fictivestudios.wheatherapp.data.networks.WeatherApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherRepository @Inject constructor(
    private val api: WeatherApi,
    preferences: DataPreference
) : BaseRepository(preferences) {

    suspend fun getWeatherData(lat: Double, long: Double) = safeApiCall {
        api.getWeather(lat, long, API_KEY)
    }

    suspend fun getFveDaysWeather(lat: Double, long: Double) = safeApiCall {
        api.getWeatherForecast(lat, long, API_KEY)
    }


}