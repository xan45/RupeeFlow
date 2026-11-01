package com.rupeeflow.app.domain.model

enum class TransactionType(val rawValue: String, val displayName: String, val prefix: String) {
    EXPENSE("expense", "Expense", "-₹"),
    EARNING("earning", "Earning", "+₹");

    companion object {
        fun fromString(value: String): TransactionType {
            return if (value.equals("earning", ignoreCase = true) || 
                       value.equals("credit", ignoreCase = true) || 
                       value.equals("cr", ignoreCase = true)) {
                EARNING
            } else {
                EXPENSE
            }
        }
    }
}
