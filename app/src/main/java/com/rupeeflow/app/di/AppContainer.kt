package com.rupeeflow.app.di

import android.content.Context
import com.rupeeflow.app.data.local.AppDatabase
import com.rupeeflow.app.data.repository.CategoryRepositoryImpl
import com.rupeeflow.app.data.repository.StatementRepositoryImpl
import com.rupeeflow.app.data.repository.TransactionRepositoryImpl
import com.rupeeflow.app.domain.repository.CategoryRepository
import com.rupeeflow.app.domain.repository.StatementRepository
import com.rupeeflow.app.domain.repository.TransactionRepository

interface AppContainer {
    val transactionRepository: TransactionRepository
    val statementRepository: StatementRepository
    val categoryRepository: CategoryRepository
}

class DefaultAppContainer(private val context: Context) : AppContainer {

    private val database: AppDatabase by lazy {
        AppDatabase.getInstance(context)
    }

    override val transactionRepository: TransactionRepository by lazy {
        TransactionRepositoryImpl(database.transactionDao())
    }

    override val statementRepository: StatementRepository by lazy {
        StatementRepositoryImpl(context, database.importedFileDao(), database.transactionDao())
    }

    override val categoryRepository: CategoryRepository by lazy {
        CategoryRepositoryImpl(database.categoryDao())
    }
}
