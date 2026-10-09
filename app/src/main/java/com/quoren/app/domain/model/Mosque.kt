package com.quoren.app.domain.model

data class Mosque(
    val id: String,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val address: String? = null,
    val phone: String? = null,
    val website: String? = null,
    val distanceMeters: Double = 0.0,
    val rating: Float? = null,
    val isFavorite: Boolean = false,
) {
    init {
        require(id.isNotBlank())
        require(latitude in -90.0..90.0)
        require(longitude in -180.0..180.0)
    }
}