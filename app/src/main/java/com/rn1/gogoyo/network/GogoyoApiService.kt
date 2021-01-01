package com.rn1.gogoyo.network

import com.rn1.gogoyo.BuildConfig
import com.rn1.gogoyo.model.source.CurrentWeatherResponse
import com.rn1.gogoyo.model.source.WeatherResponse
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import retrofit2.http.GET
import retrofit2.http.Query


private const val HOST_NAME = "api.openweathermap.org"
private const val API_KEY = "c85e691f3438722f98f9193c008f89f2"
private const val BASE_URL = "https://$HOST_NAME/"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val client = OkHttpClient.Builder()
    .addInterceptor(HttpLoggingInterceptor().apply {
        level = when (BuildConfig.LOGGER_VISIABLE) {
            true -> HttpLoggingInterceptor.Level.BODY
            false -> HttpLoggingInterceptor.Level.NONE
        }
    })
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .client(client)
    .build()

interface GogoyoApiService {

    @GET("data/2.5/forecast")
    suspend fun getForecast(@Query("id") id: String,
                            @Query("appid") appid: String = API_KEY,
                            @Query("lang") lang: String = "zh_tw",
                            @Query("units") units: String = "metric",
                            @Query("mode") mode: String = "json"): WeatherResponse

    @GET("data/2.5/weather")
    suspend fun getCurrentWeather(@Query("lat") lat: Double,
                                  @Query("lon") lon: Double,
                                  @Query("appid") appid: String = API_KEY,
                                  @Query("lang") lang: String = "zh_tw",
                                  @Query("units") units: String = "metric",
                                  @Query("mode") mode: String = "json"): CurrentWeatherResponse

}

object GogoyoApi {
    val retrofitService: GogoyoApiService by lazy { retrofit.create(GogoyoApiService::class.java) }
}

