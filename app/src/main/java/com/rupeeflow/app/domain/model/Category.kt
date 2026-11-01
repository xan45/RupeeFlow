package com.rupeeflow.app.domain.model

data class Category(
    val id: Long = 0,
    val name: String,
    val iconName: String = "category",
    val colorHex: String = "#6366F1",
    val isDefault: Boolean = true
) {
    companion object {
        val DEFAULT_CATEGORIES = listOf(
            Category(1, "Food & Dining", "restaurant", "#F43F5E"),
            Category(2, "Groceries", "shopping_cart", "#10B981"),
            Category(3, "Shopping", "shopping_bag", "#EC4899"),
            Category(4, "Bills & UPI", "receipt", "#3B82F6"),
            Category(5, "Travel", "directions_car", "#F59E0B"),
            Category(6, "Salary / Income", "payments", "#10B981"),
            Category(7, "Investments", "trending_up", "#8B5CF6"),
            Category(8, "Health", "medical_services", "#EF4444"),
            Category(9, "Entertainment", "movie", "#A855F7"),
            Category(10, "Other", "category", "#64748B")
        )
    }
}
