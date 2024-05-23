package com.fictivestudios.wheatherapp.ui.next

import androidx.lifecycle.MutableLiveData
import com.fictivestudios.wheatherapp.base.response.Resource
import com.fictivestudios.wheatherapp.base.viewModel.BaseViewModel
import com.fictivestudios.wheatherapp.data.repositories.WeatherRepository
import com.fictivestudios.wheatherapp.data.responses.NextFiveWeatherResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NextFiveDayWeatherViewModel @Inject constructor(val repository: WeatherRepository) :
    BaseViewModel(repository) {

    private val _getFiveDayWeatherResponse: MutableLiveData<Resource<NextFiveWeatherResponse>?> =
        MutableLiveData()
    val getFiveDayWeatherResponse: MutableLiveData<Resource<NextFiveWeatherResponse>?>
        get() = _getFiveDayWeatherResponse


   suspend fun getFiveDataWeatherData(lat: Double, long: Double)  {
        _getFiveDayWeatherResponse.value = Resource.Loading
        _getFiveDayWeatherResponse.value = repository.getFveDaysWeather(lat, long)
        _getFiveDayWeatherResponse.value = null
    }

}