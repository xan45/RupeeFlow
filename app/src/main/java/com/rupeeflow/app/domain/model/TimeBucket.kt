package com.rupeeflow.app.domain.model

enum class TimeGranularity(val title: String) {
    DAY("Day"),
    WEEK("Week"),
    MONTH("Month"),
    YEAR("Year")
}

data class TimeBucket(
    val key: String,
    val label: String,
    val subLabel: String,
    val creditTotal: Double,
    val debitTotal: Double,
    val creditCount: Int,
    val debitCount: Int,
    val transactions: List<Transaction> = emptyList(),
    val collatedParties: List<PartySummary> = emptyList()
) {
    val netFlow: Double get() = creditTotal - debitTotal
    val totalCount: Int get() = creditCount + debitCount
}
