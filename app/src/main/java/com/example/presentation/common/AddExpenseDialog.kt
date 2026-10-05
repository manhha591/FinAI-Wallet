package com.example.presentation.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.core.util.MoneyParser
import com.example.domain.model.Expense
import com.example.domain.model.ExpenseCategory
import com.example.domain.model.ExpenseStatus
import com.example.domain.model.PaymentMethod
import com.example.ui.theme.BorderOutline
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.SurfaceContainer
import com.example.ui.theme.TextSlate400
import com.example.ui.theme.TextSlate500
import com.example.ui.theme.TextSlate700
import com.example.ui.theme.TextSlate900

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddExpenseDialog(
    onDismiss: () -> Unit,
    onConfirm: (Expense) -> Unit,
    initialExpense: Expense? = null
) {
    var amountText by remember {
        mutableStateOf(initialExpense?.amount?.toString() ?: "")
    }
    var title by remember {
        mutableStateOf(initialExpense?.title ?: "")
    }
    var selectedCategory by remember {
        mutableStateOf(initialExpense?.category ?: ExpenseCategory.FOOD)
    }
    var selectedMethod by remember {
        mutableStateOf(initialExpense?.paymentMethod ?: PaymentMethod.BANK_TRANSFER.displayName)
    }
    var note by remember {
        mutableStateOf(initialExpense?.note ?: "")
    }
    var bankCode by remember {
        mutableStateOf(initialExpense?.bankCode ?: "MB")
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .testTag("add_expense_dialog")
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (initialExpense == null) "Thêm khoản chi" else "Sửa giao dịch",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSlate900
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Đóng",
                            tint = TextSlate500
                        )
                    }
                }

                // Amount Input
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Số tiền (VNĐ) *",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextSlate700
                    )
                    OutlinedTextField(
                        value = amountText,
                        onValueChange = { amountText = it.filter { ch -> ch.isDigit() } },
                        placeholder = { Text("0 ₫", color = TextSlate400) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("amount_input"),
                        shape = RoundedCornerShape(14.dp),
                        trailingIcon = {
                            Text(
                                "₫",
                                fontWeight = FontWeight.Bold,
                                color = TextSlate500,
                                modifier = Modifier.padding(end = 12.dp)
                            )
                        }
                    )

                    // Quick amount chips
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        listOf(20_000L, 50_000L, 100_000L, 200_000L, 500_000L).forEach { chipAmt ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = SurfaceContainer,
                                modifier = Modifier.clickable {
                                    val current = amountText.toLongOrNull() ?: 0L
                                    amountText = (current + chipAmt).toString()
                                }
                            ) {
                                Text(
                                    text = "+${chipAmt / 1000}k",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = TextSlate700,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }

                // Title Input
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Nội dung / Tiêu đề *",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextSlate700
                    )
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        placeholder = { Text("Ví dụ: Ăn phở bò, đổ xăng...", color = TextSlate400) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("title_input"),
                        shape = RoundedCornerShape(14.dp)
                    )
                }

                // Category Selection
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Danh mục",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextSlate700
                    )
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        ExpenseCategory.entries.forEach { category ->
                            val isSelected = selectedCategory == category
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) PrimaryBlue else SurfaceContainer,
                                border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, BorderOutline),
                                modifier = Modifier.clickable { selectedCategory = category }
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .background(
                                                color = if (isSelected) Color.White else Color(category.colorHex),
                                                shape = CircleShape
                                            )
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = category.displayName,
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) Color.White else TextSlate900
                                    )
                                }
                            }
                        }
                    }
                }

                // Payment Method
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Phương thức thanh toán",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextSlate700
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        PaymentMethod.entries.forEach { method ->
                            val isSelected = selectedMethod == method.displayName
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) PrimaryBlue.copy(alpha = 0.12f) else SurfaceContainer,
                                border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, PrimaryBlue) else null,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { selectedMethod = method.displayName }
                            ) {
                                Text(
                                    text = method.displayName,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) PrimaryBlue else TextSlate700,
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }
                }

                // Note
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Ghi chú thêm",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextSlate700
                    )
                    OutlinedTextField(
                        value = note,
                        onValueChange = { note = it },
                        placeholder = { Text("Tùy chọn...", color = TextSlate400) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        maxLines = 2
                    )
                }

                // Actions
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Hủy", color = TextSlate500, fontWeight = FontWeight.SemiBold)
                    }

                    val parsedAmount = amountText.toLongOrNull() ?: 0L
                    val isValid = parsedAmount > 0 && title.isNotBlank()

                    Button(
                        onClick = {
                            if (isValid) {
                                onConfirm(
                                    Expense(
                                        id = initialExpense?.id ?: 0L,
                                        amount = parsedAmount,
                                        title = title.trim(),
                                        category = selectedCategory,
                                        categoryId = (selectedCategory.ordinal + 1).toLong(),
                                        paymentMethod = selectedMethod,
                                        note = note.takeIf { it.isNotBlank() },
                                        bankCode = if (selectedMethod == PaymentMethod.BANK_TRANSFER.displayName) bankCode else null,
                                        status = initialExpense?.status ?: ExpenseStatus.PAID,
                                        transactionDate = initialExpense?.transactionDate ?: System.currentTimeMillis()
                                    )
                                )
                            }
                        },
                        enabled = isValid,
                        modifier = Modifier
                            .weight(2f)
                            .height(48.dp)
                            .testTag("save_expense_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("Lưu giao dịch", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }
}
