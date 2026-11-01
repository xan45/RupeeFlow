package com.rupeeflow.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rupeeflow.app.data.local.entity.ImportedFileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ImportedFileDao {
    @Query("SELECT * FROM imported_files ORDER BY importedDate DESC")
    fun getAllFiles(): Flow<List<ImportedFileEntity>>

    @Query("SELECT * FROM imported_files WHERE id = :id LIMIT 1")
    suspend fun getFileById(id: Long): ImportedFileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(file: ImportedFileEntity): Long

    @Delete
    suspend fun delete(file: ImportedFileEntity)

    @Query("DELETE FROM imported_files WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM imported_files")
    suspend fun deleteAll()
}
