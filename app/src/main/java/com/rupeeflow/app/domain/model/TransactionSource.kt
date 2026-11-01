package com.rupeeflow.app.domain.model

enum class TransactionSource(val code: String, val displayName: String) {
    BANK("Bank", "Bank A/C"),
    GPAY("GPay", "Google Pay"),
    PHONEPE("PhonePe", "PhonePe"),
    MAILBOX("Mailbox", "Mailbox Alert"),
    MANUAL("Manual", "Cash / Manual");

    companion object {
        fun fromString(value: String): TransactionSource {
            return entries.firstOrNull { 
                it.code.equals(value, ignoreCase = true) || 
                it.displayName.equals(value, ignoreCase = true) 
            } ?: BANK
        }
    }
}
