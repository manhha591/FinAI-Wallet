package com.example.core.util

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

object MoneyParser {

    private val MONEY_REGEX = Regex(
        """(?i)(\d+(?:[.,]\d+)?)\s*(k|nghìn|ngàn|triệu|trieu|tr|m|đồng|dong|đ|d)?(?:\b|$)"""
    )

    // Match patterns like "1tr2", "2tr5", "1k5"
    private val COMPOUND_TR_REGEX = Regex(
        """(?i)(\d+)\s*(?:tr|triệu|trieu)\s*(\d+)(?:\s*(?:k|nghìn|ngàn))?"""
    )

    private val COMPOUND_K_REGEX = Regex(
        """(?i)(\d+)\s*(?:k|nghìn|ngàn)\s*(\d+)"""
    )

    fun parseAmount(text: String): Long? {
        val trimmed = text.trim()

        // 1. Compound pattern like "1tr2" -> 1.200.000, "1tr250" -> 1.250.000
        val compMatch = COMPOUND_TR_REGEX.find(trimmed)
        if (compMatch != null) {
            val main = compMatch.groupValues[1].toLongOrNull() ?: 0L
            val subStr = compMatch.groupValues[2]
            val sub = when (subStr.length) {
                1 -> subStr.toLong() * 100_000L
                2 -> subStr.toLong() * 10_000L
                3 -> subStr.toLong() * 1_000L
                else -> subStr.toLongOrNull() ?: 0L
            }
            return (main * 1_000_000L) + sub
        }

        // 1b. Compound pattern like "1k5" -> 1500
        val compKMatch = COMPOUND_K_REGEX.find(trimmed)
        if (compKMatch != null) {
            val main = compKMatch.groupValues[1].toLongOrNull() ?: 0L
            val subStr = compKMatch.groupValues[2]
            val sub = when (subStr.length) {
                1 -> subStr.toLong() * 100L
                2 -> subStr.toLong() * 10L
                else -> subStr.toLongOrNull() ?: 0L
            }
            return (main * 1_000L) + sub
        }

        // 2. Standard pattern with unit
        val matches = MONEY_REGEX.findAll(trimmed).toList()
        for (match in matches) {
            val numberStr = match.groupValues[1].replace(",", ".")
            val unit = match.groupValues[2].lowercase(Locale.ROOT)
            val baseNum = numberStr.toDoubleOrNull() ?: continue

            return when {
                unit.startsWith("tr") || unit.startsWith("m") -> {
                    (baseNum * 1_000_000).toLong()
                }
                unit.startsWith("k") || unit.startsWith("ngh") || unit.startsWith("ng") -> {
                    (baseNum * 1_000).toLong()
                }
                unit == "đ" || unit == "d" || unit.startsWith("đ") || unit.startsWith("d") -> {
                    baseNum.toLong()
                }
                baseNum >= 1000 -> {
                    baseNum.toLong()
                }
                else -> {
                    // E.g. "50" in "ăn phở 50" often means 50k in VN context
                    if (baseNum in 1.0..999.0 && (trimmed.contains("k") || trimmed.contains("nghìn") || trimmed.contains("ngàn"))) {
                        (baseNum * 1_000).toLong()
                    } else if (baseNum in 10.0..999.0) {
                        (baseNum * 1_000).toLong() // In VN, "ăn phở 50" -> 50.000d
                    } else {
                        baseNum.toLong()
                    }
                }
            }
        }

        // 3. Fallback for raw digits with thousand separators e.g. "150.000" or "150,000"
        val cleanNumberStr = trimmed.filter { it.isDigit() }
        if (cleanNumberStr.isNotEmpty() && cleanNumberStr.length in 4..12) {
            return cleanNumberStr.toLongOrNull()
        }

        return null
    }

    fun formatCurrency(amount: Long, withSymbol: Boolean = true): String {
        val symbols = DecimalFormatSymbols(Locale.GERMANY).apply {
            groupingSeparator = '.'
            decimalSeparator = ','
        }
        val formatter = DecimalFormat("#,###", symbols)
        val formatted = formatter.format(amount)
        return if (withSymbol) "$formatted ₫" else formatted
    }
}
