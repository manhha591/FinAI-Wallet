package com.example.domain.repository

import com.example.domain.model.Expense
import com.example.domain.model.ExpenseCategory
import com.example.domain.model.ExpenseStatus
import com.example.domain.model.Statistics
import kotlinx.coroutines.flow.Flow

interface ExpenseRepository {
    fun getAllExpenses(): Flow<List<Expense>>
    fun getExpensesBetween(startDate: Long, endDate: Long): Flow<List<Expense>>
    suspend fun getExpensesBetweenSync(startDate: Long, endDate: Long): List<Expense>
    fun getExpensesByCategory(categoryId: Long): Flow<List<Expense>>
    suspend fun getExpenseById(id: Long): Expense?
    suspend fun insertExpense(expense: Expense): Long
    suspend fun updateExpense(expense: Expense)
    suspend fun deleteExpense(expense: Expense)
    suspend fun deleteExpenseById(id: Long)
    suspend fun updateExpenseStatus(id: Long, status: ExpenseStatus)
    suspend fun getTotalSpentBetween(startDate: Long, endDate: Long): Long
    suspend fun getTotalSpentByCategory(category: ExpenseCategory, startDate: Long, endDate: Long): Long
    fun searchExpenses(query: String): Flow<List<Expense>>
    suspend fun searchExpensesSync(query: String): List<Expense>
    fun getStatistics(): Flow<Statistics>
    fun getMonthlyBudget(month: Int, year: Int): Flow<Long>
    suspend fun updateMonthlyBudget(month: Int, year: Int, amount: Long)
}
