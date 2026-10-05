package com.example

import com.example.core.util.MoneyParser
import com.example.data.ai.ExpenseRuleParser
import com.example.data.ai.LocalAiEngine
import com.example.data.qr.VietQrParser
import com.example.domain.model.ExpenseCategory
import com.example.domain.model.FinancialIntent
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class MoneyAndExpenseParserTest {

    @Test
    fun testMoneyParser_variousVietnameseFormats() {
        assertEquals(50_000L, MoneyParser.parseAmount("50k"))
        assertEquals(50_000L, MoneyParser.parseAmount("50K"))
        assertEquals(50_000L, MoneyParser.parseAmount("50 nghìn"))
        assertEquals(50_000L, MoneyParser.parseAmount("50 ngàn"))
        assertEquals(100_000L, MoneyParser.parseAmount("đổ xăng 100 nghìn"))
        assertEquals(1_000_000L, MoneyParser.parseAmount("1tr"))
        assertEquals(1_000_000L, MoneyParser.parseAmount("1 triệu"))
        assertEquals(1_200_000L, MoneyParser.parseAmount("1tr2"))
        assertEquals(1_200_000L, MoneyParser.parseAmount("1.2 triệu"))
        assertEquals(350_000L, MoneyParser.parseAmount("mua áo 350k"))
        assertEquals(45_000L, MoneyParser.parseAmount("coffee 45k"))
    }

    @Test
    fun testExpenseRuleParser_detectsIntentAndCategory() {
        val parser = ExpenseRuleParser()

        val phoIntent = parser.parse("ăn phở 50k")
        assertNotNull(phoIntent)
        assertEquals(50_000L, phoIntent?.amount)
        assertEquals(ExpenseCategory.FOOD, phoIntent?.category)
        assertTrue(phoIntent?.title?.contains("Ăn phở") == true)

        val xangIntent = parser.parse("đổ xăng 100 nghìn")
        assertNotNull(xangIntent)
        assertEquals(100_000L, xangIntent?.amount)
        assertEquals(ExpenseCategory.TRANSPORT, xangIntent?.category)

        val aoIntent = parser.parse("mua áo 350k")
        assertNotNull(aoIntent)
        assertEquals(350_000L, aoIntent?.amount)
        assertEquals(ExpenseCategory.SHOPPING, aoIntent?.category)
    }

    @Test
    fun testVietQrParser_parsesEmvCoString() {
        val rawEmvCo = "00020101021238540010A0000007270124000697042202100123456789530370454061500005802VN5912NGUYEN VAN A62160812AN TOI FINAI6304ABCD"
        val parsed = VietQrParser.parse(rawEmvCo)

        assertNotNull(parsed)
        assertEquals("0123456789", parsed?.accountNumber)
        assertEquals(150_000L, parsed?.amount)
        assertEquals("NGUYEN VAN A", parsed?.receiverName)
        assertEquals("AN TOI FINAI", parsed?.content)
    }

    @Test
    fun testLocalAiEngine_parsesQueries() = runBlocking {
        val engine = LocalAiEngine()

        val foodQuery = engine.parseQuery("Tháng này tôi tiêu ăn uống bao nhiêu?")
        assertTrue(foodQuery is FinancialIntent.GetExpenseByCategory)
        val catIntent = foodQuery as FinancialIntent.GetExpenseByCategory
        assertEquals(ExpenseCategory.FOOD, catIntent.category)

        val budgetQuery = engine.parseQuery("Tôi còn bao nhiêu ngân sách tháng này?")
        assertTrue(budgetQuery is FinancialIntent.GetBudgetStatus)

        val compareQuery = engine.parseQuery("So sánh tuần này và tuần trước")
        assertTrue(compareQuery is FinancialIntent.ComparePeriods)
    }
}
