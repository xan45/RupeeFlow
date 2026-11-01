package com.rupeeflow.app.domain.repository

import com.rupeeflow.app.domain.model.FinancialHealthSummary
import com.rupeeflow.app.domain.model.PartySummary
import com.rupeeflow.app.domain.model.TimeBucket
import com.rupeeflow.app.domain.model.TimeGranularity
import com.rupeeflow.app.domain.model.Transaction
import com.rupeeflow.app.domain.model.TransactionSource
import com.rupeeflow.app.domain.model.TransactionType
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {
    fun getAllTransactions(): Flow<List<Transaction>>
    fun getFilteredTransactions(
        search: String? = null,
        type: TransactionType? = null,
        source: TransactionSource? = null,
        category: String? = null,
        fromDate: Long? = null,
        toDate: Long? = null
    ): Flow<List<Transaction>>
    
    suspend fun addTransaction(transaction: Transaction): Long
    suspend fun addTransactions(transactions: List<Transaction>): List<Long>
    suspend fun deleteTransaction(id: Long)
    suspend fun deleteAllTransactions()
    
    fun getFinancialHealthSummary(latestClosingBalance: String? = null): Flow<FinancialHealthSummary>
    fun getTimeBuckets(granularity: TimeGranularity, txs: List<Transaction>): List<TimeBucket>
    fun getCollatedParties(transactions: List<Transaction>): List<PartySummary>
}
