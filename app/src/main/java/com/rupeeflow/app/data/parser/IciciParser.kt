package com.rupeeflow.app.data.parser

import com.rupeeflow.app.domain.model.Transaction
import com.rupeeflow.app.domain.model.TransactionSource
import com.rupeeflow.app.domain.model.TransactionType
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.util.UUID

class IciciParser : StatementParser {

    override val providerName: String = "ICICI Bank"

    override fun canParse(headerLines: List<String>): Boolean {
        val combined = headerLines.joinToString(" ").lowercase()
        return combined.contains("icici") || 
               (combined.contains("transaction remarks") && combined.contains("withdrawal amount"))
    }

    override fun parse(inputStream: InputStream): ParsedStatementResult {
        val reader = BufferedReader(InputStreamReader(inputStream))
        return parseLines(reader.readLines())
    }

    override fun parseText(rawText: String): ParsedStatementResult {
        return parseLines(rawText.lines())
    }

    private fun parseLines(rawLines: List<String>): ParsedStatementResult {
        val lines = rawLines.map { it.trim() }.filter { it.isNotEmpty() }
        val parsedRows = lines.map { CsvHelper.parseCsvLine(it) }

        var headerIndex = -1
        var dateIdx = -1
        var descIdx = -1
        var refIdx = -1
        var drIdx = -1
        var crIdx = -1

        for (i in parsedRows.indices) {
            val lower = parsedRows[i].map { it.lowercase().trim() }
            if (lower.any { it.contains("remarks") || it.contains("particulars") || it.contains("description") } && 
                lower.any { it.contains("withdrawal") || it.contains("debit") }) {
                headerIndex = i
                val valDate = lower.indexOfFirst { it == "value date" }
                val txDate = lower.indexOfFirst { it.contains("date") }
                dateIdx = if (valDate != -1) valDate else txDate
                descIdx = lower.indexOfFirst { it.contains("remarks") || it.contains("particulars") || it.contains("description") }
                refIdx = lower.indexOfFirst { it.contains("ref") || it.contains("cheque") }
                drIdx = lower.indexOfFirst { it.contains("withdrawal") || it == "debit" }
                crIdx = lower.indexOfFirst { it.contains("deposit") || it == "credit" }
                break
            }
        }

        val items = mutableListOf<Transaction>()
        val startRow = if (headerIndex != -1) headerIndex + 1 else 0

        for (i in startRow until parsedRows.size) {
            val cols = parsedRows[i]
            if (cols.size < 3) continue

            val rawDate = if (dateIdx != -1) cols.getOrNull(dateIdx) else cols.getOrNull(0)
            val rawDesc = if (descIdx != -1) cols.getOrNull(descIdx) else cols.getOrNull(1)

            if (rawDate.isNullOrBlank() || rawDesc.isNullOrBlank()) continue

            val drVal = if (drIdx != -1) CsvHelper.cleanAmount(cols.getOrNull(drIdx)) else 0.0
            val crVal = if (crIdx != -1) CsvHelper.cleanAmount(cols.getOrNull(crIdx)) else 0.0

            val (amount, type) = when {
                crVal > 0.0 -> Pair(crVal, TransactionType.EARNING)
                drVal > 0.0 -> Pair(drVal, TransactionType.EXPENSE)
                else -> continue
            }

            val (cleanTitle, refNote) = CsvHelper.formatNarration(rawDesc)
            val refNo = if (refIdx != -1 && !cols.getOrNull(refIdx).isNullOrBlank()) cols[refIdx] else refNote

            items.add(
                Transaction(
                    originalId = "icici-$i-${UUID.randomUUID().toString().take(8)}",
                    date = CsvHelper.parseDateToMillis(rawDate),
                    title = cleanTitle,
                    rawDescription = rawDesc,
                    refNumber = refNo,
                    amount = amount,
                    type = type,
                    source = TransactionSource.BANK,
                    category = CategoryRuleMatcher.match(rawDesc, type)
                )
            )
        }

        return ParsedStatementResult(
            items = items,
            bankOrProviderName = providerName,
            statementType = "CSV"
        )
    }
}
