package com.wbrawner.budget.common.category

data class Category(
        val budgetId: Long,
        val id: Long? = null,
        val title: String,
        val description: String? = null,
        val amount: Long,
        val expense: Boolean = true,
        val archived: Boolean = false
) {
    override fun toString(): String {
        return title
    }
}
