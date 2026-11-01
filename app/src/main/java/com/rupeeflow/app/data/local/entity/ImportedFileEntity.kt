package com.rupeeflow.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.rupeeflow.app.domain.model.ImportedFile

@Entity(tableName = "imported_files")
data class ImportedFileEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val fileName: String,
    val uriString: String,
    val importedDate: Long = System.currentTimeMillis(),
    val statementType: String,
    val bankName: String,
    val rowCount: Int,
    val closingBalance: String? = null
) {
    fun toDomain(): ImportedFile {
        return ImportedFile(
            id = id,
            fileName = fileName,
            uriString = uriString,
            importedDate = importedDate,
            statementType = statementType,
            bankName = bankName,
            rowCount = rowCount,
            closingBalance = closingBalance
        )
    }

    companion object {
        fun fromDomain(domain: ImportedFile): ImportedFileEntity {
            return ImportedFileEntity(
                id = domain.id,
                fileName = domain.fileName,
                uriString = domain.uriString,
                importedDate = domain.importedDate,
                statementType = domain.statementType,
                bankName = domain.bankName,
                rowCount = domain.rowCount,
                closingBalance = domain.closingBalance
            )
        }
    }
}
