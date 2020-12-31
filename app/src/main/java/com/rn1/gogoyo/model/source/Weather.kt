package com.rn1.gogoyo.model.source



data class City(
    val coord: Coord?,
    val country: String?,
    val id: Int?,
    val name: String?
)

data class Clouds(
    val all: Int?
)

data class Coord(
    val lat: Double?,
    val lon: Double?
)

data class Main(
    val grnd_level: Int?,
    val humidity: Int?,
    val pressure: Int?,
    val sea_level: Double?,
    val temp: Double?,
    val temp_kf: Double?,
    val temp_max: Double?,
    val temp_min: Double?,
    val feels_like: Double?,
) {
    fun getTempC(): Int? = temp?.toInt()
}

class Snow(
)

data class Weather(
    val description: String?,
    val icon: String?,
    val id: Int?,
    val main: String?
)

data class Sys(
    val pod: String?,
    val type: Int?,
    val id: Int?,
    val message: Double?,
    val country: String?,
    val sunrise: Long?,
    val sunset: Long?
)

data class Wind(
    val deg: Double?,
    val speed: Double?
)

data class X(
    val clouds: Clouds?,
    val dt: Long?,
    val dt_txt: String?,
    val main: Main?,
    val snow: Snow?,
    val sys: Sys?,
    val weather: List<Weather>?,
    val wind: Wind?
)

data class WeatherResponse(
    val city: City?,
    val cnt: Int?,
    val cod: String?,
    val list: List<X>?,
    val message: Double?
)

data class CurrentWeatherResponse(
    val coord: Coord?,
    val weather: List<Weather>?,
    val base: String?,
    val main: Main?,
    val visibility: Int?,
    val wind: Wind?,
    val clouds: Clouds?,
    val dt: Long?,
    val sys: Sys?,
    val timezone: Int?,
    val id: Int?,
    val name: String?,
    val cod: Int?
)