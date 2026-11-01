package com.rupeeflow.app.ui.screens.importstatement

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.rupeeflow.app.data.parser.ParsedStatementResult
import com.rupeeflow.app.domain.repository.StatementRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ImportUiState(
    val providerHint: String = "AUTO",
    val statementText: String = "",
    val replaceExisting: Boolean = true,
    val previewCount: Int = 0,
    val isProcessing: Boolean = false,
    val statusMessage: String? = null,
    val lastImportedCount: Int? = null
)

class ImportStatementViewModel(
    private val statementRepository: StatementRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ImportUiState())
    val uiState = _uiState.asStateFlow()

    fun updateProviderHint(hint: String) {
        _uiState.value = _uiState.value.copy(providerHint = hint)
        recomputePreview()
    }

    fun updateStatementText(text: String) {
        _uiState.value = _uiState.value.copy(statementText = text)
        recomputePreview()
    }

    fun updateReplaceExisting(replace: Boolean) {
        _uiState.value = _uiState.value.copy(replaceExisting = replace)
    }

    fun clearStatementText() {
        _uiState.value = _uiState.value.copy(statementText = "", previewCount = 0, statusMessage = null)
    }

    fun dismissStatusMessage() {
        _uiState.value = _uiState.value.copy(statusMessage = null)
    }

    private fun recomputePreview() {
        val lines = _uiState.value.statementText.lines().map { it.trim() }.filter { it.isNotEmpty() }
        _uiState.value = _uiState.value.copy(previewCount = Math.max(0, lines.size - 1))
    }

    fun importFromUri(uri: Uri, fileName: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isProcessing = true, statusMessage = null)
            try {
                val result = statementRepository.parseFromUri(uri, _uiState.value.providerHint)
                statementRepository.saveImportedStatement(
                    result = result,
                    uriString = uri.toString(),
                    fileName = fileName,
                    replaceExisting = _uiState.value.replaceExisting
                )
                _uiState.value = _uiState.value.copy(
                    isProcessing = false,
                    lastImportedCount = result.items.size,
                    statusMessage = "Successfully imported ${result.items.size} transactions from $fileName!"
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isProcessing = false,
                    statusMessage = "Import failed: ${e.localizedMessage ?: "Unknown error"}"
                )
            }
        }
    }

    fun parseAndImportText() {
        val text = _uiState.value.statementText
        if (text.isBlank()) {
            _uiState.value = _uiState.value.copy(statusMessage = "Please paste or load statement CSV content.")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isProcessing = true, statusMessage = null)
            try {
                val result = statementRepository.parseFromText(text, _uiState.value.providerHint)
                statementRepository.saveImportedStatement(
                    result = result,
                    uriString = "text://manual_paste",
                    fileName = "Pasted Statement (${result.bankOrProviderName})",
                    replaceExisting = _uiState.value.replaceExisting
                )
                _uiState.value = _uiState.value.copy(
                    isProcessing = false,
                    lastImportedCount = result.items.size,
                    statusMessage = "Successfully imported ${result.items.size} transactions!"
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isProcessing = false,
                    statusMessage = "Parsing error: ${e.localizedMessage}"
                )
            }
        }
    }

    companion object {
        fun provideFactory(repository: StatementRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return ImportStatementViewModel(repository) as T
                }
            }
    }
}
