package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.local.dao.BudgetDao
import com.example.data.local.dao.CategoryDao
import com.example.data.local.dao.ExpenseDao
import com.example.data.local.entity.BudgetEntity
import com.example.data.local.entity.CategoryEntity
import com.example.data.local.entity.ExpenseEntity
import com.example.domain.model.ExpenseCategory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar

@Database(
    entities = [
        ExpenseEntity::class,
        CategoryEntity::class,
        BudgetEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class FinAiDatabase : RoomDatabase() {
    abstract fun expenseDao(): ExpenseDao
    abstract fun categoryDao(): CategoryDao
    abstract fun budgetDao(): BudgetDao

    companion object {
        @Volatile
        private var INSTANCE: FinAiDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): FinAiDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FinAiDatabase::class.java,
                    "finai_database"
                )
                    .addCallback(DatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database)
                    }
                }
            }
        }

        suspend fun populateInitialData(database: FinAiDatabase) {
            val categoryDao = database.categoryDao()
            val budgetDao = database.budgetDao()
            val expenseDao = database.expenseDao()

            // Populate categories
            val categories = ExpenseCategory.entries.mapIndexed { index, cat ->
                CategoryEntity(
                    id = (index + 1).toLong(),
                    name = cat.displayName,
                    icon = cat.iconName,
                    isDefault = true
                )
            }
            categoryDao.insertAll(categories)

            // Current calendar
            val cal = Calendar.getInstance()
            val currentMonth = cal.get(Calendar.MONTH) + 1
            val currentYear = cal.get(Calendar.YEAR)

            // Populate Budget (10.000.000 VND)
            budgetDao.insertOrUpdate(
                BudgetEntity(
                    id = 1,
                    month = currentMonth,
                    year = currentYear,
                    amount = 10_000_000L
                )
            )

            // Seed realistic transactions from screenshot
            val now = System.currentTimeMillis()
            val hourMs = 3600_000L
            val dayMs = 86400_000L

            val initialExpenses = listOf(
                ExpenseEntity(
                    amount = 55_000L,
                    title = "Ăn trưa bún bò",
                    categoryId = 1L, // Ăn uống
                    note = "Bún bò Huế bà Nga",
                    paymentMethod = "Chuyển khoản",
                    bankCode = "MB",
                    accountNumber = "0123456789",
                    receiverName = "NGUYEN VAN A",
                    paymentContent = "AN TRUA BUN BO",
                    status = "PAID",
                    transactionDate = now - (2 * hourMs),
                    createdAt = now - (2 * hourMs),
                    updatedAt = now - (2 * hourMs)
                ),
                ExpenseEntity(
                    amount = 90_000L,
                    title = "Đổ xăng xe máy",
                    categoryId = 2L, // Di chuyển
                    note = "Cây xăng Petrolimex số 3",
                    paymentMethod = "Tiền mặt",
                    bankCode = null,
                    accountNumber = null,
                    receiverName = null,
                    paymentContent = null,
                    status = "PAID",
                    transactionDate = now - (6 * hourMs),
                    createdAt = now - (6 * hourMs),
                    updatedAt = now - (6 * hourMs)
                ),
                ExpenseEntity(
                    amount = 40_000L,
                    title = "Cà phê sáng",
                    categoryId = 1L, // Ăn uống
                    note = "The Coffee House",
                    paymentMethod = "Chuyển khoản",
                    bankCode = "MB",
                    accountNumber = "0123456789",
                    receiverName = "NGUYEN VAN A",
                    paymentContent = "CA PHE SANG",
                    status = "PENDING",
                    transactionDate = now - (8 * hourMs),
                    createdAt = now - (8 * hourMs),
                    updatedAt = now - (8 * hourMs)
                ),
                ExpenseEntity(
                    amount = 210_000L,
                    title = "Mua sách lập trình",
                    categoryId = 7L, // Học tập
                    note = "Sách Kotlin Clean Architecture",
                    paymentMethod = "Chuyển khoản",
                    bankCode = "VCB",
                    accountNumber = "0071001234567",
                    receiverName = "FAHASA BOOKSTORE",
                    paymentContent = "MUA SACH",
                    status = "PAID",
                    transactionDate = now - (1 * dayMs),
                    createdAt = now - (1 * dayMs),
                    updatedAt = now - (1 * dayMs)
                ),
                ExpenseEntity(
                    amount = 1_500_000L,
                    title = "Mua sắm quần áo Uniqlo",
                    categoryId = 3L, // Mua sắm
                    note = "Áo khoác gió & áo thun",
                    paymentMethod = "Chuyển khoản",
                    bankCode = "TCB",
                    accountNumber = "19034567890123",
                    receiverName = "UNIQLO VIETNAM",
                    paymentContent = "DON HANG UNIQLO",
                    status = "PAID",
                    transactionDate = now - (4 * dayMs),
                    createdAt = now - (4 * dayMs),
                    updatedAt = now - (4 * dayMs)
                ),
                ExpenseEntity(
                    amount = 950_000L,
                    title = "Hóa đơn điện nước tháng này",
                    categoryId = 5L, // Hóa đơn
                    note = "Điện lực EVN Hà Nội",
                    paymentMethod = "Chuyển khoản",
                    bankCode = "MB",
                    accountNumber = "0123456789",
                    receiverName = "EVN HANOI",
                    paymentContent = "TIEN DIEN T9",
                    status = "PAID",
                    transactionDate = now - (7 * dayMs),
                    createdAt = now - (7 * dayMs),
                    updatedAt = now - (7 * dayMs)
                ),
                ExpenseEntity(
                    amount = 760_000L,
                    title = "Grab Car đi sân bay Nội Bài",
                    categoryId = 2L, // Di chuyển
                    note = "Công tác",
                    paymentMethod = "Ví điện tử",
                    bankCode = "MOMO",
                    accountNumber = null,
                    receiverName = "GRAB VIETNAM",
                    paymentContent = "CUOC XE GRAB",
                    status = "PAID",
                    transactionDate = now - (10 * dayMs),
                    createdAt = now - (10 * dayMs),
                    updatedAt = now - (10 * dayMs)
                ),
                ExpenseEntity(
                    amount = 500_000L,
                    title = "Vé xem phim & Bắp nước CGV",
                    categoryId = 4L, // Giải trí
                    note = "Phim cuối tuần với bạn bè",
                    paymentMethod = "Chuyển khoản",
                    bankCode = "MB",
                    accountNumber = "0123456789",
                    receiverName = "CGV CINEMAS",
                    paymentContent = "VE PHIM CGV",
                    status = "PAID",
                    transactionDate = now - (12 * dayMs),
                    createdAt = now - (12 * dayMs),
                    updatedAt = now - (12 * dayMs)
                ),
                ExpenseEntity(
                    amount = 1_805_000L,
                    title = "Tiệc ăn tối gia đình cuối tuần",
                    categoryId = 1L, // Ăn uống
                    note = "Nhà hàng lẩu nướng Sumo",
                    paymentMethod = "Chuyển khoản",
                    bankCode = "MB",
                    accountNumber = "0123456789",
                    receiverName = "SUMO BBQ",
                    paymentContent = "AN TOI SUMO",
                    status = "PAID",
                    transactionDate = now - (15 * dayMs),
                    createdAt = now - (15 * dayMs),
                    updatedAt = now - (15 * dayMs)
                ),
                ExpenseEntity(
                    amount = 350_000L,
                    title = "Chi phí linh tinh khác",
                    categoryId = 9L, // Khác
                    note = "Đồ dùng tạp hóa",
                    paymentMethod = "Tiền mặt",
                    bankCode = null,
                    accountNumber = null,
                    receiverName = null,
                    paymentContent = null,
                    status = "PAID",
                    transactionDate = now - (18 * dayMs),
                    createdAt = now - (18 * dayMs),
                    updatedAt = now - (18 * dayMs)
                )
            )

            expenseDao.insertAll(initialExpenses)
        }
    }
}
