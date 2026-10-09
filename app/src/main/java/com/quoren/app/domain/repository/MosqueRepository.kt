package com.quoren.app.domain.repository

import com.quoren.app.domain.model.Mosque
import com.quoren.app.domain.model.PrayerLocation
import kotlinx.coroutines.flow.Flow

interface MosqueRepository {
    suspend fun searchNearby(location: PrayerLocation, radiusMeters: Int = 5_000): List<Mosque>
    fun observeFavorites(): Flow<List<Mosque>>
    suspend fun toggleFavorite(mosque: Mosque)
    suspend fun replaceFavorites(mosques: List<Mosque>)
    fun observeLastSearch(): Flow<List<Mosque>>
}