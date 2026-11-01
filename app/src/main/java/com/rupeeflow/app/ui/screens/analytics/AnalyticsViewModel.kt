package com.rupeeflow.app.ui.screens.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.rupeeflow.app.domain.model.PartyRole
import com.rupeeflow.app.domain.model.PartySummary
import com.rupeeflow.app.domain.model.TimeBucket
import com.rupeeflow.app.domain.model.TimeGranularity
import com.rupeeflow.app.domain.model.Transaction
import com.rupeeflow.app.domain.model.TransactionType
import com.rupeeflow.app.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

enum class PartySortOption(val title: String) {
    DEBIT_DESC("Highest Expense (Debited)"),
    CREDIT_DESC("Highest Earning (Credited)"),
    COUNT_DESC("Most Frequent (# Txns)"),
    NET_ASC("Largest Net Outflow"),
    NET_DESC("Largest Net Inflow"),
    NAME_ASC("Name (A → Z)")
}

data class AnalyticsUiState(
    val activeSubTab: Int = 0, // 0: Time, 1: Top Dr/Cr, 2: Collated Names
    val timeGranularity: TimeGranularity = TimeGranularity.DAY,
    val partyRoleFilter: PartyRole = PartyRole.ALL,
    val sortOption: PartySortOption = PartySortOption.DEBIT_DESC,
    val partyDirectorySearch: String = "",
    val selectedPartyName: String = "ALL",
    val fromDate: Long? = null,
    val toDate: Long? = null
)

class AnalyticsViewModel(
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    val uiState = MutableStateFlow(AnalyticsUiState())

    private val allTransactions = transactionRepository.getAllTransactions()

    // Scoped transactions based on date range and selected party
    val scopedTransactions: StateFlow<List<Transaction>> = combine(
        allTransactions,
        uiState
    ) { txs, state ->
        txs.filter { tx ->
            val matchesParty = state.selectedPartyName == "ALL" || 
                tx.title.equals(state.selectedPartyName, ignoreCase = true)
            val matchesFrom = state.fromDate == null || tx.date >= state.fromDate
            val matchesTo = state.toDate == null || tx.date <= state.toDate
            matchesParty && matchesFrom && matchesTo
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Collated parties based on scoped transactions
    val collatedParties: StateFlow<List<PartySummary>> = combine(
        scopedTransactions,
        uiState
    ) { txs, state ->
        val rawParties = transactionRepository.getCollatedParties(txs)

        // Filter by role
        val roleFiltered = when (state.partyRoleFilter) {
            PartyRole.ALL -> rawParties
            PartyRole.DEBITOR -> rawParties.filter { it.debitCount > 0 && it.creditCount == 0 }
            PartyRole.CREDITOR -> rawParties.filter { it.creditCount > 0 && it.debitCount == 0 }
            PartyRole.BOTH -> rawParties.filter { it.creditCount > 0 && it.debitCount > 0 }
        }

        // Filter by search query
        val search = state.partyDirectorySearch.trim().lowercase()
        val searched = if (search.isNotEmpty()) {
            roleFiltered.filter { it.name.lowercase().contains(search) }
        } else {
            roleFiltered
        }

        // Sort
        when (state.sortOption) {
            PartySortOption.DEBIT_DESC -> searched.sortedWith(compareByDescending<PartySummary> { it.debitTotal }.thenByDescending { it.totalCount })
            PartySortOption.CREDIT_DESC -> searched.sortedWith(compareByDescending<PartySummary> { it.creditTotal }.thenByDescending { it.totalCount })
            PartySortOption.COUNT_DESC -> searched.sortedWith(compareByDescending<PartySummary> { it.totalCount }.thenByDescending { it.debitTotal + it.creditTotal })
            PartySortOption.NET_ASC -> searched.sortedBy { it.netFlow }
            PartySortOption.NET_DESC -> searched.sortedByDescending { it.netFlow }
            PartySortOption.NAME_ASC -> searched.sortedBy { it.name }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Time buckets
    val timeBuckets: StateFlow<List<TimeBucket>> = combine(
        scopedTransactions,
        uiState
    ) { txs, state ->
        transactionRepository.getTimeBuckets(state.timeGranularity, txs)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setSubTab(index: Int) {
        uiState.value = uiState.value.copy(activeSubTab = index)
    }

    fun setGranularity(granularity: TimeGranularity) {
        uiState.value = uiState.value.copy(timeGranularity = granularity)
    }

    fun setPartyRoleFilter(role: PartyRole) {
        uiState.value = uiState.value.copy(partyRoleFilter = role)
    }

    fun setSortOption(option: PartySortOption) {
        uiState.value = uiState.value.copy(sortOption = option)
    }

    fun setPartyDirectorySearch(search: String) {
        uiState.value = uiState.value.copy(partyDirectorySearch = search)
    }

    fun resetFilters() {
        uiState.value = AnalyticsUiState()
    }

    companion object {
        fun provideFactory(repository: TransactionRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return AnalyticsViewModel(repository) as T
                }
            }
    }
}
