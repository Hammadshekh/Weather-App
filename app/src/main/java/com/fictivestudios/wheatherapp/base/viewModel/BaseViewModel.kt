package com.fictivestudios.wheatherapp.base.viewModel

import androidx.lifecycle.ViewModel
import com.fictivestudios.wheatherapp.base.repository.BaseRepository
import com.fictivestudios.wheatherapp.data.responses.LocationData


abstract class BaseViewModel(private val repository: BaseRepository) : ViewModel() {

    suspend fun fetchLocationData() = repository.getLocationData()

    suspend fun saveLocationData(data: LocationData)  {
        repository.saveLocationData(data)
    }
}