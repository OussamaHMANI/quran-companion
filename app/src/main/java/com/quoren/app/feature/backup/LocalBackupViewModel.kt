package com.quoren.app.feature.backup

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quoren.app.R
import com.quoren.app.domain.usecase.ExportUserBackupUseCase
import com.quoren.app.domain.usecase.ImportUserBackupUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.ByteArrayOutputStream
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class LocalBackupUiState(val messageResource: Int? = null, val busy: Boolean = false)

@HiltViewModel
class LocalBackupViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val exportBackup: ExportUserBackupUseCase,
    private val importBackup: ImportUserBackupUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(LocalBackupUiState())
    val state: StateFlow<LocalBackupUiState> = _state.asStateFlow()

    fun exportTo(uri: Uri) {
        viewModelScope.launch {
            _state.value = LocalBackupUiState(busy = true)
            val success = runCatching {
                val json = withContext(Dispatchers.IO) { exportBackup() }
                require(json.toByteArray(Charsets.UTF_8).size <= MAX_BACKUP_BYTES)
                withContext(Dispatchers.IO) {
                    context.contentResolver.openOutputStream(uri, "wt")?.bufferedWriter(Charsets.UTF_8)?.use { it.write(json) }
                        ?: error("Backup destination is unavailable")
                }
            }.isSuccess
            _state.value = LocalBackupUiState(messageResource = if (success) R.string.backup_export_success else R.string.backup_file_error)
        }
    }

    fun importFrom(uri: Uri) {
        viewModelScope.launch {
            _state.value = LocalBackupUiState(busy = true)
            val success = runCatching {
                val json = withContext(Dispatchers.IO) {
                    val input = context.contentResolver.openInputStream(uri) ?: error("Backup file is unavailable")
                    input.use { stream ->
                        val output = ByteArrayOutputStream()
                        val buffer = ByteArray(BUFFER_SIZE)
                        var total = 0
                        while (true) {
                            val count = stream.read(buffer)
                            if (count < 0) break
                            total += count
                            require(total <= MAX_BACKUP_BYTES) { "Backup file is too large" }
                            output.write(buffer, 0, count)
                        }
                        output.toString(Charsets.UTF_8.name())
                    }
                }
                importBackup(json)
            }.isSuccess
            _state.value = LocalBackupUiState(messageResource = if (success) R.string.backup_import_success else R.string.backup_file_error)
        }
    }

    fun clearMessage() {
        _state.value = _state.value.copy(messageResource = null)
    }

    companion object {
        private const val MAX_BACKUP_BYTES = 5_000_000
        private const val BUFFER_SIZE = 8_192
    }
}