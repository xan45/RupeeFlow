package com.rupeeflow.app.data.parser

import com.rupeeflow.app.domain.model.Transaction

data class ParsedStatementResult(
    val items: List<Transaction>,
    val detectedClosingBalance: String? = null,
    val bankOrProviderName: String,
    val statementType: String = "CSV"
)
