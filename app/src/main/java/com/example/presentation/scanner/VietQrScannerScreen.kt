package com.example.presentation.scanner

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.util.MoneyParser
import com.example.domain.model.BankApp
import com.example.domain.model.ExpenseStatus
import com.example.ui.theme.BackgroundCanvas
import com.example.ui.theme.BorderOutline
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.PrimaryBlueContainer
import com.example.ui.theme.SecondaryEmerald
import com.example.ui.theme.SurfaceContainer
import com.example.ui.theme.TextSlate400
import com.example.ui.theme.TextSlate500
import com.example.ui.theme.TextSlate700
import com.example.ui.theme.TextSlate900
import com.example.ui.theme.WarningAmber
import com.example.ui.theme.WarningAmberContainer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VietQrScannerScreen(
    viewModel: ScannerViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(uiState.toastMessage) {
        uiState.toastMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearToast()
        }
    }

    // Amount Edit Dialog
    if (uiState.showEditAmountDialog) {
        var editInput by remember {
            mutableStateOf(uiState.paymentInfo.amount?.toString() ?: "150000")
        }
        AlertDialog(
            onDismissRequest = { viewModel.showEditAmount(false) },
            title = { Text("Chỉnh sửa số tiền (VNĐ)", fontWeight = FontWeight.Bold, color = TextSlate900) },
            text = {
                OutlinedTextField(
                    value = editInput,
                    onValueChange = { editInput = it.filter { ch -> ch.isDigit() } },
                    modifier = Modifier.fillMaxWidth().testTag("edit_amount_input"),
                    trailingIcon = { Text("₫", fontWeight = FontWeight.Bold, color = TextSlate500) }
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        val num = editInput.toLongOrNull() ?: 150_000L
                        viewModel.updateAmount(num)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                ) {
                    Text("Cập nhật")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.showEditAmount(false) }) {
                    Text("Hủy", color = TextSlate500)
                }
            }
        )
    }

    // Payment Confirmation Bottom Sheet
    if (uiState.showConfirmSheet) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.resolvePayment(ExpenseStatus.PENDING) },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = Color.White,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = PrimaryBlueContainer.copy(alpha = 0.5f),
                        modifier = Modifier.size(46.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(imageVector = Icons.Default.Security, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(24.dp))
                        }
                    }
                    Column {
                        Text("Xác nhận thanh toán", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = TextSlate900)
                        Text("Bảo mật thông tin tài chính người dùng", fontSize = 12.sp, color = TextSlate500)
                    }
                }

                Text(
                    text = "Bạn đã hoàn tất chuyển tiền ${MoneyParser.formatCurrency(uiState.paymentInfo.amount ?: 150_000L)} cho ${uiState.paymentInfo.receiverName ?: "NGUYEN VAN A"} qua ứng dụng ${uiState.selectedBank?.displayName ?: "MB Bank"} chưa?",
                    fontSize = 14.sp,
                    color = TextSlate700,
                    lineHeight = 20.sp
                )

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        onClick = { viewModel.resolvePayment(ExpenseStatus.PAID) },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(imageVector = Icons.Default.TaskAlt, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                            Text("Đã thanh toán thành công (PAID)", fontWeight = FontWeight.Bold)
                        }
                    }

                    OutlinedButton(
                        onClick = { viewModel.resolvePayment(ExpenseStatus.PENDING) },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(imageVector = Icons.Default.Schedule, contentDescription = null, tint = TextSlate700, modifier = Modifier.size(18.dp))
                            Text("Chưa thanh toán (PENDING)", color = TextSlate700, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    Box(modifier = modifier.fillMaxSize().background(BackgroundCanvas)) {
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Top Bar
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = PrimaryBlueContainer.copy(alpha = 0.5f),
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(imageVector = Icons.Default.QrCodeScanner, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(20.dp))
                            }
                        }
                        Text("Quét Vietqr", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextSlate900)
                    }

                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        IconButton(onClick = { /* Notifications */ }) {
                            Icon(imageVector = Icons.Default.Notifications, contentDescription = "Thông báo", tint = TextSlate500, modifier = Modifier.size(22.dp))
                        }
                        Surface(shape = CircleShape, color = PrimaryBlue, modifier = Modifier.size(36.dp)) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("NV", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        }
                    }
                }
            }

            // Top Viewfinder Zone (Matching Image 4)
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color(0xFF0F172A))
                        .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(24.dp))
                        .padding(16.dp)
                ) {
                    // Header Overlay
                    Row(
                        modifier = Modifier.fillMaxWidth().align(Alignment.TopCenter),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFF0B132B).copy(alpha = 0.85f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.15f))
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Box(modifier = Modifier.size(7.dp).background(SecondaryEmerald, CircleShape))
                                Text("CameraX • EMVCo & VietQR", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Medium)
                            }
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Surface(
                                shape = CircleShape,
                                color = Color(0xFF0B132B).copy(alpha = 0.85f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.15f)),
                                modifier = Modifier.size(36.dp).clickable { viewModel.toggleFlash() }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = if (uiState.isFlashOn) Icons.Default.FlashOn else Icons.Default.FlashOff,
                                        contentDescription = "Flash",
                                        tint = if (uiState.isFlashOn) WarningAmber else Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }

                            Surface(
                                shape = CircleShape,
                                color = Color(0xFF0B132B).copy(alpha = 0.85f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.15f)),
                                modifier = Modifier.size(36.dp).clickable {
                                    // Simulate Gallery VietQR decode
                                    viewModel.onScanQrCode("00020101021238540010A0000007270124000697042202100123456789530370454061500005802VN5912NGUYEN VAN A62160812AN TOI FINAI6304ABCD")
                                }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(imageVector = Icons.Default.PhotoLibrary, contentDescription = "Thư viện", tint = Color.White, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }

                    // Scanner Center Reticle
                    Box(
                        modifier = Modifier
                            .size(190.dp)
                            .align(Alignment.Center),
                        contentAlignment = Alignment.Center
                    ) {
                        // Corner brackets
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .size(28.dp)
                                .border(width = 3.5.dp, color = PrimaryBlue, shape = RoundedCornerShape(topStart = 8.dp))
                        )
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .size(28.dp)
                                .border(width = 3.5.dp, color = PrimaryBlue, shape = RoundedCornerShape(topEnd = 8.dp))
                        )
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .size(28.dp)
                                .border(width = 3.5.dp, color = PrimaryBlue, shape = RoundedCornerShape(bottomStart = 8.dp))
                        )
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .size(28.dp)
                                .border(width = 3.5.dp, color = PrimaryBlue, shape = RoundedCornerShape(bottomEnd = 8.dp))
                        )

                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color(0xFF0F172A).copy(alpha = 0.75f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.15f))
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(imageVector = Icons.Default.QrCodeScanner, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(28.dp))
                                Text("Hướng camera vào mã VietQR", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Medium)
                            }
                        }
                    }

                    // Bottom validation tag
                    Surface(
                        shape = CircleShape,
                        color = PrimaryBlue,
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.3f)),
                        modifier = Modifier.align(Alignment.BottomCenter)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(imageVector = Icons.Default.VerifiedUser, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                            Text("Phát hiện mã hợp lệ NAPAS247 / VietQR", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }

            // Payment Preview Card (Matching Image 4)
            item {
                val payment = uiState.paymentInfo
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderOutline),
                    modifier = Modifier.fillMaxWidth().testTag("payment_preview_card")
                ) {
                    Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        // Header Status & Security
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = WarningAmberContainer,
                                border = androidx.compose.foundation.BorderStroke(1.dp, WarningAmber.copy(alpha = 0.3f))
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Box(modifier = Modifier.size(6.dp).background(WarningAmber, CircleShape))
                                    Text("PENDING • Chờ chuyển tiền", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF92400E))
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Icon(imageVector = Icons.Default.Shield, contentDescription = null, tint = TextSlate400, modifier = Modifier.size(14.dp))
                                Text("Bảo mật EMVCo", fontSize = 11.sp, color = TextSlate500, fontWeight = FontWeight.Medium)
                            }
                        }

                        // Recipient Detail Bento Tile
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color(0xFFF8FAFC),
                            border = androidx.compose.foundation.BorderStroke(1.dp, BorderOutline),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = PrimaryBlue,
                                        modifier = Modifier.size(46.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text("MB", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                        }
                                    }
                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                            Text(payment.receiverName ?: "NGUYEN VAN A", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextSlate900)
                                            Icon(imageVector = Icons.Default.Verified, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(16.dp))
                                        }
                                        Text("MB Bank • CN Thăng Long Hà Nội", fontSize = 12.sp, color = TextSlate500)
                                    }
                                }

                                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(BorderOutline))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text("Số tài khoản thụ hưởng", fontSize = 11.sp, color = TextSlate400, fontWeight = FontWeight.Medium)
                                        Text(
                                            text = payment.accountNumber ?: "0123456789",
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily.Monospace,
                                            color = TextSlate900
                                        )
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = PrimaryBlueContainer.copy(alpha = 0.4f),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryBlue.copy(alpha = 0.2f)),
                                        modifier = Modifier.clickable {
                                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                                            clipboard?.setPrimaryClip(ClipData.newPlainText("STK", payment.accountNumber ?: "0123456789"))
                                            viewModel.copyToClipboard(payment.accountNumber ?: "", "Đã sao chép Số tài khoản MB Bank!")
                                        }
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                        ) {
                                            Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(14.dp))
                                            Text("Sao chép", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = PrimaryBlue)
                                        }
                                    }
                                }
                            }
                        }

                        // Amount Row
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color(0xFFF8FAFC),
                            border = androidx.compose.foundation.BorderStroke(1.dp, BorderOutline),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("Số tiền cần chuyển", fontSize = 11.sp, color = TextSlate400, fontWeight = FontWeight.Medium)
                                    Row(
                                        verticalAlignment = Alignment.Bottom,
                                        horizontalArrangement = Arrangement.spacedBy(3.dp),
                                        modifier = Modifier.padding(top = 2.dp)
                                    ) {
                                        Text(
                                            text = MoneyParser.formatCurrency(payment.amount ?: 150_000L, withSymbol = false),
                                            fontSize = 26.sp,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily.Monospace,
                                            color = TextSlate900
                                        )
                                        Text("₫", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextSlate900, modifier = Modifier.padding(bottom = 2.dp))
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = PrimaryBlueContainer.copy(alpha = 0.4f),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryBlue.copy(alpha = 0.2f)),
                                    modifier = Modifier.clickable { viewModel.showEditAmount(true) }
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.Edit, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(14.dp))
                                        Text("Sửa số tiền", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = PrimaryBlue)
                                    }
                                }
                            }
                        }

                        // Description Row
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color(0xFFF8FAFC),
                            border = androidx.compose.foundation.BorderStroke(1.dp, BorderOutline),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                                    Text("Nội dung chuyển khoản", fontSize = 11.sp, color = TextSlate400, fontWeight = FontWeight.Medium)
                                    Text(
                                        text = payment.content ?: "AN TOI FINAI",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace,
                                        color = TextSlate900,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = PrimaryBlueContainer.copy(alpha = 0.4f),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryBlue.copy(alpha = 0.2f)),
                                    modifier = Modifier.clickable {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                                        clipboard?.setPrimaryClip(ClipData.newPlainText("Nội dung", payment.content ?: "AN TOI FINAI"))
                                        viewModel.copyToClipboard(payment.content ?: "", "Đã sao chép Nội dung giao dịch!")
                                    }
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(14.dp))
                                        Text("Sao chép", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = PrimaryBlue)
                                    }
                                }
                            }
                        }

                        // Bank App Launcher Drawer (Matching Image 4)
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Mở ứng dụng Ngân hàng", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextSlate900)
                                Text("${uiState.installedBanks.size} ứng dụng sẵn có", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = PrimaryBlue)
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                uiState.installedBanks.take(5).forEach { bank ->
                                    val isSelected = uiState.selectedBank?.id == bank.id
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(16.dp))
                                            .background(if (isSelected) PrimaryBlueContainer.copy(alpha = 0.4f) else Color(0xFFF8FAFC))
                                            .border(
                                                if (isSelected) 2.dp else 1.dp,
                                                if (isSelected) PrimaryBlue else BorderOutline,
                                                RoundedCornerShape(16.dp)
                                            )
                                            .clickable { viewModel.selectBank(bank) }
                                            .padding(vertical = 8.dp, horizontal = 4.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(46.dp)
                                                .background(Color(bank.accentColorHex), RoundedCornerShape(12.dp)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(bank.shortName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                            if (bank.id == "MB") {
                                                Box(
                                                    modifier = Modifier
                                                        .size(14.dp)
                                                        .background(WarningAmber, CircleShape)
                                                        .align(Alignment.TopEnd)
                                                ) {
                                                    Text("★", fontSize = 9.sp, color = TextSlate900, modifier = Modifier.align(Alignment.Center))
                                                }
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = bank.displayName.split(" ").first(),
                                            fontSize = 11.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isSelected) PrimaryBlue else TextSlate700,
                                            maxLines = 1
                                        )
                                    }
                                }
                            }
                        }

                        // Primary CTA & Info text
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 8.dp)) {
                            val targetBankName = uiState.selectedBank?.displayName ?: "MB Bank"
                            Button(
                                onClick = { viewModel.launchBankingApp() },
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                                modifier = Modifier.fillMaxWidth().height(52.dp).testTag("launch_bank_button")
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Icon(imageVector = Icons.Default.OpenInNew, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                                    Text("Mở $targetBankName để chuyển tiền", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }

                            Row(
                                verticalAlignment = Alignment.Top,
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.padding(horizontal = 4.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Info, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(15.dp).padding(top = 2.dp))
                                Text(
                                    text = "Tự động sao chép số tài khoản & nội dung vào clipboard khi mở app. Không tự ý lưu trạng thái Đã thanh toán khi chưa có xác nhận từ bạn.",
                                    fontSize = 11.sp,
                                    color = TextSlate500,
                                    lineHeight = 16.sp
                                )
                            }
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
