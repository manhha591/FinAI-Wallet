package com.example.data.ai

import com.example.domain.model.ExpenseIntent
import com.example.domain.model.FinancialIntent

class CloudAiEngine(
    private val localFallback: LocalAiEngine = LocalAiEngine()
) : AiEngine {
    // Cloud AI implementation stub that gracefully delegates to local pipeline
    // when offline or by default, maintaining identical interface for future Gemini API calls
    override suspend fun parseExpense(text: String): ExpenseIntent? {
        return localFallback.parseExpense(text)
    }

    override suspend fun parseQuery(text: String): FinancialIntent {
        return localFallback.parseQuery(text)
    }

    override suspend fun generateAnswer(intent: FinancialIntent, verifiedFacts: String): String {
        return localFallback.generateAnswer(intent, verifiedFacts)
    }
}
