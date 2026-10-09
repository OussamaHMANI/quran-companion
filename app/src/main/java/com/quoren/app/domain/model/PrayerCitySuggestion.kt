package com.quoren.app.domain.model

data class PrayerLandmark(
    val name: String,
    val latitude: Double,
    val longitude: Double,
)

data class PrayerCitySuggestion(
    val location: PrayerLocation,
    val mainMosque: PrayerLandmark? = null,
)