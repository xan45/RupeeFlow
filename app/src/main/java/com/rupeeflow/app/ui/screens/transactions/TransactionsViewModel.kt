package com.rupeeflow.app.ui.screens.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.rupeeflow.app.domain.model.Transaction
import com.rupeeflow.app.domain.model.TransactionSource
import com.rupeeflow.app.domain.model.TransactionType
import com.rupeeflow.app.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class TransactionsFilterState(
    val searchQuery: String = "",
    val activeFilter: String = "ALL", // "ALL", "expense", "earning", "Bank", "GPay", "PhonePe", "Mailbox"
    val partyFilter: String = "ALL",
    val fromDate: Long? = null,
    val toDate: Long? = null
)

class TransactionsViewModel(
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    val filterState = MutableStateFlow(TransactionsFilterState())

    private val allTransactions = transactionRepository.getAllTransactions()

    val filteredTransactions: StateFlow<List<Transaction>> = combine(
        allTransactions,
        filterState
    ) { txs, filters ->
        txs.filter { tx ->
            // Search query
            val q = filters.searchQuery.trim().lowercase()
            val matchesSearch = q.isEmpty() || 
                tx.title.lowercase().contains(q) || 
                tx.rawDescription.lowercase().contains(q) || 
                tx.category.lowercase().contains(q) || 
                tx.source.code.lowercase().contains(q)

            // Filter chips
            val matchesFilter = when (filters.activeFilter) {
                "ALL" -> true
                "expense" -> tx.type == TransactionType.EXPENSE
                "earning" -> tx.type == TransactionType.EARNING
                else -> tx.source.code.equals(filters.activeFilter, ignoreCase = true)
            }

            // Party filter
            val matchesParty = filters.partyFilter == "ALL" || 
                tx.title.equals(filters.partyFilter, ignoreCase = true)

            // Date range
            val matchesFrom = filters.fromDate == null || tx.date >= filters.fromDate
            val matchesTo = filters.toDate == null || tx.date <= filters.toDate

            matchesSearch && matchesFilter && matchesParty && matchesFrom && matchesTo
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun updateSearchQuery(query: String) {
        filterState.value = filterState.value.copy(searchQuery = query)
    }

    fun updateActiveFilter(filter: String) {
        filterState.value = filterState.value.copy(activeFilter = filter)
    }

    fun updatePartyFilter(party: String) {
        filterState.value = filterState.value.copy(partyFilter = party)
    }

    fun resetFilters() {
        filterState.value = TransactionsFilterState()
    }

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

    fun clearAllTransactions() {
        viewModelScope.launch {
            transactionRepository.deleteAllTransactions()
        }
    }

    companion object {
        fun provideFactory(repository: TransactionRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return TransactionsViewModel(repository) as T
                }
            }
    }
}
