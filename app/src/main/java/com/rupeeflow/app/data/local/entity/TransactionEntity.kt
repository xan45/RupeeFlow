package com.rupeeflow.app.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.rupeeflow.app.domain.model.Transaction
import com.rupeeflow.app.domain.model.TransactionSource
import com.rupeeflow.app.domain.model.TransactionType

@Entity(
    tableName = "transactions",
    indices = [
        Index("date"),
        Index("type"),
        Index("source"),
        Index("category"),
        Index("title"),
        Index("importedFileId")
    ]
)
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val originalId: String = "",
    val date: Long,
    val title: String,
    val rawDescription: String = "",
    val refNumber: String = "",
    val amount: Double,
    val type: String,
    val source: String,
    val category: String,
    val importedFileId: Long? = null,
    val createdAt: Long = System.currentTimeMillis()
) {
    fun toDomain(): Transaction {
        return Transaction(
            id = id,
            originalId = originalId,
            date = date,
            title = title,
            rawDescription = rawDescription,
            refNumber = refNumber,
            amount = amount,
            type = TransactionType.fromString(type),
            source = TransactionSource.fromString(source),
            category = category,
            importedFileId = importedFileId,
            createdAt = createdAt
        )
    }

    companion object {
        fun fromDomain(domain: Transaction): TransactionEntity {
            return TransactionEntity(
                id = domain.id,
                originalId = domain.originalId,
                date = domain.date,
                title = domain.title,
                rawDescription = domain.rawDescription,
                refNumber = domain.refNumber,
                amount = domain.amount,
                type = domain.type.rawValue,
                source = domain.source.code,
                category = domain.category,
                importedFileId = domain.importedFileId,
                createdAt = domain.createdAt
            )
        }
    }
}
