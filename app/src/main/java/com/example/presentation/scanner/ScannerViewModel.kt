package com.example.presentation.scanner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.FinAiApplication
import com.example.core.util.MoneyParser
import com.example.data.payment.BankAppLauncher
import com.example.data.qr.VietQrParser
import com.example.domain.model.BankApp
import com.example.domain.model.Expense
import com.example.domain.model.ExpenseCategory
import com.example.domain.model.ExpenseStatus
import com.example.domain.model.PaymentInfo
import com.example.domain.usecase.AddExpenseUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ScannerUiState(
    val isFlashOn: Boolean = false,
    val paymentInfo: PaymentInfo = PaymentInfo(
        bankCode = "MB",
        bankName = "MB Bank",
        accountNumber = "0123456789",
        receiverName = "NGUYEN VAN A",
        amount = 150_000L,
        content = "AN TOI FINAI"
    ),
    val installedBanks: List<BankApp> = emptyList(),
    val selectedBank: BankApp? = null,
    val showConfirmSheet: Boolean = false,
    val showEditAmountDialog: Boolean = false,
    val toastMessage: String? = null
)

class ScannerViewModel(
    private val bankAppLauncher: BankAppLauncher,
    private val addExpenseUseCase: AddExpenseUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ScannerUiState())
    val uiState: StateFlow<ScannerUiState> = _uiState.asStateFlow()

    init {
        loadBanks()
    }

    private fun loadBanks() {
        val banks = bankAppLauncher.getInstalledBanks()
        val defaultMb = banks.find { it.id == "MB" } ?: banks.firstOrNull()
        _uiState.update {
            it.copy(
                installedBanks = banks,
                selectedBank = defaultMb
            )
        }
    }

    fun toggleFlash() {
        _uiState.update {
            val newState = !it.isFlashOn
            it.copy(
                isFlashOn = newState,
                toastMessage = if (newState) "Đã bật đèn pin trợ sáng" else "Đã tắt đèn pin"
            )
        }
    }

    fun onScanQrCode(qrData: String) {
        val parsed = VietQrParser.parse(qrData)
        if (parsed != null) {
            _uiState.update {
                it.copy(
                    paymentInfo = parsed,
                    toastMessage = "Đã nhận diện mã VietQR Napas247 hợp lệ!"
                )
            }
        } else {
            _uiState.update { it.copy(toastMessage = "Mã QR không đúng định dạng VietQR") }
        }
    }

    fun selectBank(bank: BankApp) {
        _uiState.update {
            it.copy(
                selectedBank = bank,
                toastMessage = "Đã chọn mở: ${bank.displayName}"
            )
        }
    }

    fun updateAmount(newAmount: Long) {
        _uiState.update { current ->
            current.copy(
                paymentInfo = current.paymentInfo.copy(amount = newAmount),
                showEditAmountDialog = false,
                toastMessage = "Đã cập nhật số tiền: ${MoneyParser.formatCurrency(newAmount)}"
            )
        }
    }

    fun showEditAmount(show: Boolean) {
        _uiState.update { it.copy(showEditAmountDialog = show) }
    }

    fun copyToClipboard(content: String, label: String) {
        _uiState.update { it.copy(toastMessage = label) }
    }

    fun launchBankingApp() {
        val currentBank = _uiState.value.selectedBank
        val payment = _uiState.value.paymentInfo

        if (currentBank != null) {
            bankAppLauncher.openBank(currentBank, payment)
            _uiState.update {
                it.copy(
                    toastMessage = "Đã sao chép STK & nội dung! Đang kết nối app ${currentBank.displayName}...",
                    showConfirmSheet = true
                )
            }
        }
    }

    fun resolvePayment(status: ExpenseStatus) {
        val payment = _uiState.value.paymentInfo
        val bank = _uiState.value.selectedBank
        viewModelScope.launch {
            val expense = Expense(
                amount = payment.amount ?: 150_000L,
                title = payment.content ?: "Chuyển tiền ${payment.receiverName ?: ""}",
                category = ExpenseCategory.FOOD,
                paymentMethod = "Chuyển khoản",
                bankCode = payment.bankCode ?: bank?.shortName ?: "MB",
                accountNumber = payment.accountNumber,
                receiverName = payment.receiverName,
                paymentContent = payment.content,
                status = status,
                transactionDate = System.currentTimeMillis()
            )
            addExpenseUseCase(expense)
            _uiState.update {
                it.copy(
                    showConfirmSheet = false,
                    toastMessage = if (status == ExpenseStatus.PAID) {
                        "Giao dịch đã được lưu: Đã thanh toán (PAID)!"
                    } else {
                        "Giao dịch giữ trạng thái chờ (PENDING)"
                    }
                )
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
                return ScannerViewModel(
                    bankAppLauncher = app.bankAppLauncher,
                    addExpenseUseCase = app.addExpenseUseCase
                ) as T
            }
        }
    }
}
