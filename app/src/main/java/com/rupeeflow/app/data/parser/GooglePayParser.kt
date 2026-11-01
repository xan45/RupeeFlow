package com.rupeeflow.app.data.parser

import com.rupeeflow.app.domain.model.Transaction
import com.rupeeflow.app.domain.model.TransactionSource
import com.rupeeflow.app.domain.model.TransactionType
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.util.UUID

class GooglePayParser : StatementParser {

    override val providerName: String = "Google Pay"

    override fun canParse(headerLines: List<String>): Boolean {
        val combined = headerLines.joinToString(" ").lowercase()
        return combined.contains("gpay") || 
               combined.contains("google pay") || 
               (combined.contains("description") && combined.contains("type") && combined.contains("source"))
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
        var typeIdx = -1
        var amountIdx = -1

        for (i in parsedRows.indices) {
            val lower = parsedRows[i].map { it.lowercase().trim() }
            if (lower.any { it.contains("date") } && lower.any { it.contains("amount") }) {
                headerIndex = i
                dateIdx = lower.indexOfFirst { it.contains("date") }
                descIdx = lower.indexOfFirst { it.contains("description") || it.contains("merchant") }
                typeIdx = lower.indexOfFirst { it.contains("type") }
                amountIdx = lower.indexOfFirst { it.contains("amount") }
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
            val rawType = if (typeIdx != -1) cols.getOrNull(typeIdx)?.uppercase() else null
            val rawAmount = if (amountIdx != -1) cols.getOrNull(amountIdx) else cols.getOrNull(3)

            if (rawDate.isNullOrBlank() || rawDesc.isNullOrBlank()) continue

            val amount = CsvHelper.cleanAmount(rawAmount)
            if (amount <= 0.0) continue

            val type = if (rawType?.contains("CREDIT") == true || rawType == "CR") {
                TransactionType.EARNING
            } else {
                TransactionType.EXPENSE
            }

            val (cleanTitle, refNote) = CsvHelper.formatNarration(rawDesc)

            items.add(
                Transaction(
                    originalId = "gpay-$i-${UUID.randomUUID().toString().take(8)}",
                    date = CsvHelper.parseDateToMillis(rawDate),
                    title = cleanTitle,
                    rawDescription = rawDesc,
                    refNumber = if (refNote.isNotEmpty()) refNote else "GPay",
                    amount = amount,
                    type = type,
                    source = TransactionSource.GPAY,
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
