package com.rupeeflow.app.domain.model

data class Transaction(
    val id: Long = 0,
    val originalId: String = "",
    val date: Long, // Epoch timestamp in milliseconds
    val title: String,
    val rawDescription: String = "",
    val refNumber: String = "",
    val amount: Double,
    val type: TransactionType,
    val source: TransactionSource,
    val category: String,
    val importedFileId: Long? = null,
    val createdAt: Long = System.currentTimeMillis()
)
