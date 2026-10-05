package com.example.presentation.statistics

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.DirectionsCar
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material.icons.outlined.SportsEsports
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.util.MoneyParser
import com.example.domain.model.CategorySpending
import com.example.domain.model.ExpenseCategory
import com.example.domain.model.WeekSpending
import com.example.ui.theme.BackgroundCanvas
import com.example.ui.theme.BorderOutline
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.ErrorRedContainer
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.PrimaryBlueContainer
import com.example.ui.theme.SurfaceContainer
import com.example.ui.theme.TextSlate400
import com.example.ui.theme.TextSlate500
import com.example.ui.theme.TextSlate700
import com.example.ui.theme.TextSlate900
import com.example.ui.theme.WarningAmber

@Composable
fun StatisticsScreen(
    viewModel: StatisticsViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.infoMessage) {
        uiState.infoMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessage()
        }
    }

    Box(modifier = modifier.fillMaxSize().background(BackgroundCanvas)) {
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Top Bar (Matching Image 2)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = PrimaryBlueContainer.copy(alpha = 0.5f),
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.QueryStats,
                                    contentDescription = null,
                                    tint = PrimaryBlue,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Text(
                            text = "Thống kê",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSlate900
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        IconButton(onClick = { /* Notifications */ }) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Thông báo",
                                tint = TextSlate500,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Surface(
                            shape = CircleShape,
                            color = PrimaryBlue,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("NV", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        }
                    }
                }
            }

            // Month Selector & Time Filter Segmented Tabs
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CalendarToday,
                                contentDescription = null,
                                tint = PrimaryBlue,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "Tháng ${uiState.selectedMonth}, ${uiState.selectedYear}",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextSlate900
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color.White,
                                border = androidx.compose.foundation.BorderStroke(1.dp, BorderOutline),
                                modifier = Modifier.size(32.dp).clickable { viewModel.prevMonth() }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(imageVector = Icons.Default.ChevronLeft, contentDescription = "Tháng trước", tint = TextSlate700, modifier = Modifier.size(18.dp))
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color.White,
                                border = androidx.compose.foundation.BorderStroke(1.dp, BorderOutline),
                                modifier = Modifier.size(32.dp).clickable { viewModel.nextMonth() }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(imageVector = Icons.Default.ChevronRight, contentDescription = "Tháng sau", tint = TextSlate700, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }

                    // 4 Segment Tabs
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = SurfaceContainer,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(modifier = Modifier.padding(4.dp)) {
                            TimeTab.entries.forEach { tab ->
                                val isSelected = uiState.selectedTab == tab
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (isSelected) PrimaryBlue else Color.Transparent,
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { viewModel.selectTab(tab) }
                                ) {
                                    Text(
                                        text = tab.title,
                                        fontSize = 13.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) Color.White else TextSlate500,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(vertical = 7.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Hero Overview Card (Tổng chi tiêu tháng này: 6.250.000 ₫)
            item {
                val stats = uiState.statistics
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderOutline),
                    modifier = Modifier.fillMaxWidth().testTag("stats_hero_card")
                ) {
                    Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column {
                                Text("Tổng chi tiêu tháng này", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = TextSlate500)
                                Row(
                                    verticalAlignment = Alignment.Bottom,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    modifier = Modifier.padding(top = 2.dp)
                                ) {
                                    Text(
                                        text = MoneyParser.formatCurrency(stats.totalThisMonth, withSymbol = false),
                                        fontSize = 30.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace,
                                        color = TextSlate900
                                    )
                                    Text("₫", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextSlate500, modifier = Modifier.padding(bottom = 2.dp))
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = PrimaryBlueContainer.copy(alpha = 0.5f),
                                modifier = Modifier.size(44.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.AccountBalanceWallet,
                                        contentDescription = null,
                                        tint = PrimaryBlue,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                        }

                        // Divider & Details
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFF8FAFC), RoundedCornerShape(12.dp))
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Surface(
                                        shape = CircleShape,
                                        color = ErrorRedContainer,
                                        border = androidx.compose.foundation.BorderStroke(1.dp, ErrorRed.copy(alpha = 0.3f))
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(3.dp),
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Icon(imageVector = Icons.Default.TrendingUp, contentDescription = null, tint = ErrorRed, modifier = Modifier.size(12.dp))
                                            Text("+15.7%", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ErrorRed)
                                        }
                                    }
                                    Text("(Tăng 850.000 ₫ so với T8)", fontSize = 12.sp, color = TextSlate500)
                                }

                                Surface(
                                    shape = CircleShape,
                                    color = SurfaceContainer
                                ) {
                                    Text("So kỳ trước", fontSize = 10.sp, color = TextSlate500, fontWeight = FontWeight.Medium, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp))
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Icon(imageVector = Icons.Default.Speed, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(16.dp))
                                    Text("Tốc độ trung bình:", fontSize = 13.sp, color = TextSlate500)
                                }
                                Text("390.000 ₫/ngày", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextSlate900)
                            }
                        }
                    }
                }
            }

            // 4-Week Progress Bar Chart Card (Matching Image 2)
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderOutline),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = PrimaryBlueContainer.copy(alpha = 0.5f),
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(imageVector = Icons.Default.BarChart, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(18.dp))
                                    }
                                }
                                Text("Tiến độ chi tiêu 4 tuần", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextSlate900)
                            }
                            Text("Đơn vị: Triệu ₫", fontSize = 12.sp, color = TextSlate400, fontWeight = FontWeight.Medium)
                        }

                        // Bar Chart Visualization
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .padding(top = 12.dp, bottom = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            val weeks = uiState.statistics.weeklyBreakdown.ifEmpty {
                                listOf(
                                    WeekSpending(1, "T1", "1-7", 1_200_000L),
                                    WeekSpending(2, "T2", "8-14", 1_800_000L),
                                    WeekSpending(3, "T3", "15-21", 1_900_000L, isExceededWarning = true),
                                    WeekSpending(4, "T4", "22-30", 1_350_000L)
                                )
                            }

                            weeks.forEach { week ->
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    // Amount Label
                                    val amountStr = String.format("%.2fM", week.amount / 1_000_000.0)
                                    if (week.isExceededWarning) {
                                        Surface(
                                            shape = CircleShape,
                                            color = WarningAmber.copy(alpha = 0.12f),
                                            border = androidx.compose.foundation.BorderStroke(1.dp, WarningAmber.copy(alpha = 0.3f)),
                                            modifier = Modifier.padding(bottom = 6.dp)
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(2.dp),
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                            ) {
                                                Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = WarningAmber, modifier = Modifier.size(10.dp))
                                                Text(amountStr, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = WarningAmber)
                                            }
                                        }
                                    } else {
                                        Text(
                                            text = amountStr,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = TextSlate500,
                                            modifier = Modifier.padding(bottom = 6.dp)
                                        )
                                    }

                                    // Bar Column with max height fraction
                                    val heightFraction = (week.amount / 2_200_000f).coerceIn(0.2f, 1f)
                                    Box(
                                        modifier = Modifier
                                            .width(48.dp)
                                            .height(84.dp)
                                            .background(SurfaceContainer, RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp)),
                                        contentAlignment = Alignment.BottomCenter
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .fillMaxHeight(heightFraction)
                                                .background(
                                                    if (week.isExceededWarning) WarningAmber else PrimaryBlue,
                                                    RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp)
                                                )
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))

                                    // Week Label
                                    Text(
                                        text = "${week.label} (${week.dateRange})",
                                        fontSize = 11.sp,
                                        fontWeight = if (week.isExceededWarning) FontWeight.Bold else FontWeight.Medium,
                                        color = if (week.isExceededWarning) WarningAmber else TextSlate700
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Donut Chart & Category Structure (Matching Image 2)
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderOutline),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = PrimaryBlueContainer.copy(alpha = 0.5f),
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(imageVector = Icons.Default.PieChart, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(18.dp))
                                    }
                                }
                                Text("Cơ cấu danh mục chi", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextSlate900)
                            }

                            val count = uiState.statistics.categoryBreakdown.size.coerceAtLeast(6)
                            Surface(shape = CircleShape, color = SurfaceContainer) {
                                Text("$count nhóm chi", fontSize = 11.sp, color = TextSlate700, fontWeight = FontWeight.Medium, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp))
                            }
                        }

                        // Donut Chart Canvas (Matching 33.6% Eat & Drink Center)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            val categories = uiState.statistics.categoryBreakdown.ifEmpty {
                                listOf(
                                    CategorySpending(ExpenseCategory.FOOD, 2_100_000L, 33.6f, 5),
                                    CategorySpending(ExpenseCategory.SHOPPING, 1_500_000L, 24.0f, 3),
                                    CategorySpending(ExpenseCategory.BILLS, 950_000L, 15.2f, 2),
                                    CategorySpending(ExpenseCategory.TRANSPORT, 850_000L, 13.6f, 4),
                                    CategorySpending(ExpenseCategory.ENTERTAINMENT, 500_000L, 8.0f, 1),
                                    CategorySpending(ExpenseCategory.OTHER, 350_000L, 5.6f, 1)
                                )
                            }

                            val sliceColors = listOf(
                                PrimaryBlue,
                                Color(0xFF3B82F6),
                                Color(0xFF60A5FA),
                                Color(0xFF93C5FD),
                                Color(0xFFBFDBFE),
                                Color(0xFFCBD5E1)
                            )

                            Canvas(modifier = Modifier.size(160.dp)) {
                                var startAngle = -90f
                                categories.take(6).forEachIndexed { index, cat ->
                                    val sweep = (cat.percentage / 100f) * 360f
                                    drawArc(
                                        color = sliceColors.getOrElse(index) { Color.LightGray },
                                        startAngle = startAngle,
                                        sweepAngle = sweep - 2f,
                                        useCenter = false,
                                        style = Stroke(width = 24.dp.toPx(), cap = StrokeCap.Round)
                                    )
                                    startAngle += sweep
                                }
                            }

                            // Center Info
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Lớn nhất", fontSize = 10.sp, color = TextSlate400, fontWeight = FontWeight.Medium)
                                Text("Ăn uống", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                                Text("33.6%", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextSlate700)
                            }
                        }

                        // 6 Categories Breakdown List
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            val items = uiState.statistics.categoryBreakdown.ifEmpty {
                                listOf(
                                    CategorySpending(ExpenseCategory.FOOD, 2_100_000L, 33.6f, 5),
                                    CategorySpending(ExpenseCategory.SHOPPING, 1_500_000L, 24.0f, 3),
                                    CategorySpending(ExpenseCategory.BILLS, 950_000L, 15.2f, 2),
                                    CategorySpending(ExpenseCategory.TRANSPORT, 850_000L, 13.6f, 4),
                                    CategorySpending(ExpenseCategory.ENTERTAINMENT, 500_000L, 8.0f, 1),
                                    CategorySpending(ExpenseCategory.OTHER, 350_000L, 5.6f, 1)
                                )
                            }

                            items.take(6).forEach { catStat ->
                                CategoryBreakdownRow(catStat)
                            }
                        }
                    }
                }
            }

            // Budget Limit & AI Forecast Box (Matching Image 2)
            item {
                val stats = uiState.statistics
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderOutline),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = PrimaryBlueContainer.copy(alpha = 0.5f),
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(imageVector = Icons.Default.AccountBalance, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(18.dp))
                                    }
                                }
                                Text("Hạn mức ngân sách", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextSlate900)
                            }

                            Surface(
                                shape = CircleShape,
                                color = PrimaryBlueContainer.copy(alpha = 0.5f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryBlue.copy(alpha = 0.2f))
                            ) {
                                Text("Đã dùng 62.5%", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = PrimaryBlue, modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp))
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Column {
                                Text("Đã chi / Định mức", fontSize = 12.sp, color = TextSlate500)
                                Row(
                                    verticalAlignment = Alignment.Bottom,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    modifier = Modifier.padding(top = 2.dp)
                                ) {
                                    Text(MoneyParser.formatCurrency(stats.totalThisMonth), fontSize = 17.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                                    Text("/ ${MoneyParser.formatCurrency(stats.budgetMonth)}", fontSize = 13.sp, color = TextSlate400, fontWeight = FontWeight.Medium)
                                }
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text("Còn lại", fontSize = 12.sp, color = TextSlate500)
                                Text(MoneyParser.formatCurrency(stats.remainingBudget), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextSlate900)
                            }
                        }

                        LinearProgressIndicator(
                            progress = { 0.625f },
                            color = PrimaryBlue,
                            trackColor = SurfaceContainer,
                            modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape)
                        )

                        // AI Forecast Box
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = PrimaryBlueContainer.copy(alpha = 0.35f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryBlue.copy(alpha = 0.2f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = PrimaryBlue,
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                    }
                                }

                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text("Dự báo AI On-device", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = Color.White,
                                            border = androidx.compose.foundation.BorderStroke(0.5.dp, PrimaryBlue.copy(alpha = 0.3f))
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(2.dp),
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                            ) {
                                                Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(10.dp))
                                                Text("Bảo mật", fontSize = 9.sp, fontWeight = FontWeight.Medium, color = PrimaryBlue)
                                            }
                                        }
                                    }

                                    Text(
                                        text = "Tốc độ chi tiêu đang ổn định, dự kiến cuối tháng đạt khoảng 9.200.000 ₫ (nằm trong ngưỡng an toàn).",
                                        fontSize = 12.sp,
                                        color = TextSlate700,
                                        lineHeight = 18.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Action Buttons: Xuất Excel/CSV & Chia sẻ báo cáo
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = { viewModel.exportReport("Excel/CSV") },
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryBlue.copy(alpha = 0.4f)),
                        modifier = Modifier.weight(1f).height(50.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(imageVector = Icons.Default.FileDownload, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(18.dp))
                            Text("Xuất Excel/CSV", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                        }
                    }

                    Button(
                        onClick = { viewModel.shareReport() },
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                        modifier = Modifier.weight(1f).height(50.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(imageVector = Icons.Default.Share, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                            Text("Chia sẻ báo cáo", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 76.dp)
        )
    }
}

@Composable
fun CategoryBreakdownRow(catStat: CategorySpending) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = Color(0xFFF8FAFC),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderOutline),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                val icon = when (catStat.category) {
                    ExpenseCategory.FOOD -> Icons.Outlined.Restaurant
                    ExpenseCategory.SHOPPING -> Icons.Outlined.ShoppingBag
                    ExpenseCategory.BILLS -> Icons.Outlined.ReceiptLong
                    ExpenseCategory.TRANSPORT -> Icons.Outlined.DirectionsCar
                    ExpenseCategory.ENTERTAINMENT -> Icons.Outlined.SportsEsports
                    else -> Icons.Outlined.MoreHoriz
                }
                val iconColor = Color(catStat.category.colorHex)

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = iconColor.copy(alpha = 0.12f),
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(imageVector = icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(18.dp))
                    }
                }

                Column {
                    Text(catStat.category.displayName, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextSlate900)
                    Text("${String.format("%.1f", catStat.percentage)}% tổng chi", fontSize = 11.sp, color = TextSlate500)
                }
            }

            Text(
                text = MoneyParser.formatCurrency(catStat.amount),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = TextSlate900
            )
        }
    }
}
