package com.example.domain.model

data class ExpenseIntent(
    val amount: Long?,
    val title: String?,
    val category: ExpenseCategory?,
    val dateTimestamp: Long? = System.currentTimeMillis(),
    val confidence: Float = 1.0f,
    val rawText: String = ""
)

enum class TimePeriod(val displayName: String) {
    TODAY("Hôm nay"),
    THIS_WEEK("Tuần này"),
    THIS_MONTH("Tháng này"),
    THIS_YEAR("Năm nay")
}

sealed interface FinancialIntent {
    data class AddExpense(val expenseIntent: ExpenseIntent) : FinancialIntent
    data class GetTotalExpense(val period: TimePeriod) : FinancialIntent
    data class GetExpenseByCategory(val category: ExpenseCategory, val period: TimePeriod) : FinancialIntent
    data class GetExpenseByDate(val dateTimestamp: Long, val label: String) : FinancialIntent
    data class ComparePeriods(val compareDescription: String) : FinancialIntent
    data class GetTopCategory(val period: TimePeriod) : FinancialIntent
    data class GetBudgetStatus(val month: Int, val year: Int) : FinancialIntent
    data class SearchTransaction(val query: String) : FinancialIntent
    data class Unknown(val query: String) : FinancialIntent
}
