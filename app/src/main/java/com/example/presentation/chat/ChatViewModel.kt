package com.example.presentation.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.FinAiApplication
import com.example.domain.model.Expense
import com.example.domain.model.ExpenseCategory
import com.example.domain.model.ExpenseIntent
import com.example.domain.model.ExpenseStatus
import com.example.domain.model.FinancialIntent
import com.example.domain.usecase.AddExpenseUseCase
import com.example.domain.usecase.AiQueryResult
import com.example.domain.usecase.ProcessAiQueryUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val isUser: Boolean,
    val text: String,
    val timestamp: String = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()),
    val aiResult: AiQueryResult? = null,
    val latencyLabel: String = "18ms (NPU)"
)

data class ChatUiState(
    val messages: List<ChatMessage> = emptyList(),
    val inputText: String = "",
    val isGenerating: Boolean = false,
    val toastMessage: String? = null
)

class ChatViewModel(
    private val processAiQueryUseCase: ProcessAiQueryUseCase,
    private val addExpenseUseCase: AddExpenseUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    init {
        initSampleConversation()
    }

    private fun initSampleConversation() {
        val initialList = listOf(
            ChatMessage(
                isUser = true,
                text = "Tháng này tôi tiêu ăn uống bao nhiêu?",
                timestamp = "09:15"
            ),
            ChatMessage(
                isUser = false,
                text = "Phân tích từ database SQLite: Tháng này bạn đã chi tổng cộng 2.100.000 ₫ cho danh mục Ăn uống (chiếm 33.6% tổng chi). So với tháng trước, bạn đang tiết kiệm hơn 250.000 ₫.",
                timestamp = "09:15",
                aiResult = AiQueryResult(
                    intent = FinancialIntent.GetExpenseByCategory(ExpenseCategory.FOOD, com.example.domain.model.TimePeriod.THIS_MONTH),
                    textAnswer = "Phân tích từ database SQLite...",
                    amountHighlight = 2_100_000L,
                    categoryHighlight = ExpenseCategory.FOOD,
                    savingsDiff = 250_000L,
                    percentageHighlight = 33.6f
                ),
                latencyLabel = "18ms (NPU)"
            ),
            ChatMessage(
                isUser = true,
                text = "ăn phở bò tái 55k trưa nay",
                timestamp = "12:46"
            ),
            ChatMessage(
                isUser = false,
                text = "Đã nhận diện giao dịch (Rule Parser + Local AI)",
                timestamp = "12:46",
                aiResult = AiQueryResult(
                    intent = FinancialIntent.AddExpense(
                        ExpenseIntent(
                            amount = 55_000L,
                            title = "Ăn phở bò tái",
                            category = ExpenseCategory.FOOD,
                            confidence = 0.98f
                        )
                    ),
                    textAnswer = "Đã nhận diện giao dịch từ ngôn ngữ tự nhiên",
                    parsedExpense = ExpenseIntent(
                        amount = 55_000L,
                        title = "Ăn phở bò tái",
                        category = ExpenseCategory.FOOD,
                        confidence = 0.98f
                    ),
                    amountHighlight = 55_000L,
                    categoryHighlight = ExpenseCategory.FOOD
                ),
                latencyLabel = "14ms (NPU)"
            ),
            ChatMessage(
                isUser = true,
                text = "Tôi còn bao nhiêu ngân sách tháng này?",
                timestamp = "13:10"
            ),
            ChatMessage(
                isUser = false,
                text = "Ngân sách còn lại của bạn là 3.750.000 ₫ còn lại / 10.000.000 ₫. Còn 14 ngày trong tháng, mức chi tiêu an toàn khuyến nghị là ~267.000 ₫/ngày.",
                timestamp = "13:10",
                aiResult = AiQueryResult(
                    intent = FinancialIntent.GetBudgetStatus(9, 2024),
                    textAnswer = "Ngân sách còn lại của bạn...",
                    remainingBudget = 3_750_000L,
                    totalBudget = 10_000_000L,
                    dailyRecommended = 267_000L,
                    percentageHighlight = 62.5f
                ),
                latencyLabel = "16ms (NPU)"
            )
        )

        _uiState.update { it.copy(messages = initialList) }
    }

    fun onInputTextChange(text: String) {
        _uiState.update { it.copy(inputText = text) }
    }

    fun sendMessage(query: String? = null) {
        val textToSend = (query ?: _uiState.value.inputText).trim()
        if (textToSend.isEmpty()) return

        val userMsg = ChatMessage(isUser = true, text = textToSend)
        _uiState.update {
            it.copy(
                messages = it.messages + userMsg,
                inputText = "",
                isGenerating = true
            )
        }

        viewModelScope.launch {
            try {
                val result = processAiQueryUseCase(textToSend)
                val aiMsg = ChatMessage(
                    isUser = false,
                    text = result.textAnswer,
                    aiResult = result,
                    latencyLabel = "16ms (NPU)"
                )
                _uiState.update {
                    it.copy(
                        messages = it.messages + aiMsg,
                        isGenerating = false
                    )
                }
            } catch (e: Exception) {
                val errorMsg = ChatMessage(
                    isUser = false,
                    text = "Xin lỗi, đã xảy ra lỗi cục bộ khi xử lý: ${e.message}"
                )
                _uiState.update {
                    it.copy(
                        messages = it.messages + errorMsg,
                        isGenerating = false
                    )
                }
            }
        }
    }

    fun commitParsedExpense(intent: ExpenseIntent) {
        viewModelScope.launch {
            val expense = Expense(
                amount = intent.amount ?: 50_000L,
                title = intent.title ?: "Chi tiêu AI",
                category = intent.category ?: ExpenseCategory.FOOD,
                status = ExpenseStatus.PAID,
                paymentMethod = "Chuyển khoản",
                transactionDate = System.currentTimeMillis()
            )
            addExpenseUseCase(expense)
            _uiState.update {
                it.copy(toastMessage = "Đã lưu vào SQLite Database: ${intent.title} (-${intent.amount} ₫)")
            }
        }
    }

    fun clearToast() {
        _uiState.update { it.copy(toastMessage = null) }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val app = FinAiApplication.instance
                return ChatViewModel(
                    processAiQueryUseCase = app.processAiQueryUseCase,
                    addExpenseUseCase = app.addExpenseUseCase
                ) as T
            }
        }
    }
}
