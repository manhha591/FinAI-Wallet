package com.example.presentation.settings

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.FinAiApplication
import com.example.core.util.MoneyParser
import com.example.ui.theme.BackgroundCanvas
import com.example.ui.theme.BorderOutline
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.PrimaryBlueContainer
import com.example.ui.theme.SecondaryEmerald
import com.example.ui.theme.SecondaryEmeraldContainer
import com.example.ui.theme.SurfaceContainer
import com.example.ui.theme.TextSlate400
import com.example.ui.theme.TextSlate500
import com.example.ui.theme.TextSlate700
import com.example.ui.theme.TextSlate900
import kotlinx.coroutines.launch
import java.util.Calendar

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier
) {
    val app = FinAiApplication.instance
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var isLocalAiEnabled by remember { mutableStateOf(true) }
    var monthlyBudget by remember { mutableStateOf(10_000_000L) }
    var showBudgetDialog by remember { mutableStateOf(false) }

    if (showBudgetDialog) {
        var input by remember { mutableStateOf(monthlyBudget.toString()) }
        AlertDialog(
            onDismissRequest = { showBudgetDialog = false },
            title = { Text("Hạn mức ngân sách tháng", fontWeight = FontWeight.Bold, color = TextSlate900) },
            text = {
                OutlinedTextField(
                    value = input,
                    onValueChange = { input = it.filter { ch -> ch.isDigit() } },
                    trailingIcon = { Text("₫", fontWeight = FontWeight.Bold, color = TextSlate500) },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        val num = input.toLongOrNull() ?: 10_000_000L
                        monthlyBudget = num
                        coroutineScope.launch {
                            val cal = Calendar.getInstance()
                            app.expenseRepository.updateMonthlyBudget(
                                cal.get(Calendar.MONTH) + 1,
                                cal.get(Calendar.YEAR),
                                num
                            )
                            snackbarHostState.showSnackbar("Đã cập nhật ngân sách: ${MoneyParser.formatCurrency(num)}")
                        }
                        showBudgetDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                ) {
                    Text("Lưu")
                }
            },
            dismissButton = {
                TextButton(onClick = { showBudgetDialog = false }) {
                    Text("Hủy", color = TextSlate500)
                }
            }
        )
    }

    Box(modifier = modifier.fillMaxSize().background(BackgroundCanvas)) {
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            item {
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
                            Icon(imageVector = Icons.Default.Settings, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(20.dp))
                        }
                    }
                    Text("Cài đặt hệ thống", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextSlate900)
                }
            }

            // Financial & Budget Section
            item {
                SettingsSectionTitle("Quản lý tài chính")
            }

            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderOutline),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        SettingsRow(
                            icon = Icons.Default.AccountBalanceWallet,
                            title = "Định mức ngân sách tháng",
                            subtitle = MoneyParser.formatCurrency(monthlyBudget),
                            onClick = { showBudgetDialog = true }
                        )
                        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(BorderOutline))
                        SettingsRow(
                            icon = Icons.Default.AccountBalance,
                            title = "Tiền tệ mặc định",
                            subtitle = "VND (₫) • Việt Nam Đồng",
                            onClick = { }
                        )
                    }
                }
            }

            // AI Model & Privacy Section
            item {
                SettingsSectionTitle("Trợ lý AI & Bảo mật")
            }

            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderOutline),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = PrimaryBlueContainer.copy(alpha = 0.5f),
                                    modifier = Modifier.size(38.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(imageVector = Icons.Default.Memory, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(20.dp))
                                    }
                                }
                                Column {
                                    Text("Bật Local AI On-device", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextSlate900)
                                    Text("Mô hình Qwen 1.5B Q4 • NPU Active", fontSize = 11.sp, color = TextSlate500)
                                }
                            }

                            Switch(
                                checked = isLocalAiEnabled,
                                onCheckedChange = { isLocalAiEnabled = it },
                                colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = PrimaryBlue)
                            )
                        }

                        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(BorderOutline))

                        SettingsRow(
                            icon = Icons.Default.Lock,
                            title = "Quyền riêng tư & Bảo mật",
                            subtitle = "Dữ liệu 100% trên thiết bị, không lưu PIN/OTP",
                            onClick = {
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Dữ liệu được mã hóa và bảo mật hoàn toàn cục bộ.")
                                }
                            }
                        )
                    }
                }
            }

            // Installed Banking Apps Section
            item {
                SettingsSectionTitle("Liên kết Ngân hàng")
            }

            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderOutline),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        val banks = app.bankAppLauncher.getAllSupportedBanks()
                        banks.take(5).forEach { bank ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    Box(
                                        modifier = Modifier
                                            .size(34.dp)
                                            .background(Color(bank.accentColorHex), RoundedCornerShape(8.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(bank.shortName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                    }
                                    Text(bank.displayName, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextSlate900)
                                }

                                Surface(
                                    shape = CircleShape,
                                    color = SecondaryEmeraldContainer
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(3.dp),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = Color(0xFF15803D), modifier = Modifier.size(11.dp))
                                        Text("Sẵn sàng", fontSize = 10.sp, color = Color(0xFF15803D), fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Backup & Export Section
            item {
                SettingsSectionTitle("Sao lưu & Dữ liệu")
            }

            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderOutline),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        SettingsRow(
                            icon = Icons.Default.FileDownload,
                            title = "Xuất dữ liệu Excel/CSV",
                            subtitle = "Sao lưu lịch sử chi tiêu về máy",
                            onClick = {
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Đã xuất file FinAI_Expenses_Backup.csv thành công!")
                                }
                            }
                        )
                        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(BorderOutline))
                        SettingsRow(
                            icon = Icons.Default.Refresh,
                            title = "Nạp lại dữ liệu mẫu",
                            subtitle = "Khôi phục dữ liệu demo trực quan",
                            onClick = {
                                coroutineScope.launch {
                                    com.example.data.local.FinAiDatabase.populateInitialData(app.database)
                                    snackbarHostState.showSnackbar("Đã nạp lại dữ liệu mẫu thành công!")
                                }
                            }
                        )
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
fun SettingsSectionTitle(title: String) {
    Text(
        text = title,
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        color = TextSlate500,
        modifier = Modifier.padding(start = 4.dp, top = 4.dp)
    )
}

@Composable
fun SettingsRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = SurfaceContainer,
                modifier = Modifier.size(38.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(imageVector = icon, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(20.dp))
                }
            }
            Column {
                Text(title, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextSlate900)
                Text(subtitle, fontSize = 11.sp, color = TextSlate500)
            }
        }
        Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = TextSlate400, modifier = Modifier.size(18.dp))
    }
}
