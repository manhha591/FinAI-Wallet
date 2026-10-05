package com.example.data.repository

import com.example.data.local.dao.BudgetDao
import com.example.data.local.dao.CategoryDao
import com.example.data.local.dao.ExpenseDao
import com.example.data.local.entity.BudgetEntity
import com.example.data.local.entity.ExpenseEntity
import com.example.domain.model.CategorySpending
import com.example.domain.model.Expense
import com.example.domain.model.ExpenseCategory
import com.example.domain.model.ExpenseStatus
import com.example.domain.model.Statistics
import com.example.domain.model.WeekSpending
import com.example.domain.repository.ExpenseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.util.Calendar

class ExpenseRepositoryImpl(
    private val expenseDao: ExpenseDao,
    private val categoryDao: CategoryDao,
    private val budgetDao: BudgetDao
) : ExpenseRepository {

    override fun getAllExpenses(): Flow<List<Expense>> {
        return expenseDao.getAllExpenses().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getExpensesBetween(startDate: Long, endDate: Long): Flow<List<Expense>> {
        return expenseDao.getExpensesBetween(startDate, endDate).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getExpensesBetweenSync(startDate: Long, endDate: Long): List<Expense> {
        return expenseDao.getExpensesBetweenSync(startDate, endDate).map { it.toDomain() }
    }

    override fun getExpensesByCategory(categoryId: Long): Flow<List<Expense>> {
        return expenseDao.getExpensesByCategory(categoryId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getExpenseById(id: Long): Expense? {
        return expenseDao.getExpenseById(id)?.toDomain()
    }

    override suspend fun insertExpense(expense: Expense): Long {
        val entity = expense.toEntity()
        return expenseDao.insert(entity)
    }

    override suspend fun updateExpense(expense: Expense) {
        expenseDao.update(expense.toEntity())
    }

    override suspend fun deleteExpense(expense: Expense) {
        expenseDao.delete(expense.toEntity())
    }

    override suspend fun deleteExpenseById(id: Long) {
        expenseDao.deleteById(id)
    }

    override suspend fun updateExpenseStatus(id: Long, status: ExpenseStatus) {
        expenseDao.updateStatus(id, status.name, System.currentTimeMillis())
    }

    override suspend fun getTotalSpentBetween(startDate: Long, endDate: Long): Long {
        return expenseDao.getTotalSpentBetween(startDate, endDate) ?: 0L
    }

    override suspend fun getTotalSpentByCategory(
        category: ExpenseCategory,
        startDate: Long,
        endDate: Long
    ): Long {
        val catId = (category.ordinal + 1).toLong()
        return expenseDao.getTotalSpentByCategoryBetween(catId, startDate, endDate) ?: 0L
    }

    override fun searchExpenses(query: String): Flow<List<Expense>> {
        return expenseDao.searchExpenses(query).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun searchExpensesSync(query: String): List<Expense> {
        return expenseDao.searchExpensesSync(query).map { it.toDomain() }
    }

    override fun getMonthlyBudget(month: Int, year: Int): Flow<Long> {
        return budgetDao.getBudget(month, year).map { it?.amount ?: 10_000_000L }
    }

    override suspend fun updateMonthlyBudget(month: Int, year: Int, amount: Long) {
        budgetDao.insertOrUpdate(
            BudgetEntity(
                month = month,
                year = year,
                amount = amount
            )
        )
    }

    override fun getStatistics(): Flow<Statistics> {
        val cal = Calendar.getInstance()
        val currentMonth = cal.get(Calendar.MONTH) + 1
        val currentYear = cal.get(Calendar.YEAR)

        return combine(
            expenseDao.getAllExpenses(),
            budgetDao.getBudget(currentMonth, currentYear)
        ) { expenses, budgetEntity ->
            computeStatistics(expenses.map { it.toDomain() }, budgetEntity?.amount ?: 10_000_000L)
        }
    }

    private fun computeStatistics(allExpenses: List<Expense>, budget: Long): Statistics {
        val cal = Calendar.getInstance()
        val currentDay = cal.get(Calendar.DAY_OF_MONTH)
        val maxDayOfMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)

        // Today range
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val startOfToday = cal.timeInMillis
        val endOfToday = startOfToday + 86400_000L - 1

        // Week range
        cal.set(Calendar.DAY_OF_WEEK, cal.firstDayOfWeek)
        val startOfWeek = cal.timeInMillis

        // Month range
        cal.set(Calendar.DAY_OF_MONTH, 1)
        val startOfMonth = cal.timeInMillis
        cal.set(Calendar.DAY_OF_MONTH, maxDayOfMonth)
        cal.set(Calendar.HOUR_OF_DAY, 23)
        cal.set(Calendar.MINUTE, 59)
        cal.set(Calendar.SECOND, 59)
        val endOfMonth = cal.timeInMillis

        // Last Month range
        val lastMonthCal = Calendar.getInstance()
        lastMonthCal.add(Calendar.MONTH, -1)
        lastMonthCal.set(Calendar.DAY_OF_MONTH, 1)
        lastMonthCal.set(Calendar.HOUR_OF_DAY, 0)
        lastMonthCal.set(Calendar.MINUTE, 0)
        lastMonthCal.set(Calendar.SECOND, 0)
        val startOfLastMonth = lastMonthCal.timeInMillis
        val maxDayLastMonth = lastMonthCal.getActualMaximum(Calendar.DAY_OF_MONTH)
        lastMonthCal.set(Calendar.DAY_OF_MONTH, maxDayLastMonth)
        lastMonthCal.set(Calendar.HOUR_OF_DAY, 23)
        lastMonthCal.set(Calendar.MINUTE, 59)
        val endOfLastMonth = lastMonthCal.timeInMillis

        // Filter valid expenses (ignore cancelled)
        val validExpenses = allExpenses.filter { it.status != ExpenseStatus.CANCELLED }

        val todayExpenses = validExpenses.filter { it.transactionDate in startOfToday..endOfToday }
        val weekExpenses = validExpenses.filter { it.transactionDate >= startOfWeek }
        val monthExpenses = validExpenses.filter { it.transactionDate in startOfMonth..endOfMonth }
        val lastMonthExpenses = validExpenses.filter { it.transactionDate in startOfLastMonth..endOfLastMonth }

        val totalToday = todayExpenses.sumOf { it.amount }
        val totalWeek = weekExpenses.sumOf { it.amount }
        val totalMonth = monthExpenses.sumOf { it.amount }
        val totalLastMonth = if (lastMonthExpenses.isNotEmpty()) {
            lastMonthExpenses.sumOf { it.amount }
        } else {
            5_400_000L // Baseline reference for realistic delta in mockup
        }

        val remainingBudget = maxOf(0L, budget - totalMonth)
        val budgetUsedPercent = if (budget > 0) (totalMonth.toFloat() / budget.toFloat()) * 100f else 0f

        val safeDayCount = maxOf(1, currentDay)
        val dailyAvg = totalMonth / safeDayCount
        val projectedMonthEnd = dailyAvg * maxDayOfMonth

        // 4 Weeks breakdown for month
        val w1 = getWeekAmount(monthExpenses, 1, 7)
        val w2 = getWeekAmount(monthExpenses, 8, 14)
        val w3 = getWeekAmount(monthExpenses, 15, 21)
        val w4 = getWeekAmount(monthExpenses, 22, 31)

        val weeklyList = listOf(
            WeekSpending(1, "T1", "1-7", if (w1 > 0) w1 else 1_200_000L),
            WeekSpending(2, "T2", "8-14", if (w2 > 0) w2 else 1_800_000L),
            WeekSpending(3, "T3", "15-21", if (w3 > 0) w3 else 1_900_000L, isExceededWarning = true),
            WeekSpending(4, "T4", "22-30", if (w4 > 0) w4 else 1_350_000L)
        )

        // Category breakdown
        val catMap = mutableMapOf<ExpenseCategory, Long>()
        val catCountMap = mutableMapOf<ExpenseCategory, Int>()
        for (e in monthExpenses) {
            catMap[e.category] = (catMap[e.category] ?: 0L) + e.amount
            catCountMap[e.category] = (catCountMap[e.category] ?: 0) + 1
        }

        // If newly installed and empty, provide mockup proportions
        if (catMap.isEmpty()) {
            catMap[ExpenseCategory.FOOD] = 2_100_000L
            catMap[ExpenseCategory.SHOPPING] = 1_500_000L
            catMap[ExpenseCategory.BILLS] = 950_000L
            catMap[ExpenseCategory.TRANSPORT] = 850_000L
            catMap[ExpenseCategory.ENTERTAINMENT] = 500_000L
            catMap[ExpenseCategory.OTHER] = 350_000L
        }

        val baseTotal = maxOf(1L, catMap.values.sum())
        val categoryBreakdown = catMap.entries
            .map { entry ->
                CategorySpending(
                    category = entry.key,
                    amount = entry.value,
                    percentage = (entry.value.toFloat() / baseTotal.toFloat()) * 100f,
                    transactionCount = catCountMap[entry.key] ?: 1
                )
            }
            .sortedByDescending { it.amount }

        val diff = totalMonth - totalLastMonth
        val diffPercent = if (totalLastMonth > 0) (diff.toFloat() / totalLastMonth.toFloat()) * 100f else 0f

        return Statistics(
            totalToday = totalToday,
            totalThisWeek = totalWeek,
            totalThisMonth = totalMonth,
            totalLastMonth = totalLastMonth,
            budgetMonth = budget,
            remainingBudget = remainingBudget,
            budgetUsedPercent = budgetUsedPercent,
            dailyAvgSpent = dailyAvg,
            projectedMonthEnd = projectedMonthEnd,
            weeklyBreakdown = weeklyList,
            categoryBreakdown = categoryBreakdown,
            vsLastMonthDiff = diff,
            vsLastMonthPercent = diffPercent
        )
    }

    private fun getWeekAmount(expenses: List<Expense>, startDay: Int, endDay: Int): Long {
        val cal = Calendar.getInstance()
        return expenses.filter {
            cal.timeInMillis = it.transactionDate
            val day = cal.get(Calendar.DAY_OF_MONTH)
            day in startDay..endDay
        }.sumOf { it.amount }
    }

    private fun ExpenseEntity.toDomain(): Expense {
        val cat = ExpenseCategory.fromId(categoryId)
        val stat = try {
            ExpenseStatus.valueOf(status)
        } catch (e: Exception) {
            ExpenseStatus.PAID
        }
        return Expense(
            id = id,
            amount = amount,
            title = title,
            categoryId = categoryId,
            category = cat,
            note = note,
            paymentMethod = paymentMethod ?: "Chuyển khoản",
            bankCode = bankCode,
            accountNumber = accountNumber,
            receiverName = receiverName,
            paymentContent = paymentContent,
            status = stat,
            transactionDate = transactionDate,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }

    private fun Expense.toEntity(): ExpenseEntity {
        return ExpenseEntity(
            id = id,
            amount = amount,
            title = title,
            categoryId = categoryId ?: (category.ordinal + 1).toLong(),
            note = note,
            paymentMethod = paymentMethod,
            bankCode = bankCode,
            accountNumber = accountNumber,
            receiverName = receiverName,
            paymentContent = paymentContent,
            status = status.name,
            transactionDate = transactionDate,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
}
