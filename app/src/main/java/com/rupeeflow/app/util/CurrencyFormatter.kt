package com.rupeeflow.app.util

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

object CurrencyFormatter {

    fun formatINR(amount: Double, includeSymbol: Boolean = true, includeDecimals: Boolean = true): String {
        val absAmount = Math.abs(amount)
        val symbols = DecimalFormatSymbols(Locale.ENGLISH).apply {
            groupingSeparator = ','
            decimalSeparator = '.'
        }

        val pattern = if (includeDecimals) "#,##,##0.00" else "#,##,##0"
        val df = DecimalFormat(pattern, symbols)
        val formatted = df.format(absAmount)

        return if (includeSymbol) "₹$formatted" else formatted
    }

    fun formatSignedINR(amount: Double): String {
        val sign = if (amount < 0) "-" else "+"
        return "$sign${formatINR(amount)}"
    }
}
