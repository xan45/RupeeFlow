package com.rupeeflow.app.ui.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.rupeeflow.app.domain.model.FinancialHealthSummary
import com.rupeeflow.app.domain.model.Transaction
import com.rupeeflow.app.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class DashboardUiState(
    val summary: FinancialHealthSummary = FinancialHealthSummary(
        netCashFlow = 0.0,
        totalEarnings = 0.0,
        totalExpenses = 0.0,
        earningCount = 0,
        expenseCount = 0,
        transactionCount = 0,
        savingsRate = 0.0,
        expenseToIncomeRatio = 0.0,
        latestClosingBalance = null,
        healthScore = 50
    ),
    val recentTransactions: List<Transaction> = emptyList()
)

class DashboardViewModel(
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    val allTransactions: StateFlow<List<Transaction>> = transactionRepository.getAllTransactions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val financialSummary: StateFlow<FinancialHealthSummary> = transactionRepository.getFinancialHealthSummary(null)
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            FinancialHealthSummary(0.0, 0.0, 0.0, 0, 0, 0, 0.0, 0.0, null, 50)
        )

    fun addTransaction(transaction: Transaction) {
        viewModelScope.launch {
            transactionRepository.addTransaction(transaction)
        }
    }

    fun deleteTransaction(id: Long) {
        viewModelScope.launch {
            transactionRepository.deleteTransaction(id)
        }
    }

    companion object {
        fun provideFactory(repository: TransactionRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return DashboardViewModel(repository) as T
                }
            }
    }
}
