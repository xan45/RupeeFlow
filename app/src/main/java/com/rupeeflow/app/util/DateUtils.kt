package com.rupeeflow.app.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateUtils {

    fun formatDisplayDate(epochMillis: Long): String {
        val sdf = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
        return sdf.format(Date(epochMillis))
    }

    fun formatShortDate(epochMillis: Long): String {
        val sdf = SimpleDateFormat("dd/MM/yy", Locale.ENGLISH)
        return sdf.format(Date(epochMillis))
    }

    fun formatFullDateTime(epochMillis: Long): String {
        val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.ENGLISH)
        return sdf.format(Date(epochMillis))
    }

    fun parseInputDate(dateStr: String): Long? {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
        return try {
            sdf.parse(dateStr)?.time
        } catch (_: Exception) {
            null
        }
    }

    fun toIsoDateString(epochMillis: Long): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
        return sdf.format(Date(epochMillis))
    }
}
