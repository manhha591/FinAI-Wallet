package com.example.domain.model

data class PaymentInfo(
    val bankCode: String? = null,
    val bankName: String? = null,
    val accountNumber: String? = null,
    val receiverName: String? = null,
    val amount: Long? = null,
    val content: String? = null,
    val rawQr: String? = null
)

data class BankApp(
    val id: String,
    val displayName: String,
    val shortName: String,
    val packageName: String,
    val accentColorHex: Long,
    val isInstalled: Boolean = false,
    val deepLinkPrefix: String? = null
)
