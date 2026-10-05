package com.example.data.ai

import com.example.core.util.MoneyParser
import com.example.domain.model.ExpenseCategory
import com.example.domain.model.ExpenseIntent
import com.example.domain.model.FinancialIntent
import com.example.domain.model.TimePeriod
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.Locale

class LocalAiEngine(
    private val ruleParser: ExpenseRuleParser = ExpenseRuleParser()
) : AiEngine {

    override suspend fun parseExpense(text: String): ExpenseIntent? = withContext(Dispatchers.Default) {
        // Step 1: Rule-Based parser pipeline (Regex & MoneyParser)
        val ruleResult = ruleParser.parse(text)
        if (ruleResult != null) {
            return@withContext ruleResult
        }

        // Step 2: Semantic fallback for natural speech patterns
        val lower = text.lowercase(Locale.ROOT).trim()
        val amount = MoneyParser.parseAmount(lower) ?: return@withContext null

        val cat = when {
            lower.contains("ăn") || lower.contains("uống") || lower.contains("cơm") || lower.contains("nước") -> ExpenseCategory.FOOD
            lower.contains("xe") || lower.contains("xăng") || lower.contains("đi lại") -> ExpenseCategory.TRANSPORT
            lower.contains("mua") || lower.contains("sắm") || lower.contains("đồ") -> ExpenseCategory.SHOPPING
            lower.contains("điện") || lower.contains("nước") || lower.contains("net") -> ExpenseCategory.BILLS
            lower.contains("chơi") || lower.contains("phim") || lower.contains("tiệc") -> ExpenseCategory.ENTERTAINMENT
            lower.contains("thuốc") || lower.contains("bệnh") || lower.contains("khám") -> ExpenseCategory.HEALTH
            lower.contains("học") || lower.contains("sách") -> ExpenseCategory.EDUCATION
            else -> ExpenseCategory.OTHER
        }

        ExpenseIntent(
            amount = amount,
            title = text.trim(),
            category = cat,
            dateTimestamp = System.currentTimeMillis(),
            confidence = 0.85f,
            rawText = text
        )
    }

    override suspend fun parseQuery(text: String): FinancialIntent = withContext(Dispatchers.Default) {
        val lower = text.lowercase(Locale.ROOT).trim()

        // 1. Direct Add Expense request like "Thêm ăn sáng 50k", "Thêm đổ xăng 100 nghìn" or simple "ăn phở 50k"
        if (lower.startsWith("thêm ") || lower.startsWith("ghi ") || lower.startsWith("nhập ")) {
            val stripped = lower.removePrefix("thêm ").removePrefix("ghi ").removePrefix("nhập ")
            val expenseIntent = parseExpense(stripped)
            if (expenseIntent != null) {
                return@withContext FinancialIntent.AddExpense(expenseIntent)
            }
        }

        // Check if query is actually an expense statement e.g. "ăn phở bò tái 55k trưa nay"
        val possibleExpense = parseExpense(lower)
        if (possibleExpense != null && possibleExpense.amount != null && !lower.contains("bao nhiêu") && !lower.contains("hỏi") && !lower.contains("tìm")) {
            return@withContext FinancialIntent.AddExpense(possibleExpense)
        }

        // 2. Budget query: "ngân sách", "còn bao nhiêu tiền", "hạn mức"
        if (lower.contains("ngân sách") || lower.contains("còn bao nhiêu") || lower.contains("định mức")) {
            val cal = Calendar.getInstance()
            return@withContext FinancialIntent.GetBudgetStatus(
                month = cal.get(Calendar.MONTH) + 1,
                year = cal.get(Calendar.YEAR)
            )
        }

        // 3. Comparison query: "so sánh"
        if (lower.contains("so sánh")) {
            return@withContext FinancialIntent.ComparePeriods(
                compareDescription = if (lower.contains("tuần")) "WEEK" else "MONTH"
            )
        }

        // 4. Top category: "danh mục nào", "nhiều nhất", "top"
        if (lower.contains("nhiều nhất") || lower.contains("top") || lower.contains("chi nhiều")) {
            val period = parsePeriod(lower)
            return@withContext FinancialIntent.GetTopCategory(period)
        }

        // 5. Category specific query: "ăn uống", "mua sắm", "xăng", "di chuyển"
        val detectedCat = findCategoryInQuery(lower)
        if (detectedCat != null && (lower.contains("bao nhiêu") || lower.contains("hết") || lower.contains("tổng"))) {
            val period = parsePeriod(lower)
            return@withContext FinancialIntent.GetExpenseByCategory(detectedCat, period)
        }

        // 6. Total expense query: "tiêu bao nhiêu", "chi bao nhiêu", "tổng chi"
        if (lower.contains("bao nhiêu") || lower.contains("tổng") || lower.contains("hết mấy") || lower.contains("đã chi")) {
            val period = parsePeriod(lower)
            return@withContext FinancialIntent.GetTotalExpense(period)
        }

        // 7. Search transaction: "tìm", "tiền xăng", "coffee 45k"
        if (lower.contains("tìm ") || lower.contains("giao dịch") || lower.contains("khoản chi")) {
            val query = lower.replace("tìm ", "").replace("giao dịch ", "").trim()
            return@withContext FinancialIntent.SearchTransaction(query)
        }

        // Fallback search
        FinancialIntent.SearchTransaction(text.trim())
    }

    override suspend fun generateAnswer(intent: FinancialIntent, verifiedFacts: String): String = withContext(Dispatchers.Default) {
        // Compose natural language answers adhering strictly to verified database facts
        when (intent) {
            is FinancialIntent.GetExpenseByCategory -> {
                "Phân tích từ database SQLite: $verifiedFacts"
            }
            is FinancialIntent.GetBudgetStatus -> {
                "$verifiedFacts"
            }
            is FinancialIntent.GetTotalExpense -> {
                "Dựa trên dữ liệu giao dịch: $verifiedFacts"
            }
            is FinancialIntent.ComparePeriods -> {
                "Báo cáo đối soát chi tiêu: $verifiedFacts"
            }
            is FinancialIntent.GetTopCategory -> {
                "Thống kê cơ cấu chi tiêu: $verifiedFacts"
            }
            is FinancialIntent.SearchTransaction -> {
                "Kết quả tra cứu lịch sử: $verifiedFacts"
            }
            is FinancialIntent.AddExpense -> {
                "Đã nhận diện giao dịch từ ngôn ngữ tự nhiên. $verifiedFacts"
            }
            is FinancialIntent.GetExpenseByDate -> {
                "Tổng kết chi tiêu: $verifiedFacts"
            }
            is FinancialIntent.Unknown -> {
                "FinAI đã xử lý yêu cầu: $verifiedFacts"
            }
        }
    }

    private fun parsePeriod(lower: String): TimePeriod {
        return when {
            lower.contains("hôm nay") || lower.contains("ngày nay") -> TimePeriod.TODAY
            lower.contains("tuần") -> TimePeriod.THIS_WEEK
            lower.contains("năm") -> TimePeriod.THIS_YEAR
            else -> TimePeriod.THIS_MONTH
        }
    }

    private fun findCategoryInQuery(lower: String): ExpenseCategory? {
        return when {
            lower.contains("ăn") || lower.contains("uống") || lower.contains("phở") || lower.contains("cafe") || lower.contains("cà phê") -> ExpenseCategory.FOOD
            lower.contains("xăng") || lower.contains("xe") || lower.contains("grab") || lower.contains("di chuyển") -> ExpenseCategory.TRANSPORT
            lower.contains("mua sắm") || lower.contains("mua đồ") || lower.contains("quần áo") || lower.contains("shopee") -> ExpenseCategory.SHOPPING
            lower.contains("điện") || lower.contains("nước") || lower.contains("hóa đơn") || lower.contains("internet") -> ExpenseCategory.BILLS
            lower.contains("giải trí") || lower.contains("phim") || lower.contains("game") -> ExpenseCategory.ENTERTAINMENT
            lower.contains("sức khỏe") || lower.contains("thuốc") || lower.contains("khám") -> ExpenseCategory.HEALTH
            lower.contains("học") || lower.contains("sách") -> ExpenseCategory.EDUCATION
            lower.contains("du lịch") || lower.contains("hotel") -> ExpenseCategory.TRAVEL
            else -> null
        }
    }
}
