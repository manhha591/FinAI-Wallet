package com.example.presentation.chat

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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.TipsAndUpdates
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.util.MoneyParser
import com.example.domain.model.ExpenseCategory
import com.example.domain.model.FinancialIntent
import com.example.ui.theme.BackgroundCanvas
import com.example.ui.theme.BorderOutline
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.PrimaryBlueContainer
import com.example.ui.theme.SecondaryEmerald
import com.example.ui.theme.SecondaryEmeraldContainer
import com.example.ui.theme.SurfaceContainer
import com.example.ui.theme.TextSlate400
import com.example.ui.theme.TextSlate500
import com.example.ui.theme.TextSlate700
import com.example.ui.theme.TextSlate900
import com.example.ui.theme.WarningAmber
import com.example.ui.theme.WarningAmberContainer
import kotlinx.coroutines.launch

@Composable
fun AiChatScreen(
    viewModel: ChatViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.messages.size) {
        if (uiState.messages.isNotEmpty()) {
            coroutineScope.launch {
                listState.animateScrollToItem(uiState.messages.size + 1)
            }
        }
    }

    LaunchedEffect(uiState.toastMessage) {
        uiState.toastMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearToast()
        }
    }

    Box(modifier = modifier.fillMaxSize().background(BackgroundCanvas)) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header Top Bar (Matching Image 6)
            Surface(
                color = Color.White,
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderOutline),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
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
                                    imageVector = Icons.Default.SmartToy,
                                    contentDescription = null,
                                    tint = PrimaryBlue,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text("Trợ lý AI", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = TextSlate900)
                                Surface(
                                    shape = CircleShape,
                                    color = PrimaryBlueContainer.copy(alpha = 0.4f),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryBlue.copy(alpha = 0.2f))
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Box(modifier = Modifier.size(5.dp).background(PrimaryBlue, CircleShape))
                                        Text("Offline", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                                    }
                                }
                            }
                            Text("Mô hình AI tài chính cục bộ", fontSize = 11.sp, color = TextSlate500)
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Surface(
                            shape = CircleShape,
                            color = SurfaceContainer,
                            border = androidx.compose.foundation.BorderStroke(1.dp, BorderOutline)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Shield, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(13.dp))
                                Text("Riêng tư 100%", fontSize = 11.sp, color = TextSlate700, fontWeight = FontWeight.Medium)
                            }
                        }

                        Surface(
                            shape = CircleShape,
                            color = SurfaceContainer,
                            border = androidx.compose.foundation.BorderStroke(1.dp, BorderOutline),
                            modifier = Modifier.size(34.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = TextSlate500, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }

            // Message Stream Area
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(top = 12.dp, bottom = 12.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Banner Status & Privacy Callout (Matching Image 6)
                item {
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFDBEAFE)),
                        modifier = Modifier.fillMaxWidth().testTag("ai_privacy_banner")
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = PrimaryBlue,
                                        modifier = Modifier.size(38.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(imageVector = Icons.Default.Memory, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                                        }
                                    }

                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            Text("FinAI Local Assistant", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextSlate900)
                                            Surface(
                                                shape = RoundedCornerShape(4.dp),
                                                color = SecondaryEmeraldContainer
                                            ) {
                                                Text("NPU Active", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF15803D), modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp))
                                            }
                                        }
                                        Text("Offline 100% • Qwen 1.5B Q4 on-device", fontSize = 11.sp, color = TextSlate700)
                                    }
                                }

                                Surface(
                                    shape = CircleShape,
                                    color = Color.White,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBFDBFE))
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(3.dp),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.Verified, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(12.dp))
                                        Text("Local", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = PrimaryBlue)
                                    }
                                }
                            }

                            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color(0xFFDBEAFE)))

                            Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(15.dp).padding(top = 1.dp))
                                Text(
                                    text = "Tất cả dữ liệu chi tiêu được xử lý cục bộ trên thiết bị, bảo mật tuyệt đối. Không có dữ liệu nào gửi lên cloud.",
                                    fontSize = 11.sp,
                                    color = TextSlate700,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }
                }

                // Chat Messages List
                items(uiState.messages, key = { it.id }) { msg ->
                    if (msg.isUser) {
                        UserChatBubble(msg)
                    } else {
                        AiChatBubble(
                            msg = msg,
                            onCommitExpense = { intent -> viewModel.commitParsedExpense(intent) }
                        )
                    }
                }

                if (uiState.isGenerating) {
                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            Surface(shape = CircleShape, color = PrimaryBlue, modifier = Modifier.size(32.dp)) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(imageVector = Icons.Default.SmartToy, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                }
                            }
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = PrimaryBlue)
                            Text("FinAI đang phân tích dữ liệu SQLite cục bộ...", fontSize = 12.sp, color = TextSlate500)
                        }
                    }
                }
            }

            // Quick Query Suggestions Chips (Matching Image 6)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text("Gợi ý truy vấn nhanh:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextSlate500)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SuggestionChip(
                        icon = Icons.Default.CompareArrows,
                        iconTint = PrimaryBlue,
                        text = "So sánh tuần này & tuần trước",
                        onClick = { viewModel.sendMessage("So sánh tuần này và tuần trước") }
                    )
                    SuggestionChip(
                        icon = Icons.Default.PieChart,
                        iconTint = ErrorRed,
                        text = "Top chi tiêu",
                        onClick = { viewModel.sendMessage("Danh mục nào tôi tiêu nhiều nhất?") }
                    )
                    SuggestionChip(
                        icon = Icons.Default.AccountBalanceWallet,
                        iconTint = SecondaryEmerald,
                        text = "Ngân sách",
                        onClick = { viewModel.sendMessage("Tôi còn bao nhiêu ngân sách tháng này?") }
                    )
                }
            }

            // Bottom Input Dock (Matching Image 6)
            Surface(
                color = Color.White,
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderOutline),
                shadowElevation = 6.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .padding(bottom = 76.dp)
            ) {
                Column(modifier = Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = SurfaceContainer,
                            modifier = Modifier.size(36.dp).clickable {
                                viewModel.sendMessage("ăn trưa bún bò 55k")
                            }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(imageVector = Icons.Default.DocumentScanner, contentDescription = "OCR", tint = TextSlate500, modifier = Modifier.size(18.dp))
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = SurfaceContainer,
                            modifier = Modifier.size(36.dp).clickable {
                                viewModel.sendMessage("Tháng này tôi tiêu ăn uống bao nhiêu?")
                            }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(imageVector = Icons.Default.Mic, contentDescription = "Mic", tint = TextSlate500, modifier = Modifier.size(18.dp))
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFF8FAFC),
                            border = androidx.compose.foundation.BorderStroke(1.dp, BorderOutline),
                            modifier = Modifier.weight(1f)
                        ) {
                            BasicTextField(
                                value = uiState.inputText,
                                onValueChange = { viewModel.onInputTextChange(it) },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                                keyboardActions = KeyboardActions(onSend = { viewModel.sendMessage() }),
                                modifier = Modifier
                                    .padding(horizontal = 12.dp, vertical = 10.dp)
                                    .testTag("chat_input"),
                                decorationBox = { innerTextField ->
                                    if (uiState.inputText.isEmpty()) {
                                        Text("Hỏi chi tiêu hoặc nhập 'cafe 30k'...", fontSize = 13.sp, color = TextSlate400)
                                    }
                                    innerTextField()
                                }
                            )
                        }

                        Surface(
                            shape = CircleShape,
                            color = PrimaryBlue,
                            modifier = Modifier
                                .size(38.dp)
                                .clickable { viewModel.sendMessage() }
                                .testTag("chat_send_button")
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(imageVector = Icons.Default.ArrowUpward, contentDescription = "Gửi", tint = Color.White, modifier = Modifier.size(18.dp))
                            }
                        }
                    }

                    // Latency & Memory Stats Bar
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Box(modifier = Modifier.size(5.dp).background(SecondaryEmerald, CircleShape))
                            Text("0.2s latency • 0KB uploaded", fontSize = 10.sp, color = TextSlate400, fontFamily = FontFamily.Monospace)
                        }
                        Text("RAM: 412MB", fontSize = 10.sp, color = TextSlate400, fontFamily = FontFamily.Monospace)
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
fun SuggestionChip(
    icon: ImageVector,
    iconTint: Color,
    text: String,
    onClick: () -> Unit
) {
    Surface(
        shape = CircleShape,
        color = SurfaceContainer,
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderOutline),
        modifier = Modifier.clickable { onClick() }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(14.dp))
            Text(text, fontSize = 11.sp, fontWeight = FontWeight.Medium, color = TextSlate700)
        }
    }
}

