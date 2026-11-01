package com.rupeeflow.app.domain.model

enum class PartyRole(val displayName: String) {
    ALL("All Names"),
    DEBITOR("Receivers (Dr)"),
    CREDITOR("Senders (Cr)"),
    BOTH("Two-Way (Both)")
}

data class PartySummary(
    val name: String,
    val creditTotal: Double,
    val debitTotal: Double,
    val creditCount: Int,
    val debitCount: Int,
    val totalCount: Int,
    val firstDate: Long,
    val lastDate: Long,
    val category: String,
    val netFlow: Double = creditTotal - debitTotal
) {
    val role: PartyRole
        get() = when {
            creditCount > 0 && debitCount > 0 -> PartyRole.BOTH
            creditCount > 0 -> PartyRole.CREDITOR
            else -> PartyRole.DEBITOR
        }
}
