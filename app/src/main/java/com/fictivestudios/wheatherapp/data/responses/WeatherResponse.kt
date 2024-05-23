package com.fictivestudios.wheatherapp.data.responses

data class LocationData(

    val city: String,
    val currentLatitude: Double = 0.0,
    val currentLongitude: Double = 0.0,
)

data class WeatherData(val image:Int,val title:String,val measurement:String)

data class LocationEvent(
    val latitude:Double?,
    val longitude:Double?
)

data class WeatherResponse(
    val main: CurrentWeatherData,
    val weather: List<Weather>,
    val name: String
)

data class CurrentWeatherData(
    val temp: Double,
    val pressure: Int,
    val humidity: Int
)

data class Weather(
    val description: String,
    val icon: String
)


data class TodayWeatherResponse (

    val coord : Coordinates,
    val weather : List<CurrentWeatherResponse>,
    val base : String,
    val main : CurrentWeatherMainData,
    val visibility : Int,
    val wind : Wind,
    val clouds : Clouds,
    val dt : Int,
    val sys : CurrentWeatherSystemData,
    val timezone : Int,
    val id : Int,
    val name : String,
    val cod : Int
)

data class CurrentWeatherSystemData (
    val type : Int,
    val id : Int,
    val country : String,
    val sunrise : Int,
    val sunset : Int
)
data class CurrentWeatherResponse (
    val id : Int,
    val main : String,
    val description : String,
    val icon : String
)

data class CurrentWeatherMainData (
    val temp : Double,
    val feels_like : Double,
    val temp_min : Double,
    val temp_max : Double,
    val pressure : Int,
    val humidity : Int
)