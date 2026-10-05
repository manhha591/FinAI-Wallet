package com.example

import android.app.Application
import com.example.data.ai.AiEngine
import com.example.data.ai.ExpenseRuleParser
import com.example.data.ai.LocalAiEngine
import com.example.data.local.FinAiDatabase
import com.example.data.payment.AndroidBankAppLauncher
import com.example.data.payment.BankAppLauncher
import com.example.data.repository.ExpenseRepositoryImpl
import com.example.domain.repository.ExpenseRepository
import com.example.domain.usecase.AddExpenseUseCase
import com.example.domain.usecase.GetExpensesUseCase
import com.example.domain.usecase.GetStatisticsUseCase
import com.example.domain.usecase.ParseNaturalExpenseUseCase
import com.example.domain.usecase.ProcessAiQueryUseCase
import com.example.domain.usecase.UpdateExpenseStatusUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class FinAiApplication : Application() {

    val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    val database by lazy { FinAiDatabase.getDatabase(this, applicationScope) }

    val expenseRepository: ExpenseRepository by lazy {
        ExpenseRepositoryImpl(
            expenseDao = database.expenseDao(),
            categoryDao = database.categoryDao(),
            budgetDao = database.budgetDao()
        )
    }

    val ruleParser: ExpenseRuleParser by lazy { ExpenseRuleParser() }

    val aiEngine: AiEngine by lazy {
        LocalAiEngine(ruleParser = ruleParser)
    }

    val bankAppLauncher: BankAppLauncher by lazy {
        AndroidBankAppLauncher(this)
    }

    // Use cases
    val addExpenseUseCase by lazy { AddExpenseUseCase(expenseRepository) }
    val getExpensesUseCase by lazy { GetExpensesUseCase(expenseRepository) }
    val getStatisticsUseCase by lazy { GetStatisticsUseCase(expenseRepository) }
    val updateExpenseStatusUseCase by lazy { UpdateExpenseStatusUseCase(expenseRepository) }
    val parseNaturalExpenseUseCase by lazy { ParseNaturalExpenseUseCase(aiEngine) }
    val processAiQueryUseCase by lazy { ProcessAiQueryUseCase(aiEngine, expenseRepository) }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        lateinit var instance: FinAiApplication
            private set
    }
}
