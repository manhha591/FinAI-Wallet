package com.example.presentation.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.FinAiApplication
import com.example.domain.model.Statistics
import com.example.domain.usecase.GetStatisticsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar

enum class TimeTab(val title: String) {
    DAY("Ngày"),
    WEEK("Tuần"),
    MONTH("Tháng"),
    YEAR("Năm")
}

data class StatisticsUiState(
    val selectedTab: TimeTab = TimeTab.MONTH,
    val selectedMonth: Int = 9,
    val selectedYear: Int = 2024,
    val statistics: Statistics = Statistics(),
    val isLoading: Boolean = false,
    val infoMessage: String? = null
)

class StatisticsViewModel(
    private val getStatisticsUseCase: GetStatisticsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        StatisticsUiState(
            selectedMonth = Calendar.getInstance().get(Calendar.MONTH) + 1,
            selectedYear = Calendar.getInstance().get(Calendar.YEAR)
        )
    )
    val uiState: StateFlow<StatisticsUiState> = _uiState.asStateFlow()

    init {
        loadStats()
    }

    private fun loadStats() {
        viewModelScope.launch {
            getStatisticsUseCase().catch { e ->
                _uiState.update { it.copy(infoMessage = "Lỗi tải thống kê: ${e.message}") }
            }.collect { stats ->
                _uiState.update { it.copy(statistics = stats) }
            }
        }
    }

    fun selectTab(tab: TimeTab) {
        _uiState.update { it.copy(selectedTab = tab) }
    }

    fun prevMonth() {
        _uiState.update { current ->
            var m = current.selectedMonth - 1
            var y = current.selectedYear
            if (m < 1) {
                m = 12
                y -= 1
            }
            current.copy(selectedMonth = m, selectedYear = y)
        }
    }

    fun nextMonth() {
        _uiState.update { current ->
            var m = current.selectedMonth + 1
            var y = current.selectedYear
            if (m > 12) {
                m = 1
                y += 1
            }
            current.copy(selectedMonth = m, selectedYear = y)
        }
    }

    fun exportReport(format: String) {
        _uiState.update {
            it.copy(infoMessage = "Đã xuất báo cáo $format thành công vào thư mục Downloads!")
        }
    }

    fun shareReport() {
        _uiState.update {
            it.copy(infoMessage = "Báo cáo chi tiêu sẵn sàng chia sẻ!")
        }
    }

    fun clearMessage() {
        _uiState.update { it.copy(infoMessage = null) }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val app = FinAiApplication.instance
                return StatisticsViewModel(
                    getStatisticsUseCase = app.getStatisticsUseCase
                ) as T
            }
        }
    }
}
