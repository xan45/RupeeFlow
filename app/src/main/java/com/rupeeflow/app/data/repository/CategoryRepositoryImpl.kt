package com.rupeeflow.app.data.repository

import com.rupeeflow.app.data.local.dao.CategoryDao
import com.rupeeflow.app.data.local.entity.CategoryEntity
import com.rupeeflow.app.domain.model.Category
import com.rupeeflow.app.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CategoryRepositoryImpl(
    private val categoryDao: CategoryDao
) : CategoryRepository {

    override fun getAllCategories(): Flow<List<Category>> {
        return categoryDao.getAllCategories().map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun addCategory(category: Category): Long {
        return categoryDao.insert(CategoryEntity.fromDomain(category))
    }

    override suspend fun deleteCategory(id: Long) {
        categoryDao.deleteById(id)
    }
}
