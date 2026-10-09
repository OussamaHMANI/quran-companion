package com.quoren.app.domain.usecase

import com.quoren.app.domain.model.Mosque
import com.quoren.app.domain.model.PrayerLocation
import com.quoren.app.domain.repository.MosqueRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class SearchNearbyMosquesUseCase @Inject constructor(private val mosques: MosqueRepository) {
    suspend operator fun invoke(location: PrayerLocation, radiusMeters: Int = 5_000): List<Mosque> =
        mosques.searchNearby(location, radiusMeters)
}

class ObserveFavoriteMosquesUseCase @Inject constructor(private val mosques: MosqueRepository) {
    operator fun invoke(): Flow<List<Mosque>> = mosques.observeFavorites()
}

class ObserveLastMosqueSearchUseCase @Inject constructor(private val mosques: MosqueRepository) {
    operator fun invoke(): Flow<List<Mosque>> = mosques.observeLastSearch()
}

class ToggleMosqueFavoriteUseCase @Inject constructor(private val mosques: MosqueRepository) {
    suspend operator fun invoke(mosque: Mosque) = mosques.toggleFavorite(mosque)
}