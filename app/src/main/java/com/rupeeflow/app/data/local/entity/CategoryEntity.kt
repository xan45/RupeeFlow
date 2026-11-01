package com.rupeeflow.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.rupeeflow.app.domain.model.Category

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val iconName: String = "category",
    val colorHex: String = "#6366F1",
    val isDefault: Boolean = true
) {
    fun toDomain(): Category {
        return Category(
            id = id,
            name = name,
            iconName = iconName,
            colorHex = colorHex,
            isDefault = isDefault
        )
    }

    companion object {
        fun fromDomain(domain: Category): CategoryEntity {
            return CategoryEntity(
                id = domain.id,
                name = domain.name,
                iconName = domain.iconName,
                colorHex = domain.colorHex,
                isDefault = domain.isDefault
            )
        }
    }
}
