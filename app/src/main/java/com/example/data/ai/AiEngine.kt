package com.example.data.ai

import com.example.domain.model.ExpenseIntent
import com.example.domain.model.FinancialIntent

interface AiEngine {
    suspend fun parseExpense(text: String): ExpenseIntent?
    suspend fun parseQuery(text: String): FinancialIntent
    suspend fun generateAnswer(intent: FinancialIntent, verifiedFacts: String): String
}
