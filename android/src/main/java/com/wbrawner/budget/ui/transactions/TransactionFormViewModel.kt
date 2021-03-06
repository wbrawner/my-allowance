package com.wbrawner.budget.ui.transactions

import androidx.lifecycle.ViewModel
import com.wbrawner.budget.common.budget.BudgetRepository
import com.wbrawner.budget.common.category.CategoryRepository
import com.wbrawner.budget.common.transaction.Transaction
import com.wbrawner.budget.common.transaction.TransactionRepository
import com.wbrawner.budget.common.user.UserRepository
import javax.inject.Inject

class TransactionFormViewModel : ViewModel() {
    @Inject
    lateinit var budgetRepository: BudgetRepository

    @Inject
    lateinit var categoryRepository: CategoryRepository

    @Inject
    lateinit var transactionRepository: TransactionRepository

    @Inject
    lateinit var userRepository: UserRepository

    var currentUserId: String? = null
        private set

    //TODO: Find a better way to handle this
    fun init() {
        userRepository.currentUser.observeForever {
            currentUserId = it?.id
        }
    }

    suspend fun getCategories(budgetId: String, expense: Boolean) = categoryRepository.findAll(arrayOf(budgetId)).filter {
        it.expense == expense
    }

    suspend fun getTransaction(id: String) = transactionRepository.findById(id)

    suspend fun saveTransaction(transaction: Transaction) = if (transaction.id == null)
        transactionRepository.create(transaction)
    else
        transactionRepository.update(transaction)

    suspend fun deleteTransaction(id: String) = transactionRepository.delete(id)

    suspend fun getAccounts() = budgetRepository.findAll()
}
