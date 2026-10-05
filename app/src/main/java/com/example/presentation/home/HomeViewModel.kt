package com.example.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.FinAiApplication
import com.example.domain.model.Expense
import com.example.domain.model.ExpenseCategory
import com.example.domain.model.ExpenseIntent
import com.example.domain.model.ExpenseStatus
import com.example.domain.model.Statistics
import com.example.domain.usecase.AddExpenseUseCase
import com.example.domain.usecase.GetExpensesUseCase
import com.example.domain.usecase.GetStatisticsUseCase
import com.example.domain.usecase.ParseNaturalExpenseUseCase
import com.example.domain.usecase.UpdateExpenseStatusUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeUiState(
    val statistics: Statistics = Statistics(),
    val recentExpenses: List<Expense> = emptyList(),
    val quickInputText: String = "",
    val detectedIntent: ExpenseIntent? = null,
    val isLoading: Boolean = false,
    val userMessage: String? = null
)

class HomeViewModel(
    private val getExpensesUseCase: GetExpensesUseCase,
    private val getStatisticsUseCase: GetStatisticsUseCase,
    private val addExpenseUseCase: AddExpenseUseCase,
    private val updateExpenseStatusUseCase: UpdateExpenseStatusUseCase,
    private val parseNaturalExpenseUseCase: ParseNaturalExpenseUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            getStatisticsUseCase().catch { e ->
                _uiState.update { it.copy(userMessage = "Lỗi tải thống kê: ${e.message}") }
            }.collect { stats ->
                _uiState.update { it.copy(statistics = stats) }
            }
        }

        viewModelScope.launch {
            getExpensesUseCase().catch { e ->
                _uiState.update { it.copy(userMessage = "Lỗi tải giao dịch: ${e.message}") }
            }.collect { list ->
                _uiState.update { it.copy(recentExpenses = list) }
            }
        }
    }

    fun onQuickInputChange(text: String) {
        _uiState.update { it.copy(quickInputText = text) }
        viewModelScope.launch {
            if (text.trim().length >= 2) {
                val intent = parseNaturalExpenseUseCase(text)
                _uiState.update { it.copy(detectedIntent = intent) }
            } else {
                _uiState.update { it.copy(detectedIntent = null) }
            }
        }
    }

    fun submitQuickInput() {
        val currentText = _uiState.value.quickInputText.trim()
        val intent = _uiState.value.detectedIntent
        if (intent != null && intent.amount != null && intent.amount > 0) {
            viewModelScope.launch {
                val expense = Expense(
                    amount = intent.amount,
                    title = intent.title ?: currentText,
                    category = intent.category ?: ExpenseCategory.OTHER,
                    status = ExpenseStatus.PAID,
                    paymentMethod = "Tiền mặt",
                    transactionDate = System.currentTimeMillis()
                )
                addExpenseUseCase(expense)
                _uiState.update {
                    it.copy(
                        quickInputText = "",
                        detectedIntent = null,
                        userMessage = "Đã lưu khoản chi: ${intent.title} (-${intent.amount} ₫)"
                    )
                }
            }
        } else if (currentText.isNotEmpty()) {
            _uiState.update { it.copy(userMessage = "Vui lòng nhập số tiền hợp lệ (ví dụ: ăn phở 50k)") }
        }
    }

    fun addManualExpense(expense: Expense) {
        viewModelScope.launch {
            try {
                addExpenseUseCase(expense)
                _uiState.update { it.copy(userMessage = "Đã thêm giao dịch: ${expense.title}") }
            } catch (e: Exception) {
                _uiState.update { it.copy(userMessage = "Lỗi: ${e.message}") }
            }
        }
    }

    fun toggleExpenseStatus(expense: Expense) {
        viewModelScope.launch {
            val newStatus = if (expense.status == ExpenseStatus.PAID) ExpenseStatus.PENDING else ExpenseStatus.PAID
            updateExpenseStatusUseCase(expense.id, newStatus)
            _uiState.update { it.copy(userMessage = "Đã chuyển trạng thái sang: ${newStatus.displayName}") }
        }
    }

    fun clearUserMessage() {
        _uiState.update { it.copy(userMessage = null) }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val app = FinAiApplication.instance
                return HomeViewModel(
                    getExpensesUseCase = app.getExpensesUseCase,
                    getStatisticsUseCase = app.getStatisticsUseCase,
                    addExpenseUseCase = app.addExpenseUseCase,
                    updateExpenseStatusUseCase = app.updateExpenseStatusUseCase,
                    parseNaturalExpenseUseCase = app.parseNaturalExpenseUseCase
                ) as T
            }
        }
    }
}
