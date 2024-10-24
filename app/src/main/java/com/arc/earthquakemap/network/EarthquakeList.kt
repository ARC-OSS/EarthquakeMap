package com.arc.earthquakemap.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class EarthquakeList(

    @Json(name="metadata")
    val metadata: Metadata,
    @Json(name="features")
    val features: List<Feature>,
)

@JsonClass(generateAdapter = true)
data class Metadata (

    @Json(name="title")
    val title: String,
    @Json(name="count")
    val count: Int,
)

@JsonClass(generateAdapter = true)
data class Feature (

    @Json(name="properties")
    val properties: Properties,
    @Json(name="geometry")
    val geometry: Geometry,
)

@JsonClass(generateAdapter = true)
data class Properties (

    @Json(name="title")
    val title: String,
    @Json(name="mag")
    val mag: Double,
)

@JsonClass(generateAdapter = true)
data class Geometry (

    @Json(name="coordinates")
    val coordinates: List<Double>,
)