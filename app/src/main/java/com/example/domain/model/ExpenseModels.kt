package com.example.domain.model

enum class ExpenseCategory(val displayName: String, val iconName: String, val colorHex: Long) {
    FOOD("Ăn uống", "restaurant", 0xFFF59E0B),
    TRANSPORT("Di chuyển", "directions_car", 0xFF2563EB),
    SHOPPING("Mua sắm", "shopping_bag", 0xFF6366F1),
    ENTERTAINMENT("Giải trí", "sports_esports", 0xFF8B5CF6),
    BILLS("Hóa đơn", "receipt_long", 0xFF0D9488),
    HEALTH("Sức khỏe", "medical_services", 0xFFEF4444),
    EDUCATION("Học tập", "menu_book", 0xFF059669),
    TRAVEL("Du lịch", "flight", 0xFF0284C7),
    OTHER("Khác", "more_horiz", 0xFF64748B);

    companion object {
        fun fromId(id: Long?): ExpenseCategory {
            if (id == null) return OTHER
            val index = (id - 1).toInt()
            return entries.getOrNull(index) ?: OTHER
        }

        fun fromName(name: String?): ExpenseCategory {
            if (name == null) return OTHER
            val normalized = name.trim().lowercase()
            return entries.find {
                it.displayName.lowercase() == normalized ||
                        it.name.lowercase() == normalized
            } ?: OTHER
        }
    }
}

enum class ExpenseStatus(val displayName: String, val colorHex: Long) {
    DRAFT("Nháp", 0xFF64748B),
    PENDING("Chờ đối soát", 0xFFD97706),
    PAID("Đã thanh toán", 0xFF059669),
    CANCELLED("Đã hủy", 0xFFDC2626)
}

enum class PaymentMethod(val displayName: String) {
    BANK_TRANSFER("Chuyển khoản"),
    CASH("Tiền mặt"),
    E_WALLET("Ví điện tử"),
    CREDIT_CARD("Thẻ tín dụng")
}

data class Expense(
    val id: Long = 0,
    val amount: Long,
    val title: String,
    val categoryId: Long? = 1,
    val category: ExpenseCategory = ExpenseCategory.FOOD,
    val note: String? = null,
    val paymentMethod: String? = PaymentMethod.BANK_TRANSFER.displayName,
    val bankCode: String? = "MB",
    val accountNumber: String? = null,
    val receiverName: String? = null,
    val paymentContent: String? = null,
    val status: ExpenseStatus = ExpenseStatus.PAID,
    val transactionDate: Long = System.currentTimeMillis(),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
