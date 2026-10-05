package com.example.presentation.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.outlined.DirectionsCar
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.RamenDining
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.util.MoneyParser
import com.example.domain.model.Expense
import com.example.domain.model.ExpenseCategory
import com.example.domain.model.ExpenseStatus
import com.example.presentation.common.AddExpenseDialog
import com.example.ui.theme.BackgroundCanvas
import com.example.ui.theme.BorderOutline
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.PrimaryBlueContainer
import com.example.ui.theme.SecondaryEmerald
import com.example.ui.theme.SecondaryEmeraldContainer
import com.example.ui.theme.SurfaceContainer
import com.example.ui.theme.SurfaceContainerLow
import com.example.ui.theme.TextSlate400
import com.example.ui.theme.TextSlate500
import com.example.ui.theme.TextSlate700
import com.example.ui.theme.TextSlate900
import com.example.ui.theme.WarningAmber
import com.example.ui.theme.WarningAmberContainer

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToScanner: () -> Unit,
    onNavigateToChat: () -> Unit,
    onNavigateToStats: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var showAddDialog by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.userMessage) {
        uiState.userMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearUserMessage()
        }
    }

    if (showAddDialog) {
        AddExpenseDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { expense ->
                viewModel.addManualExpense(expense)
                showAddDialog = false
            }
        )
    }

    Box(modifier = modifier.fillMaxSize().background(BackgroundCanvas)) {
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // Greeting & Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box {
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = PrimaryBlueContainer,
                                modifier = Modifier.size(48.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text("MA", fontWeight = FontWeight.Bold, color = PrimaryBlue, fontSize = 16.sp)
                                }
                            }
                            Box(
                                modifier = Modifier
                                    .size(14.dp)
                                    .background(SecondaryEmerald, CircleShape)
                                    .border(2.dp, Color.White, CircleShape)
                                    .align(Alignment.BottomEnd)
                            )
                        }

                        Column {
                            Text(
                                text = "Chào buổi sáng, chúc ngày tốt lành!",
                                fontSize = 12.sp,
                                color = TextSlate500,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "Minh Anh 👋",
                                fontSize = 19.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextSlate900
                            )
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = SecondaryEmeraldContainer,
                            border = androidx.compose.foundation.BorderStroke(1.dp, SecondaryEmerald.copy(alpha = 0.4f)),
                            modifier = Modifier.padding(end = 4.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Box(modifier = Modifier.size(6.dp).background(SecondaryEmerald, CircleShape))
                                Text(
                                    text = "Offline AI",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF065F46)
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color.White,
                            border = androidx.compose.foundation.BorderStroke(1.dp, BorderOutline),
                            modifier = Modifier.size(40.dp)
                        ) {
                            IconButton(onClick = { /* Settings / notifications */ }) {
                                Icon(
                                    imageVector = Icons.Default.Tune,
                                    contentDescription = "Tùy chọn",
                                    tint = TextSlate500,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Monthly Budget Hero Card (Matching Image 8)
            item {
                val stats = uiState.statistics
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = PrimaryBlue),
                    modifier = Modifier.fillMaxWidth().testTag("monthly_budget_card")
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color.White.copy(alpha = 0.15f),
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.CalendarToday,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = "Ngân Sách Tháng 9",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White
                                )
                            }

                            Surface(
                                shape = CircleShape,
                                color = Color.White.copy(alpha = 0.15f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.25f))
                            ) {
                                val percentStr = String.format("%.1f", stats.budgetUsedPercent)
                                Text(
                                    text = "$percentStr% Đã dùng",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Column {
                            Text(
                                text = "Tổng chi tiêu đến nay",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                            Row(
                                verticalAlignment = Alignment.Bottom,
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                modifier = Modifier.padding(top = 2.dp)
                            ) {
                                Text(
                                    text = MoneyParser.formatCurrency(stats.totalThisMonth, withSymbol = false),
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    color = Color.White
                                )
                                Text(
                                    text = "₫",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White.copy(alpha = 0.85f),
                                    modifier = Modifier.padding(bottom = 3.dp)
                                )
                            }
                        }

                        // Progress bar
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            LinearProgressIndicator(
                                progress = { (stats.budgetUsedPercent / 100f).coerceIn(0f, 1f) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(CircleShape),
                                color = SecondaryEmerald,
                                trackColor = Color.Black.copy(alpha = 0.25f)
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Ngân sách: ${MoneyParser.formatCurrency(stats.budgetMonth)}",
                                    fontSize = 12.sp,
                                    color = Color.White.copy(alpha = 0.85f),
                                    fontFamily = FontFamily.Monospace
                                )
                                Text(
                                    text = "Còn lại: ${MoneyParser.formatCurrency(stats.remainingBudget)}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }

                        // AI Insight Chip
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color.White.copy(alpha = 0.12f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.15f))
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lightbulb,
                                    contentDescription = null,
                                    tint = WarningAmber,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "Bạn còn trung bình ~250.000 ₫/ngày trong 15 ngày tới.",
                                    fontSize = 12.sp,
                                    color = Color.White,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }

            // Quick Stats Row (3 Cards)
            item {
                val stats = uiState.statistics
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Card 1: Hôm nay
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = Color.White,
                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderOutline),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Box(modifier = Modifier.size(8.dp).background(SecondaryEmerald, CircleShape))
                                Text("HÔM NAY", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextSlate500)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = MoneyParser.formatCurrency(stats.totalToday),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                color = TextSlate900
                            )
                            Text("+15k vs h.qua", fontSize = 10.sp, color = SecondaryEmerald, fontWeight = FontWeight.SemiBold)
                        }
                    }

                    // Card 2: Tuần này
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = Color.White,
                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderOutline),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Box(modifier = Modifier.size(8.dp).background(PrimaryBlue, CircleShape))
                                Text("TUẦN NÀY", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextSlate500)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = MoneyParser.formatCurrency(stats.totalThisWeek),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                color = TextSlate900
                            )
                            Text("Đạt 82% mục tiêu", fontSize = 10.sp, color = PrimaryBlue, fontWeight = FontWeight.SemiBold)
                        }
                    }

                    // Card 3: Tháng này
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = Color.White,
                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderOutline),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Box(modifier = Modifier.size(8.dp).background(TextSlate500, CircleShape))
                                Text("THÁNG NÀY", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextSlate500)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = MoneyParser.formatCurrency(stats.totalThisMonth),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                color = TextSlate900
                            )
                            Text("${uiState.recentExpenses.size} giao dịch", fontSize = 10.sp, color = TextSlate500, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }

            // Quick Actions Row (4 buttons)
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Thao tác nhanh", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextSlate900)
                        Text("Tùy biến", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = PrimaryBlue)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Action 1: Thêm tiền
                        QuickActionButton(
                            icon = Icons.Default.Add,
                            label = "Thêm tiền",
                            isPrimary = true,
                            onClick = { showAddDialog = true },
                            modifier = Modifier.weight(1f)
                        )

                        // Action 2: Quét VietQR
                        QuickActionButton(
                            icon = Icons.Default.QrCodeScanner,
                            label = "Quét VietQR",
                            iconTint = PrimaryBlue,
                            onClick = onNavigateToScanner,
                            modifier = Modifier.weight(1f)
                        )

                        // Action 3: Chat AI
                        QuickActionButton(
                            icon = Icons.Default.SmartToy,
                            label = "Chat AI",
                            iconTint = Color(0xFF6366F1),
                            onClick = onNavigateToChat,
                            modifier = Modifier.weight(1f)
                        )

                        // Action 4: Thống kê
                        QuickActionButton(
                            icon = Icons.Default.Insights,
                            label = "Thống kê",
                            iconTint = TextSlate700,
                            onClick = onNavigateToStats,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Natural Language Smart Input Pill
            item {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = Color.White,
                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderOutline),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = PrimaryBlueContainer,
                                modifier = Modifier.size(34.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        tint = PrimaryBlue,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            BasicTextField(
                                value = uiState.quickInputText,
                                onValueChange = { viewModel.onQuickInputChange(it) },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                                keyboardActions = KeyboardActions(onDone = { viewModel.submitQuickInput() }),
                                modifier = Modifier.weight(1f).testTag("quick_expense_input"),
                                decorationBox = { innerTextField ->
                                    if (uiState.quickInputText.isEmpty()) {
                                        Text(
                                            "Nhập nhanh: ăn phở 50k, đổ xăng...",
                                            fontSize = 13.sp,
                                            color = TextSlate400
                                        )
                                    }
                                    innerTextField()
                                }
                            )

                            IconButton(
                                onClick = {
                                    viewModel.onQuickInputChange("ăn phở 50k")
                                },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Mic,
                                    contentDescription = "Giọng nói",
                                    tint = TextSlate500,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            Surface(
                                shape = CircleShape,
                                color = PrimaryBlue,
                                modifier = Modifier
                                    .size(32.dp)
                                    .clickable { viewModel.submitQuickInput() }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowUpward,
                                        contentDescription = "Gửi",
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Live Detection Badge
                    AnimatedVisibility(
                        visible = uiState.detectedIntent != null,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        uiState.detectedIntent?.let { intent ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = PrimaryBlueContainer.copy(alpha = 0.5f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryBlue.copy(alpha = 0.2f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint = PrimaryBlue,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Text(
                                            text = "Phát hiện: ${intent.category?.displayName ?: "Khác"}",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = PrimaryBlue
                                        )
                                    }

                                    intent.amount?.let { amt ->
                                        Text(
                                            text = MoneyParser.formatCurrency(amt),
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily.Monospace,
                                            color = TextSlate900
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Top Spending Categories (2x2 Grid)
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(imageVector = Icons.Default.PieChart, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(18.dp))
                            Text("Danh mục hàng đầu", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextSlate900)
                        }
                        Text("Chi tiết", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = PrimaryBlue, modifier = Modifier.clickable { onNavigateToStats() })
                    }

                    // 2x2 Category Cards
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        CategoryCard(
                            title = "Ăn uống",
                            amount = 2_625_000L,
                            percentage = 42,
                            color = WarningAmber,
                            icon = Icons.Outlined.Restaurant,
                            modifier = Modifier.weight(1f)
                        )
                        CategoryCard(
                            title = "Mua sắm",
                            amount = 1_500_000L,
                            percentage = 24,
                            color = Color(0xFF6366F1),
                            icon = Icons.Outlined.ShoppingBag,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        CategoryCard(
                            title = "Di chuyển",
                            amount = 1_000_000L,
                            percentage = 16,
                            color = PrimaryBlue,
                            icon = Icons.Outlined.DirectionsCar,
                            modifier = Modifier.weight(1f)
                        )
                        CategoryCard(
                            title = "Hóa đơn",
                            amount = 750_000L,
                            percentage = 12,
                            color = WarningAmber,
                            icon = Icons.Outlined.ReceiptLong,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Recent Transactions
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(imageVector = Icons.Outlined.History, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(18.dp))
                        Text("Giao dịch gần đây", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextSlate900)
                    }
                    Text("Xem tất cả", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = PrimaryBlue)
                }
            }

            items(uiState.recentExpenses.take(8)) { expense ->
                TransactionListItem(
                    expense = expense,
                    onToggleStatus = { viewModel.toggleExpenseStatus(expense) }
                )
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 76.dp)
        )
    }
}

@Composable
fun QuickActionButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isPrimary: Boolean = false,
    iconTint: Color = PrimaryBlue
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
        modifier = modifier.clickable { onClick() }
    ) {
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = if (isPrimary) PrimaryBlue else Color.White,
            border = if (isPrimary) null else androidx.compose.foundation.BorderStroke(1.dp, BorderOutline),
            modifier = Modifier.size(52.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = if (isPrimary) Color.White else iconTint,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (isPrimary) FontWeight.Bold else FontWeight.Medium,
            color = TextSlate900,
            maxLines = 1
        )
    }
}

@Composable
fun CategoryCard(
    title: String,
    amount: Long,
    percentage: Int,
    color: Color,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderOutline),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = color.copy(alpha = 0.12f),
                    modifier = Modifier.size(34.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
                    }
                }
                Text("$percentage%", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = color)
            }

            Column {
                Text(title, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextSlate900)
                Text(MoneyParser.formatCurrency(amount), fontSize = 12.sp, fontFamily = FontFamily.Monospace, color = TextSlate500)
            }

            LinearProgressIndicator(
                progress = { percentage / 100f },
                color = color,
                trackColor = SurfaceContainer,
                modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape)
            )
        }
    }
}

