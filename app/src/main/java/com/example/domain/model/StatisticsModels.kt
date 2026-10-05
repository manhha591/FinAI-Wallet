package com.example.domain.model

data class WeekSpending(
    val weekNumber: Int,
    val label: String,
    val dateRange: String,
    val amount: Long,
    val isExceededWarning: Boolean = false
)

data class CategorySpending(
    val category: ExpenseCategory,
    val amount: Long,
    val percentage: Float,
    val transactionCount: Int
)

data class Statistics(
    val totalToday: Long = 0L,
    val totalThisWeek: Long = 0L,
    val totalThisMonth: Long = 0L,
    val totalLastMonth: Long = 0L,
    val budgetMonth: Long = 10_000_000L,
    val remainingBudget: Long = 0L,
    val budgetUsedPercent: Float = 0f,
    val dailyAvgSpent: Long = 0L,
    val projectedMonthEnd: Long = 0L,
    val weeklyBreakdown: List<WeekSpending> = emptyList(),
    val categoryBreakdown: List<CategorySpending> = emptyList(),
    val vsLastMonthDiff: Long = 0L,
    val vsLastMonthPercent: Float = 0f
)
