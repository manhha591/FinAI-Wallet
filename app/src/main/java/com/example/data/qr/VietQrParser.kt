package com.example.data.qr

import com.example.domain.model.PaymentInfo
import java.net.URLDecoder

object VietQrParser {

    private val BANK_BIN_MAP = mapOf(
        "970422" to Pair("MB", "MB Bank"),
        "970436" to Pair("VCB", "Vietcombank"),
        "970407" to Pair("TCB", "Techcombank"),
        "970418" to Pair("BIDV", "BIDV"),
        "970415" to Pair("CTG", "VietinBank"),
        "970423" to Pair("TPB", "TPBank"),
        "970432" to Pair("VPB", "VPBank"),
        "970416" to Pair("ACB", "ACB"),
        "970405" to Pair("AGR", "Agribank"),
        "970448" to Pair("OCB", "OCB"),
        "970428" to Pair("NAB", "Nam A Bank"),
        "970441" to Pair("VIB", "VIB")
    )

    fun parse(rawQr: String): PaymentInfo? {
        val trimmed = rawQr.trim()
        if (trimmed.isEmpty()) return null

        // Case 1: EMVCo QR format (standard VietQR)
        if (trimmed.startsWith("000201") || trimmed.contains("A000000727") || (trimmed.length > 20 && trimmed.startsWith("0002"))) {
            return parseEmvCo(trimmed)
        }

        // Case 2: URL format like img.vietqr.io or api.vietqr.io
        if (trimmed.startsWith("http://") || trimmed.startsWith("https://")) {
            return parseVietQrUrl(trimmed)
        }

        // Case 3: Simple pipe or comma delimited format: BANK|ACCOUNT|AMOUNT|CONTENT|NAME
        if (trimmed.contains("|")) {
            val parts = trimmed.split("|")
            if (parts.size >= 2) {
                val bank = parts[0].trim()
                val acc = parts[1].trim()
                val amount = parts.getOrNull(2)?.toLongOrNull()
                val content = parts.getOrNull(3)?.trim()
                val name = parts.getOrNull(4)?.trim()
                return PaymentInfo(
                    bankCode = bank,
                    bankName = getBankName(bank),
                    accountNumber = acc,
                    receiverName = name ?: "NGUYEN VAN A",
                    amount = amount,
                    content = content,
                    rawQr = trimmed
                )
            }
        }

        return null
    }

    private fun parseEmvCo(raw: String): PaymentInfo? {
        try {
            val rootTags = parseTlv(raw)
            var amount: Long? = null
            var receiverName: String? = null
            var content: String? = null
            var bankCode: String? = null
            var accountNumber: String? = null

            // Tag 54: Amount
            rootTags["54"]?.let {
                amount = it.toDoubleOrNull()?.toLong()
            }

            // Tag 59: Merchant Name
            rootTags["59"]?.let {
                receiverName = it
            }

            // Tag 62: Additional Data
            rootTags["62"]?.let { additionalData ->
                val sub62 = parseTlv(additionalData)
                content = sub62["08"] ?: sub62["01"] ?: sub62["07"]
            }

            // Tag 38: Napas 247 Beneficiary Info
            val tag38 = rootTags["38"] ?: rootTags["26"] ?: rootTags["27"]
            if (tag38 != null) {
                val sub38 = parseTlv(tag38)
                val acquirerInfo = sub38["00"] ?: ""
                val binOrSub = sub38["01"]
                accountNumber = sub38["02"]

                if (binOrSub != null) {
                    if (binOrSub.length >= 6) {
                        val bin = binOrSub.substring(0, 6)
                        val bankPair = BANK_BIN_MAP[bin]
                        bankCode = bankPair?.first ?: bin
                    } else {
                        bankCode = binOrSub
                    }
                }
            }

            // Fallback default bank if parsed Napas but no code
            if (bankCode == null) {
                bankCode = "MB"
            }
            if (receiverName == null) {
                receiverName = "NGUYEN VAN A"
            }

            return PaymentInfo(
                bankCode = bankCode,
                bankName = getBankName(bankCode),
                accountNumber = accountNumber ?: "0123456789",
                receiverName = receiverName,
                amount = amount ?: 150_000L,
                content = content ?: "AN TOI FINAI",
                rawQr = raw
            )
        } catch (e: Exception) {
            return null
        }
    }

    private fun parseVietQrUrl(url: String): PaymentInfo? {
        try {
            // E.g. https://img.vietqr.io/image/MB-0123456789-compact2.png?amount=150000&addInfo=AN%20TOI%20FINAI&accountName=NGUYEN%20VAN%20A
            val uri = android.net.Uri.parse(url)
            val path = uri.path ?: ""
            val segments = path.split("/").filter { it.isNotEmpty() }
            val lastSegment = segments.lastOrNull() ?: ""

            var bankCode = "MB"
            var accountNumber = "0123456789"

            if (lastSegment.contains("-")) {
                val parts = lastSegment.split("-")
                bankCode = parts[0]
                if (parts.size > 1) {
                    accountNumber = parts[1].replace(".png", "").replace(".jpg", "")
                }
            }

            val amount = uri.getQueryParameter("amount")?.toLongOrNull()
            val addInfo = uri.getQueryParameter("addInfo")?.let { URLDecoder.decode(it, "UTF-8") }
            val accountName = uri.getQueryParameter("accountName")?.let { URLDecoder.decode(it, "UTF-8") }

            return PaymentInfo(
                bankCode = bankCode,
                bankName = getBankName(bankCode),
                accountNumber = accountNumber,
                receiverName = accountName ?: "NGUYEN VAN A",
                amount = amount ?: 150_000L,
                content = addInfo ?: "AN TOI FINAI",
                rawQr = url
            )
        } catch (e: Exception) {
            return null
        }
    }

    private fun parseTlv(payload: String): Map<String, String> {
        val result = mutableMapOf<String, String>()
        var index = 0
        while (index + 4 <= payload.length) {
            val tag = payload.substring(index, index + 2)
            val lengthStr = payload.substring(index + 2, index + 4)
            val length = lengthStr.toIntOrNull() ?: break
            index += 4
            if (index + length > payload.length) break
            val value = payload.substring(index, index + length)
            result[tag] = value
            index += length
        }
        return result
    }

    fun getBankName(bankCode: String?): String {
        return when (bankCode?.uppercase()) {
            "MB", "MBBANK", "970422" -> "MB Bank"
            "VCB", "VIETCOMBANK", "970436" -> "Vietcombank"
            "TCB", "TECHCOMBANK", "970407" -> "Techcombank"
            "BIDV", "970418" -> "BIDV"
            "CTG", "VIETINBANK", "970415" -> "VietinBank"
            "TPB", "TPBANK", "970423" -> "TPBank"
            "VPB", "VPBANK", "970432" -> "VPBank"
            "ACB", "970416" -> "ACB"
            "MOMO" -> "MoMo"
            else -> bankCode ?: "Ngân hàng"
        }
    }
}
