package com.rupeeflow.app.data.repository

import android.content.Context
import android.net.Uri
import com.rupeeflow.app.data.local.dao.ImportedFileDao
import com.rupeeflow.app.data.local.dao.TransactionDao
import com.rupeeflow.app.data.local.entity.ImportedFileEntity
import com.rupeeflow.app.data.local.entity.TransactionEntity
import com.rupeeflow.app.data.parser.ParsedStatementResult
import com.rupeeflow.app.data.parser.StatementParserFactory
import com.rupeeflow.app.domain.model.ImportedFile
import com.rupeeflow.app.domain.repository.StatementRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader

class StatementRepositoryImpl(
    private val context: Context,
    private val importedFileDao: ImportedFileDao,
    private val transactionDao: TransactionDao
) : StatementRepository {

    override suspend fun parseFromUri(uri: Uri, providerHint: String?): ParsedStatementResult = withContext(Dispatchers.IO) {
        val inputStream = context.contentResolver.openInputStream(uri)
            ?: throw IllegalArgumentException("Could not open file stream from URI: $uri")

        // Read first 15 lines for auto-detection
        val headerLines = mutableListOf<String>()
        val reader = BufferedReader(InputStreamReader(context.contentResolver.openInputStream(uri)!!))
        for (i in 0 until 15) {
            val line = reader.readLine() ?: break
            headerLines.add(line)
        }
        reader.close()

        val parser = StatementParserFactory.getParser(providerHint, headerLines)
        parser.parse(inputStream)
    }

    override suspend fun parseFromText(text: String, providerHint: String?): ParsedStatementResult = withContext(Dispatchers.Default) {
        val headerLines = text.lines().take(15)
        val parser = StatementParserFactory.getParser(providerHint, headerLines)
        parser.parseText(text)
    }

    override suspend fun saveImportedStatement(
        result: ParsedStatementResult,
        uriString: String,
        fileName: String,
        replaceExisting: Boolean
    ): Long = withContext(Dispatchers.IO) {
        if (replaceExisting) {
            transactionDao.deleteAll()
        }

        val fileEntity = ImportedFileEntity(
            fileName = fileName,
            uriString = uriString,
            statementType = result.statementType,
            bankName = result.bankOrProviderName,
            rowCount = result.items.size,
            closingBalance = result.detectedClosingBalance
        )
        val fileId = importedFileDao.insert(fileEntity)

        val txEntities = result.items.map { tx ->
            TransactionEntity.fromDomain(tx.copy(importedFileId = fileId))
        }
        transactionDao.insertAll(txEntities)

        fileId
    }

    override fun getAllImportedFiles(): Flow<List<ImportedFile>> {
        return importedFileDao.getAllFiles().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun deleteImportedFile(fileId: Long) = withContext(Dispatchers.IO) {
        transactionDao.deleteByFileId(fileId)
        importedFileDao.deleteById(fileId)
    }
}
