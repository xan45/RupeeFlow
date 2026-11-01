package com.rupeeflow.app.data.repository

import com.rupeeflow.app.data.local.dao.TransactionDao
import com.rupeeflow.app.data.local.entity.TransactionEntity
import com.rupeeflow.app.data.parser.CsvHelper
import com.rupeeflow.app.domain.model.FinancialHealthSummary
import com.rupeeflow.app.domain.model.PartySummary
import com.rupeeflow.app.domain.model.TimeBucket
import com.rupeeflow.app.domain.model.TimeGranularity
import com.rupeeflow.app.domain.model.Transaction
import com.rupeeflow.app.domain.model.TransactionSource
import com.rupeeflow.app.domain.model.TransactionType
import com.rupeeflow.app.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class TransactionRepositoryImpl(
    private val transactionDao: TransactionDao
) : TransactionRepository {

    override fun getAllTransactions(): Flow<List<Transaction>> {
        return transactionDao.getAllTransactions().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getFilteredTransactions(
        search: String?,
        type: TransactionType?,
        source: TransactionSource?,
        category: String?,
        fromDate: Long?,
        toDate: Long?
    ): Flow<List<Transaction>> {
        return transactionDao.getFilteredTransactions(
            search = search?.ifBlank { null },
            type = type?.rawValue,
            source = source?.code,
            category = category?.ifBlank { null },
            fromDate = fromDate,
            toDate = toDate
        ).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun addTransaction(transaction: Transaction): Long {
        return transactionDao.insert(TransactionEntity.fromDomain(transaction))
    }

    override suspend fun addTransactions(transactions: List<Transaction>): List<Long> {
        return transactionDao.insertAll(transactions.map { TransactionEntity.fromDomain(it) })
    }

    override suspend fun deleteTransaction(id: Long) {
        transactionDao.deleteById(id)
    }

    override suspend fun deleteAllTransactions() {
        transactionDao.deleteAll()
    }

    override fun getFinancialHealthSummary(latestClosingBalance: String?): Flow<FinancialHealthSummary> {
        return getAllTransactions().map { txs ->
            var earnings = 0.0
            var expenses = 0.0
            var earnCount = 0
            var expCount = 0

            for (tx in txs) {
                if (tx.type == TransactionType.EARNING) {
                    earnings += tx.amount
                    earnCount++
                } else {
                    expenses += tx.amount
                    expCount++
                }
            }

            val netCashFlow = earnings - expenses
            val savingsRate = if (earnings > 0) ((earnings - expenses) / earnings) * 100.0 else 0.0
            val expenseToIncomeRatio = if (earnings > 0) (expenses / earnings) else 1.0

            // Financial health score calculation (0 - 100)
            val healthScore = when {
                earnings == 0.0 && expenses == 0.0 -> 50
                earnings == 0.0 -> 10
                savingsRate >= 50.0 -> 95
                savingsRate >= 30.0 -> 85
                savingsRate >= 15.0 -> 70
                savingsRate >= 0.0 -> 55
                savingsRate >= -20.0 -> 35
                else -> 15
            }

            FinancialHealthSummary(
                netCashFlow = netCashFlow,
                totalEarnings = earnings,
                totalExpenses = expenses,
                earningCount = earnCount,
                expenseCount = expCount,
                transactionCount = txs.size,
                savingsRate = savingsRate,
                expenseToIncomeRatio = expenseToIncomeRatio,
                latestClosingBalance = latestClosingBalance,
                healthScore = healthScore
            )
        }
    }

    override fun getCollatedParties(transactions: List<Transaction>): List<PartySummary> {
        val map = mutableMapOf<String, MutablePartyStats>()

        for (tx in transactions) {
            val key = CsvHelper.canonicalPartyName(tx.title)
            val stats = map.getOrPut(key) {
                MutablePartyStats(
                    name = key,
                    firstDate = tx.date,
                    lastDate = tx.date,
                    category = tx.category
                )
            }

            if (tx.type == TransactionType.EARNING) {
                stats.creditTotal += tx.amount
                stats.creditCount++
            } else {
                stats.debitTotal += tx.amount
                stats.debitCount++
            }

            stats.totalCount++
            if (tx.date < stats.firstDate) stats.firstDate = tx.date
            if (tx.date > stats.lastDate) stats.lastDate = tx.date
        }

        return map.values.map { it.toDomain() }
            .sortedWith(compareByDescending<PartySummary> { it.debitTotal }.thenByDescending { it.totalCount })
    }

    override fun getTimeBuckets(
        granularity: TimeGranularity,
        txs: List<Transaction>
    ): List<TimeBucket> {
        val calendar = Calendar.getInstance()
        val bucketsMap = mutableMapOf<String, MutableTimeBucketStats>()

        for (tx in txs) {
            calendar.timeInMillis = tx.date
            val (key, label, subLabel) = getPeriodMeta(calendar, granularity)

            val bucket = bucketsMap.getOrPut(key) {
                MutableTimeBucketStats(key = key, label = label, subLabel = subLabel)
            }

            if (tx.type == TransactionType.EARNING) {
                bucket.creditTotal += tx.amount
                bucket.creditCount++
            } else {
                bucket.debitTotal += tx.amount
                bucket.debitCount++
            }
            bucket.transactions.add(tx)
        }

        return bucketsMap.values
            .sortedByDescending { it.key }
            .map { b ->
                TimeBucket(
                    key = b.key,
                    label = b.label,
                    subLabel = b.subLabel,
                    creditTotal = b.creditTotal,
                    debitTotal = b.debitTotal,
                    creditCount = b.creditCount,
                    debitCount = b.debitCount,
                    transactions = b.transactions,
                    collatedParties = getCollatedParties(b.transactions)
                )
            }
    }

    private fun getPeriodMeta(calendar: Calendar, granularity: TimeGranularity): Triple<String, String, String> {
        val yyyy = calendar.get(Calendar.YEAR)
        val mm = calendar.get(Calendar.MONTH) // 0-based
        val dd = calendar.get(Calendar.DAY_OF_MONTH)

        return when (granularity) {
            TimeGranularity.DAY -> {
                val dayKey = String.format(Locale.ENGLISH, "%04d-%02d-%02d", yyyy, mm + 1, dd)
                val sdf = SimpleDateFormat("dd MMM yyyy (EEE)", Locale.ENGLISH)
                Triple(dayKey, sdf.format(Date(calendar.timeInMillis)), "Daily Summary")
            }
            TimeGranularity.WEEK -> {
                val weekIndex = Math.min(5, ((dd - 1) / 7) + 1)
                val weekKey = String.format(Locale.ENGLISH, "%04d-%02d-W%d", yyyy, mm + 1, weekIndex)
                val monthName = SimpleDateFormat("MMM yyyy", Locale.ENGLISH).format(Date(calendar.timeInMillis))
                val startDay = String.format(Locale.ENGLISH, "%02d", (weekIndex - 1) * 7 + 1)
                val endDay = if (weekIndex == 5) "31" else String.format(Locale.ENGLISH, "%02d", weekIndex * 7)
                Triple(
                    weekKey,
                    "Week $weekIndex • $monthName",
                    "$startDay $monthName – $endDay $monthName"
                )
            }
            TimeGranularity.MONTH -> {
                val monthKey = String.format(Locale.ENGLISH, "%04d-%02d", yyyy, mm + 1)
                val monthLabel = SimpleDateFormat("MMMM yyyy", Locale.ENGLISH).format(Date(calendar.timeInMillis))
                Triple(monthKey, monthLabel, "Monthly Statement Summary")
            }
            TimeGranularity.YEAR -> {
                val yearKey = yyyy.toString()
                Triple(yearKey, "Year $yyyy", "Annual Summary")
            }
        }
    }

    private class MutablePartyStats(
        val name: String,
        var creditTotal: Double = 0.0,
        var debitTotal: Double = 0.0,
        var creditCount: Int = 0,
        var debitCount: Int = 0,
        var totalCount: Int = 0,
        var firstDate: Long,
        var lastDate: Long,
        val category: String
    ) {
        fun toDomain(): PartySummary {
            return PartySummary(
                name = name,
                creditTotal = creditTotal,
                debitTotal = debitTotal,
                creditCount = creditCount,
                debitCount = debitCount,
                totalCount = totalCount,
                firstDate = firstDate,
                lastDate = lastDate,
                category = category
            )
        }
    }

    private class MutableTimeBucketStats(
        val key: String,
        val label: String,
        val subLabel: String,
        var creditTotal: Double = 0.0,
        var debitTotal: Double = 0.0,
        var creditCount: Int = 0,
        var debitCount: Int = 0,
        val transactions: MutableList<Transaction> = mutableListOf()
    )
}
