package com.rupeeflow.app.domain.model

data class ImportedFile(
    val id: Long = 0,
    val fileName: String,
    val uriString: String,
    val importedDate: Long = System.currentTimeMillis(),
    val statementType: String, // CSV, PDF, EXCEL
    val bankName: String,
    val rowCount: Int,
    val closingBalance: String? = null
)
