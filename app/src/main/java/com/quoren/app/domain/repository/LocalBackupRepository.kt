package com.quoren.app.domain.repository

interface LocalBackupRepository {
    suspend fun exportUserData(): String
    suspend fun importUserData(json: String)
}