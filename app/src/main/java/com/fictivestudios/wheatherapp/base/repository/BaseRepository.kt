package com.fictivestudios.wheatherapp.base.repository

import android.util.Log
import com.fictivestudios.wheatherapp.base.network.SafeApiCall
import com.fictivestudios.wheatherapp.base.preference.DataPreference
import com.fictivestudios.wheatherapp.base.preference.DataPreference.Companion.LOCATION_DATA
import com.fictivestudios.wheatherapp.data.responses.LocationData
import com.google.gson.Gson

import javax.inject.Singleton

@Singleton
abstract class BaseRepository(
    val preferences: DataPreference
) : SafeApiCall {


    suspend fun getLocationData(): LocationData? {
        return Gson().fromJson(
            preferences.getStringData(LOCATION_DATA),
            LocationData::class.java
        )
    }

    suspend fun saveLocationData(location: LocationData) {
        Log.d("location",location.toString())
        preferences.setStringData(LOCATION_DATA, Gson().toJson(location))
    }

}