@Composable
fun TransactionListItem(
    expense: Expense,
    onToggleStatus: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderOutline),
        modifier = Modifier.fillMaxWidth().clickable { onToggleStatus() }
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                val icon = when (expense.category) {
                    ExpenseCategory.FOOD -> Icons.Outlined.RamenDining
                    ExpenseCategory.TRANSPORT -> Icons.Outlined.DirectionsCar
                    ExpenseCategory.SHOPPING -> Icons.Outlined.ShoppingBag
                    ExpenseCategory.EDUCATION -> Icons.Outlined.MenuBook
                    else -> Icons.Outlined.ReceiptLong
                }
                val iconColor = Color(expense.category.colorHex)

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = iconColor.copy(alpha = 0.12f),
                    modifier = Modifier.size(42.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(imageVector = icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(20.dp))
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = expense.title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextSlate900,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(expense.category.displayName, fontSize = 11.sp, color = TextSlate500)
                        Text("•", fontSize = 11.sp, color = TextSlate400)
                        Text(
                            expense.bankCode ?: expense.paymentMethod ?: "Tiền mặt",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = PrimaryBlue
                        )
                    }
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "-${MoneyParser.formatCurrency(expense.amount)}",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = TextSlate900
                )
                Spacer(modifier = Modifier.height(4.dp))
                val isPaid = expense.status == ExpenseStatus.PAID
                Surface(
                    shape = CircleShape,
                    color = if (isPaid) SecondaryEmeraldContainer else WarningAmberContainer,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isPaid) SecondaryEmerald.copy(alpha = 0.4f) else WarningAmber.copy(alpha = 0.4f)
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(5.dp)
                                .background(if (isPaid) SecondaryEmerald else WarningAmber, CircleShape)
                        )
                        Text(
                            text = if (isPaid) "PAID" else "PENDING",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isPaid) Color(0xFF065F46) else Color(0xFF92400E)
                        )
                    }
                }
            }
        }
    }
}
