package com.fictivestudios.wheatherapp.data.responses


data class NextFiveWeatherResponse(
    val cod: Int,
    val message: Int,
    val cnt: Int,
    val list: List<WeatherList>,
    val city: City
)


data class WeatherList(
    val dt: Int,
    val main: Main,
    val weather: List<WeatherDataResponse>,
    val clouds: Clouds,
    val wind: Wind,
    val visibility: Int,
    val pop: Int,
    val sys: Sys,
    val dt_txt: String
)

data class Coordinates(
    val lat: Double,
    val lon: Double
)

data class Clouds(val all: Int)

data class Main(
    val temp: Double,
    val feels_like: Double,
    val temp_min: Double,
    val temp_max: Double,
    val pressure: Int,
    val sea_level: Int,
    val grnd_level: Int,
    val humidity: Int,
    val temp_kf: Double
)


data class WeatherDataResponse(

    val id: Int,
    val main: String,
    val description: String,
    val icon: String
)

data class City(
    val id: Int,
    val name: String,
    val coord: Coordinates,
    val country: String,
    val population: Int,
    val timezone: Int,
    val sunrise: Int,
    val sunset: Int
)

data class Wind(
    val speed: Double,
    val deg: Int,
    val gust: Double
)

data class Sys(val pod: String)