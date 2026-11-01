package com.rupeeflow.app.domain.repository

import com.rupeeflow.app.domain.model.Category
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    fun getAllCategories(): Flow<List<Category>>
    suspend fun addCategory(category: Category): Long
    suspend fun deleteCategory(id: Long)
}
