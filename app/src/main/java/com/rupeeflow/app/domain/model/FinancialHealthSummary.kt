package com.rupeeflow.app.domain.model

data class FinancialHealthSummary(
    val netCashFlow: Double,
    val totalEarnings: Double,
    val totalExpenses: Double,
    val earningCount: Int,
    val expenseCount: Int,
    val transactionCount: Int,
    val savingsRate: Double, // Percentage
    val expenseToIncomeRatio: Double,
    val latestClosingBalance: String? = null,
    val healthScore: Int // 0 - 100
)
