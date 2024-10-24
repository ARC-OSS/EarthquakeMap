package com.arc.earthquakemap.network

import com.squareup.moshi.Moshi
import retrofit2.Retrofit
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Call
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET

object ApiClient {

    val baseUrl = "https://earthquake.usgs.gov/fdsnws/event/1/"

    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    val apiService: ApiService by lazy{
        retrofit.create(ApiService::class.java)
    }
}

interface ApiService {

    @GET("query?format=geojson&minmagnitude=5.5")
    fun getEarthquakes() : Call<EarthquakeList>
}
