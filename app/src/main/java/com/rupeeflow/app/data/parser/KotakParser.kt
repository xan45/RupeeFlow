package com.rupeeflow.app.data.parser

import com.rupeeflow.app.domain.model.Transaction
import com.rupeeflow.app.domain.model.TransactionSource
import com.rupeeflow.app.domain.model.TransactionType
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.util.UUID

class KotakParser : StatementParser {

    override val providerName: String = "Kotak Mahindra Bank"

    override fun canParse(headerLines: List<String>): Boolean {
        val combined = headerLines.joinToString(" ").lowercase()
        return combined.contains("kotak") || 
               combined.contains("kkbk") || 
               (combined.contains("cust. reln. no.") && combined.contains("account no.")) ||
               (combined.contains("sl. no.") && combined.contains("dr / cr") && combined.contains("balance"))
    }

    override fun parse(inputStream: InputStream): ParsedStatementResult {
        val reader = BufferedReader(InputStreamReader(inputStream))
        val lines = reader.readLines()
        return parseLines(lines)
    }

    override fun parseText(rawText: String): ParsedStatementResult {
        val lines = rawText.lines()
        return parseLines(lines)
    }

    private fun parseLines(rawLines: List<String>): ParsedStatementResult {
        val lines = rawLines.map { it.trim() }.filter { it.isNotEmpty() }
        val parsedRows = lines.map { CsvHelper.parseCsvLine(it) }

        var detectedClosingBalance: String? = null
        for (cols in parsedRows) {
            val firstCell = cols.firstOrNull()?.lowercase() ?: ""
            if (firstCell == "closing balance" && cols.size > 1) {
                val m = Regex("([0-9,]+\\.\\d{2})").find(cols[1])
                if (m != null) detectedClosingBalance = m.value
            }
        }

        var headerIndex = -1
        var dateIdx = -1
        var descIdx = -1
        var refIdx = -1
        var amountIdx = -1
        var drcrIdx = -1
        var slIdx = -1

        for (i in parsedRows.indices) {
            val lower = parsedRows[i].map { it.lowercase().trim() }
            val hasDesc = lower.any { it == "description" || it == "narration" || it == "particulars" }
            val hasDate = lower.any { it.contains("date") }
            val hasAmount = lower.any { it.contains("amount") || it == "debit" }

            if (hasDesc && (hasDate || hasAmount)) {
                headerIndex = i
                slIdx = lower.indexOfFirst { it.contains("sl.") || it.contains("sr. no") }
                val valueDateIdx = lower.indexOfFirst { it == "value date" }
                val txDateIdx = lower.indexOfFirst { it.contains("date") }
                dateIdx = if (valueDateIdx != -1) valueDateIdx else txDateIdx
                descIdx = lower.indexOfFirst { it == "description" || it == "narration" || it == "particulars" }
                refIdx = lower.indexOfFirst { it.contains("ref") || it.contains("chq") }
                amountIdx = lower.indexOfFirst { it.contains("amount") }

                if (amountIdx != -1) {
                    for (k in (amountIdx + 1) until lower.size) {
                        if (lower[k].contains("dr") || lower[k].contains("type") || lower[k].contains("cr")) {
                            drcrIdx = k
                            break
                        }
                    }
                }
                break
            }
        }

        val items = mutableListOf<Transaction>()
        val startRow = if (headerIndex != -1) headerIndex + 1 else 0

        for (i in startRow until parsedRows.size) {
            val cols = parsedRows[i]
            if (cols.size < 3) continue

            val firstCell = cols.firstOrNull()?.lowercase() ?: ""
            if (firstCell.contains("closing balance") || 
                firstCell.contains("important note") || 
                firstCell.contains("transaction date refers") || 
                firstCell.contains("customer contact") || 
                firstCell.contains("write to us")) {
                continue
            }

            if (slIdx != -1) {
                val slVal = cols.getOrNull(slIdx)?.trim() ?: ""
                if (!slVal.matches(Regex("^\\d+$"))) continue
            }

            val rawDate = if (dateIdx != -1) cols.getOrNull(dateIdx) else cols.getOrNull(0)
            val rawDesc = if (descIdx != -1) cols.getOrNull(descIdx) else cols.getOrNull(1)

            if (rawDate.isNullOrBlank() || rawDesc.isNullOrBlank()) continue

            val rawAmount = if (amountIdx != -1) cols.getOrNull(amountIdx) else null
            val rawFlag = if (drcrIdx != -1) cols.getOrNull(drcrIdx)?.uppercase()?.trim() else null

            val amount = CsvHelper.cleanAmount(rawAmount)
            if (amount <= 0.0) continue

            val type = if (rawFlag == "CR" || rawFlag?.contains("CREDIT") == true) {
                TransactionType.EARNING
            } else {
                TransactionType.EXPENSE
            }

            val (cleanTitle, refNote) = CsvHelper.formatNarration(rawDesc)
            val refNo = if (refIdx != -1 && !cols.getOrNull(refIdx).isNullOrBlank()) {
                cols[refIdx]
            } else {
                refNote
            }

            items.add(
                Transaction(
                    originalId = "kotak-$i-${UUID.randomUUID().toString().take(8)}",
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
            detectedClosingBalance = detectedClosingBalance,
            bankOrProviderName = providerName,
            statementType = "CSV"
        )
    }
}
