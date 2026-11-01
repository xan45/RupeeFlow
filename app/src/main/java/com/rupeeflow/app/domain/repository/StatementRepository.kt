package com.rupeeflow.app.domain.repository

import android.net.Uri
import com.rupeeflow.app.data.parser.ParsedStatementResult
import com.rupeeflow.app.domain.model.ImportedFile
import kotlinx.coroutines.flow.Flow

interface StatementRepository {
    suspend fun parseFromUri(uri: Uri, providerHint: String? = null): ParsedStatementResult
    suspend fun parseFromText(text: String, providerHint: String? = null): ParsedStatementResult
    suspend fun saveImportedStatement(
        result: ParsedStatementResult,
        uriString: String,
        fileName: String,
        replaceExisting: Boolean
    ): Long
    fun getAllImportedFiles(): Flow<List<ImportedFile>>
    suspend fun deleteImportedFile(fileId: Long)
}
