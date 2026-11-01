package com.rupeeflow.app.data.parser

import com.rupeeflow.app.domain.model.Transaction
import com.rupeeflow.app.domain.model.TransactionSource
import com.rupeeflow.app.domain.model.TransactionType
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.util.UUID

class GenericUpiParser : StatementParser {

    override val providerName: String = "Generic Statement / UPI"

    override fun canParse(headerLines: List<String>): Boolean {
        // Fallback parser accepts any tabular text with amounts
        return true
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
        var amountIdx = -1
        var drcrIdx = -1
        var drIdx = -1
        var crIdx = -1

        for (i in parsedRows.indices) {
            val lower = parsedRows[i].map { it.lowercase().trim() }
            val hasDesc = lower.any { it.contains("desc") || it.contains("narrat") || it.contains("particular") || it.contains("merchant") }
            val hasAmount = lower.any { it.contains("amount") || it.contains("debit") || it.contains("credit") }

            if (hasDesc && hasAmount) {
                headerIndex = i
                dateIdx = lower.indexOfFirst { it.contains("date") }
                descIdx = lower.indexOfFirst { it.contains("desc") || it.contains("narrat") || it.contains("particular") || it.contains("merchant") }
                refIdx = lower.indexOfFirst { it.contains("ref") || it.contains("chq") || it.contains("rrn") }
                amountIdx = lower.indexOfFirst { it.contains("amount") }
                drcrIdx = lower.indexOfFirst { it.contains("type") || it.contains("dr") || it.contains("cr") }
                drIdx = lower.indexOfFirst { it == "debit" || it.contains("withdrawal") }
                crIdx = lower.indexOfFirst { it == "credit" || it.contains("deposit") }
                break
            }
        }

        val items = mutableListOf<Transaction>()
        val startRow = if (headerIndex != -1) headerIndex + 1 else 0

        for (i in startRow until parsedRows.size) {
            val cols = parsedRows[i]
            if (cols.size < 2) continue

            val rawDate = if (dateIdx != -1) cols.getOrNull(dateIdx) else cols.getOrNull(0)
            val rawDesc = if (descIdx != -1) cols.getOrNull(descIdx) else cols.getOrNull(1)

            if (rawDesc.isNullOrBlank()) continue

            var amount = 0.0
            var type = TransactionType.EXPENSE

            if (amountIdx != -1 && drcrIdx != -1) {
                amount = CsvHelper.cleanAmount(cols.getOrNull(amountIdx))
                val flag = cols.getOrNull(drcrIdx)?.uppercase() ?: ""
                type = if (flag.contains("CR") || flag.contains("CREDIT") || flag.contains("IN")) {
                    TransactionType.EARNING
                } else {
                    TransactionType.EXPENSE
                }
            } else if (drIdx != -1 && crIdx != -1) {
                val drVal = CsvHelper.cleanAmount(cols.getOrNull(drIdx))
                val crVal = CsvHelper.cleanAmount(cols.getOrNull(crIdx))
                if (crVal > 0.0) {
                    amount = crVal
                    type = TransactionType.EARNING
                } else if (drVal > 0.0) {
                    amount = drVal
                    type = TransactionType.EXPENSE
                }
            } else if (amountIdx != -1) {
                val signed = CsvHelper.cleanAmount(cols.getOrNull(amountIdx))
                amount = Math.abs(signed)
                type = if (signed < 0) TransactionType.EXPENSE else TransactionType.EARNING
            }

            if (amount <= 0.0) continue

            val (cleanTitle, refNote) = CsvHelper.formatNarration(rawDesc)
            val refNo = if (refIdx != -1 && !cols.getOrNull(refIdx).isNullOrBlank()) cols[refIdx] else refNote

            items.add(
                Transaction(
                    originalId = "gen-$i-${UUID.randomUUID().toString().take(8)}",
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
