package com.quoren.app.domain.usecase

import com.quoren.app.domain.repository.LocalBackupRepository
import javax.inject.Inject

class ExportUserBackupUseCase @Inject constructor(private val backups: LocalBackupRepository) {
    suspend operator fun invoke(): String = backups.exportUserData()
}

class ImportUserBackupUseCase @Inject constructor(private val backups: LocalBackupRepository) {
    suspend operator fun invoke(json: String) = backups.importUserData(json)
}