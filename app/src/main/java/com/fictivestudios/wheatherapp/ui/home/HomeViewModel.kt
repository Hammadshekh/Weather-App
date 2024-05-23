package com.fictivestudios.wheatherapp.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.fictivestudios.wheatherapp.base.response.Resource
import com.fictivestudios.wheatherapp.base.viewModel.BaseViewModel
import com.fictivestudios.wheatherapp.data.repositories.WeatherRepository
import com.fictivestudios.wheatherapp.data.responses.NextFiveWeatherResponse
import com.fictivestudios.wheatherapp.data.responses.TodayWeatherResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val repository: WeatherRepository) :
    BaseViewModel(repository) {

    private val _getTodayWeatherData: MutableLiveData<Resource<TodayWeatherResponse>?> =
        MutableLiveData()
    val getTodayWeatherData: MutableLiveData<Resource<TodayWeatherResponse>?>
        get() = _getTodayWeatherData

    private val _getFiveDayWeatherResponse: MutableLiveData<Resource<NextFiveWeatherResponse>?> =
        MutableLiveData()
    val getFiveDayWeatherResponse: MutableLiveData<Resource<NextFiveWeatherResponse>?>
        get() = _getFiveDayWeatherResponse


    fun getTodayWeather(lat: Double, long: Double) = viewModelScope.launch {
        _getTodayWeatherData.value = Resource.Loading
        _getTodayWeatherData.value = repository.getWeatherData(lat, long)
        _getTodayWeatherData.value = null
    }

    fun getFiveDataWeatherData(lat: Double, long: Double) = viewModelScope.launch {
        _getFiveDayWeatherResponse.value = Resource.Loading
        _getFiveDayWeatherResponse.value = repository.getFveDaysWeather(lat, long)
        _getFiveDayWeatherResponse.value = null

    }
}