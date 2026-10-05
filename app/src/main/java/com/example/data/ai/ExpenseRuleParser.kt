package com.example.data.ai

import com.example.core.util.MoneyParser
import com.example.domain.model.ExpenseCategory
import com.example.domain.model.ExpenseIntent
import java.util.Locale

class ExpenseRuleParser {

    private val categoryKeywords: Map<ExpenseCategory, List<String>> = mapOf(
        ExpenseCategory.FOOD to listOf(
            "phở", "bún", "mì", "cơm", "ăn", "sáng", "trưa", "tối", "cà phê", "cafe", "coffee",
            "trà", "nước mía", "lẩu", "nướng", "bánh mì", "ăn vặt", "trà sữa", "highland", "starbucks",
            "phúc long", "phuclong", "kfc", "lotteria", "pizza", "bbq", "chè", "gỏi", "bò", "gà"
        ),
        ExpenseCategory.TRANSPORT to listOf(
            "xăng", "xe", "đổ xăng", "grab", "be", "gojek", "taxi", "gửi xe", "sửa xe", "bảo dưỡng",
            "vé xe", "xe buýt", "bus", "rửa xe", "thay nhớt", "xăm lốp", "vinfast", "xanh sm"
        ),
        ExpenseCategory.SHOPPING to listOf(
            "mua", "áo", "quần", "giày", "dép", "túi", "shopee", "lazada", "tiki", "siêu thị",
            "winmart", "coopmart", "bách hóa", "uniqlo", "zara", "mỹ phẩm", "son", "váy"
        ),
        ExpenseCategory.BILLS to listOf(
            "điện", "tiền điện", "nước", "tiền nước", "internet", "wifi", "4g", "mạng", "evn",
            "hóa đơn", "chung cư", "phí dịch vụ", "truyền hình", "rác"
        ),
        ExpenseCategory.ENTERTAINMENT to listOf(
            "phim", "cgv", "cinema", "game", "steam", "nạp game", "netflix", "spotify", "bar",
            "pub", "bida", "karaoke", "nhậu", "tiệc"
        ),
        ExpenseCategory.HEALTH to listOf(
            "thuốc", "nhà thuốc", "khám", "bệnh viện", "bác sĩ", "nha khoa", "răng", "gym",
            "yoga", "fitness", "vitamin", "sức khỏe"
        ),
        ExpenseCategory.EDUCATION to listOf(
            "sách", "vở", "bút", "khóa học", "học phí", "tiếng anh", "ielts", "toeic", "udemy",
            "coursera", "học tập", "trường"
        ),
        ExpenseCategory.TRAVEL to listOf(
            "vé máy bay", "khách sạn", "hotel", "resort", "tour", "homestay", "du lịch", "vietjet",
            "bamboo", "vietnam airlines"
        )
    )

    fun parse(input: String): ExpenseIntent? {
        val trimmed = input.trim()
        if (trimmed.isEmpty()) return null

        val amount = MoneyParser.parseAmount(trimmed) ?: return null

        // Detect category
        val lower = trimmed.lowercase(Locale.ROOT)
        var detectedCategory = ExpenseCategory.OTHER
        var maxMatchScore = 0

        for ((category, keywords) in categoryKeywords) {
            for (keyword in keywords) {
                if (lower.contains(keyword)) {
                    val score = keyword.length
                    if (score > maxMatchScore) {
                        maxMatchScore = score
                        detectedCategory = category
                    }
                }
            }
        }

        // Clean title: remove amount part and common fillers
        var cleanedTitle = trimmed
            .replace(Regex("""(?i)\b\d+(?:[.,]\d+)?\s*(k|nghìn|ngàn|triệu|trieu|tr|m|đồng|dong|đ|d)?\b"""), "")
            .replace(Regex("""(?i)\b\d+tr\d+\b"""), "")
            .replace(Regex("""(?i)\b\d+k\d+\b"""), "")
            .trim()

        if (cleanedTitle.isEmpty()) {
            cleanedTitle = detectedCategory.displayName
        } else {
            // Capitalize first letter
            cleanedTitle = cleanedTitle.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString()
            }
        }

        val confidence = if (maxMatchScore > 0) 0.95f else 0.75f

        return ExpenseIntent(
            amount = amount,
            title = cleanedTitle,
            category = detectedCategory,
            dateTimestamp = System.currentTimeMillis(),
            confidence = confidence,
            rawText = trimmed
        )
    }
}
