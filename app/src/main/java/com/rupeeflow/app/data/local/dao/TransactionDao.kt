package com.rupeeflow.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.rupeeflow.app.data.local.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {

    @Query("SELECT * FROM transactions ORDER BY date DESC, id DESC")
    fun getAllTransactions(): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE id = :id LIMIT 1")
    suspend fun getTransactionById(id: Long): TransactionEntity?

    @Query("""
        SELECT * FROM transactions 
        WHERE (:search IS NULL OR :search = '' OR title LIKE '%' || :search || '%' OR rawDescription LIKE '%' || :search || '%' OR category LIKE '%' || :search || '%')
          AND (:type IS NULL OR type = :type)
          AND (:source IS NULL OR source = :source)
          AND (:category IS NULL OR category = :category)
          AND (:fromDate IS NULL OR date >= :fromDate)
          AND (:toDate IS NULL OR date <= :toDate)
        ORDER BY date DESC, id DESC
    """)
    fun getFilteredTransactions(
        search: String?,
        type: String?,
        source: String?,
        category: String?,
        fromDate: Long?,
        toDate: Long?
    ): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE date BETWEEN :fromDate AND :toDate ORDER BY date DESC")
    fun getTransactionsBetween(fromDate: Long, toDate: Long): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE importedFileId = :fileId ORDER BY date DESC")
    fun getTransactionsByFileId(fileId: Long): Flow<List<TransactionEntity>>

    @Query("SELECT SUM(amount) FROM transactions WHERE type = 'earning'")
    fun getTotalEarnings(): Flow<Double?>

    @Query("SELECT SUM(amount) FROM transactions WHERE type = 'expense'")
    fun getTotalExpenses(): Flow<Double?>

    @Query("SELECT COUNT(*) FROM transactions")
    fun getTransactionCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transaction: TransactionEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(transactions: List<TransactionEntity>): List<Long>

    @Update
    suspend fun update(transaction: TransactionEntity)

    @Delete
    suspend fun delete(transaction: TransactionEntity)

    @Query("DELETE FROM transactions WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM transactions WHERE importedFileId = :fileId")
    suspend fun deleteByFileId(fileId: Long)

    @Query("DELETE FROM transactions")
    suspend fun deleteAll()
}