@Composable
fun UserChatBubble(msg: ChatMessage) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 4.dp))
                .background(PrimaryBlue)
                .padding(12.dp)
                .fillMaxWidth(0.82f)
        ) {
            Text(text = msg.text, fontSize = 14.sp, color = Color.White, lineHeight = 20.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = msg.timestamp,
                fontSize = 10.sp,
                color = Color.White.copy(alpha = 0.75f),
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}

@Composable
fun AiChatBubble(
    msg: ChatMessage,
    onCommitExpense: (com.example.domain.model.ExpenseIntent) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Top
    ) {
        Surface(
            shape = CircleShape,
            color = PrimaryBlue,
            modifier = Modifier.size(32.dp).padding(top = 2.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(imageVector = Icons.Default.SmartToy, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
            }
        }

        Card(
            shape = RoundedCornerShape(topStart = 4.dp, topEnd = 18.dp, bottomStart = 18.dp, bottomEnd = 18.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderOutline),
            modifier = Modifier.fillMaxWidth(0.95f)
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // If it's a parsed expense transaction (Matching Turn 2)
                val parsed = msg.aiResult?.parsedExpense
                if (parsed != null && parsed.amount != null) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(16.dp))
                        Text("Đã nhận diện giao dịch (Rule Parser + Local AI)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                    }

                    // Parsed Details Box
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFF8FAFC),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderOutline),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Số tiền:", fontSize = 12.sp, color = TextSlate500)
                                Text("-${MoneyParser.formatCurrency(parsed.amount)}", fontSize = 16.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, color = ErrorRed)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Tiêu đề:", fontSize = 12.sp, color = TextSlate500)
                                Text(parsed.title ?: "Khoản chi", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextSlate900)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Text("Danh mục:", fontSize = 12.sp, color = TextSlate500)
                                Surface(
                                    shape = CircleShape,
                                    color = WarningAmberContainer
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Icon(imageVector = Icons.Outlined.Restaurant, contentDescription = null, tint = Color(0xFF92400E), modifier = Modifier.size(12.dp))
                                        Text(parsed.category?.displayName ?: "Ăn uống", fontSize = 11.sp, fontWeight = FontWeight.Medium, color = Color(0xFF92400E))
                                    }
                                }
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Thời gian:", fontSize = 12.sp, color = TextSlate500)
                                Text("Hôm nay (12:45)", fontSize = 12.sp, color = TextSlate700)
                            }
                        }
                    }

                    // Action Buttons: Lưu vào Database & Chỉnh sửa
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        Button(
                            onClick = { onCommitExpense(parsed) },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                            modifier = Modifier.weight(1.5f).height(40.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Icon(imageVector = Icons.Default.Save, contentDescription = null, tint = Color.White, modifier = Modifier.size(15.dp))
                                Text("Lưu vào Database", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        OutlinedButton(
                            onClick = { /* Edit */ },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f).height(40.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Icon(imageVector = Icons.Default.Edit, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(14.dp))
                                Text("Chỉnh sửa", fontSize = 12.sp, color = PrimaryBlue, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                } else if (msg.aiResult?.intent is FinancialIntent.GetBudgetStatus) {
                    // Turn 3: Budget Status Widget
                    Text(text = msg.text, fontSize = 14.sp, color = TextSlate900, lineHeight = 20.sp)

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFF8FAFC),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderOutline),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Đã chi: 62.5%", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = TextSlate500)
                                Surface(shape = CircleShape, color = SecondaryEmeraldContainer) {
                                    Text("An toàn", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF15803D), modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                }
                            }

                            LinearProgressIndicator(
                                progress = { 0.625f },
                                color = PrimaryBlue,
                                trackColor = SurfaceContainer,
                                modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape)
                            )

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(imageVector = Icons.Default.TipsAndUpdates, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(15.dp))
                                Text("Bạn đang giữ tốc độ chi tiêu tốt hơn mức trung bình 8%!", fontSize = 12.sp, color = PrimaryBlue, fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                } else {
                    // Turn 1 or Default: Analytics Widget (Matching Turn 1)
                    Text(text = msg.text, fontSize = 14.sp, color = TextSlate900, lineHeight = 20.sp)

                    // Mini 4-week bar chart widget
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFF8FAFC),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderOutline),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Xu hướng Ăn uống (4 tuần)", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = TextSlate500)
                                Surface(shape = CircleShape, color = SecondaryEmeraldContainer) {
                                    Text("-10.6%", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF15803D), modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                }
                            }

                            // 4 Mini Bars
                            Row(
                                modifier = Modifier.fillMaxWidth().height(52.dp).padding(horizontal = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Bottom
                            ) {
                                listOf(Pair("T1", 38.dp), Pair("T2", 48.dp), Pair("T3", 30.dp), Pair("T4", 42.dp)).forEach { (wk, barH) ->
                                    val isCurrent = wk == "T4"
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Box(
                                            modifier = Modifier
                                                .width(36.dp)
                                                .height(barH)
                                                .background(
                                                    if (isCurrent) PrimaryBlue else BorderOutline,
                                                    RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp)
                                                )
                                        )
                                        Spacer(modifier = Modifier.height(3.dp))
                                        Text(
                                            text = wk,
                                            fontSize = 10.sp,
                                            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isCurrent) PrimaryBlue else TextSlate500
                                        )
                                    }
                                }
                            }

                            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(BorderOutline))

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Đỉnh điểm: 680k", fontSize = 10.sp, fontFamily = FontFamily.Monospace, color = TextSlate500)
                                Text("Hiện tại: 490k", fontSize = 10.sp, fontFamily = FontFamily.Monospace, color = TextSlate500)
                            }
                        }
                    }
                }

                // Footer Metadata
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                        Icon(imageVector = Icons.Default.Bolt, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(13.dp))
                        Text("Độ trễ: ${msg.latencyLabel}", fontSize = 10.sp, color = TextSlate400)
                    }
                    Text(msg.timestamp, fontSize = 10.sp, color = TextSlate400, fontFamily = FontFamily.Monospace)
                }
            }
        }
    }
}
