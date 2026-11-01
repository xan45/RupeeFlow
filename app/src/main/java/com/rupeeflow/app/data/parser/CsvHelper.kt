package com.rupeeflow.app.data.parser

import java.text.SimpleDateFormat
import java.util.Locale

object CsvHelper {

    fun parseCsvLine(line: String): List<String> {
        val result = mutableListOf<String>()
        val cur = StringBuilder()
        var inQuotes = false
        var i = 0

        while (i < line.length) {
            val ch = line[i]
            if (ch == '"') {
                if (inQuotes && i + 1 < line.length && line[i + 1] == '"') {
                    cur.append('"')
                    i++ // Skip escaped quote
                } else {
                    inQuotes = !inQuotes
                }
            } else if (ch == ',' && !inQuotes) {
                result.add(cur.toString().trim())
                cur.clear()
            } else {
                cur.append(ch)
            }
            i++
        }
        result.add(cur.toString().trim())
        return result
    }

    fun cleanAmount(raw: String?): Double {
        if (raw.isNullOrBlank()) return 0.0
        val sanitized = raw.replace(",", "").replace(Regex("[^0-9.-]"), "")
        return sanitized.toDoubleOrNull() ?: 0.0
    }

    fun parseDateToMillis(rawDate: String?): Long {
        if (rawDate.isNullOrBlank()) return System.currentTimeMillis()
        val trimmed = rawDate.trim()

        val formats = listOf(
            "dd-MM-yyyy HH:mm:ss",
            "dd-MM-yyyy",
            "dd/MM/yyyy HH:mm:ss",
            "dd/MM/yyyy",
            "yyyy-MM-dd HH:mm:ss",
            "yyyy-MM-dd",
            "dd-MMM-yyyy",
            "dd MMM yyyy"
        )

        for (pattern in formats) {
            try {
                val sdf = SimpleDateFormat(pattern, Locale.ENGLISH)
                sdf.isLenient = true
                val date = sdf.parse(trimmed)
                if (date != null) return date.time
            } catch (_: Exception) {
            }
        }

        // Try extracting first date token if there is extra time
        val dateToken = trimmed.split(Regex("\\s+")).firstOrNull()
        if (dateToken != null && dateToken != trimmed) {
            return parseDateToMillis(dateToken)
        }

        return System.currentTimeMillis()
    }

    fun formatNarration(rawDesc: String?): Pair<String, String> {
        if (rawDesc.isNullOrBlank()) return Pair("Bank Transaction", "")
        val trimmed = rawDesc.trim()

        if (trimmed.startsWith("UPI/", ignoreCase = true)) {
            val parts = trimmed.split('/')
            val payee = parts.getOrNull(1)?.trim()?.ifEmpty { "UPI Merchant" } ?: "UPI Merchant"
            val bankCode = parts.getOrNull(2)?.trim() ?: ""
            val rrn = parts.getOrNull(3)?.trim() ?: ""
            val refNote = if (bankCode.isNotEmpty()) {
                if (rrn.length >= 4) "UPI • $bankCode • ${rrn.takeLast(4)}" else "UPI • $bankCode"
            } else {
                "UPI"
            }
            return Pair(payee, refNote)
        }

        if (trimmed.startsWith("NEFT ", ignoreCase = true)) {
            val parts = trimmed.split(Regex("\\s+"))
            val company = if (parts.size >= 4) {
                parts.subList(2, parts.size - 1).joinToString(" ")
            } else {
                trimmed
            }
            return Pair("NEFT • $company", parts.getOrNull(1) ?: "NEFT")
        }

        if (trimmed.startsWith("INT.PD:", ignoreCase = true)) {
            return Pair("Savings Interest Credit", trimmed)
        }

        if (trimmed.startsWith("CHRG:", ignoreCase = true)) {
            return Pair(trimmed.replace(Regex("^CHRG:", RegexOption.IGNORE_CASE), "Bank Charge: "), "Service Fee")
        }

        return Pair(trimmed, "")
    }

    fun canonicalPartyName(title: String?): String {
        if (title.isNullOrBlank()) return "UNKNOWN PARTY"
        val clean = title
            .replace(Regex("^NEFT\\s*•\\s*", RegexOption.IGNORE_CASE), "")
            .replace(Regex("\\s+"), " ")
            .trim()
        return clean.uppercase(Locale.ENGLISH)
    }
}
