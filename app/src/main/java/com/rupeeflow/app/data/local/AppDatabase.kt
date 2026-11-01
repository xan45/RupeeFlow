package com.rupeeflow.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.rupeeflow.app.data.local.converter.DateConverters
import com.rupeeflow.app.data.local.dao.CategoryDao
import com.rupeeflow.app.data.local.dao.ImportedFileDao
import com.rupeeflow.app.data.local.dao.TransactionDao
import com.rupeeflow.app.data.local.entity.CategoryEntity
import com.rupeeflow.app.data.local.entity.ImportedFileEntity
import com.rupeeflow.app.data.local.entity.TransactionEntity
import com.rupeeflow.app.domain.model.Category
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        TransactionEntity::class,
        ImportedFileEntity::class,
        CategoryEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(DateConverters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun transactionDao(): TransactionDao
    abstract fun importedFileDao(): ImportedFileDao
    abstract fun categoryDao(): CategoryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "rupeeflow_local.db"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(DatabaseCallback(context.applicationContext))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback(
        private val context: Context
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            CoroutineScope(Dispatchers.IO).launch {
                val database = getInstance(context)
                // Seed default categories taxonomy only (Empty transaction database on fresh install)
                database.categoryDao().insertAll(
                    Category.DEFAULT_CATEGORIES.map { CategoryEntity.fromDomain(it) }
                )
            }
        }
    }
}
