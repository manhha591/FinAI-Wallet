package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.QueryStats
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.SmartToy
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.presentation.chat.AiChatScreen
import com.example.presentation.chat.ChatViewModel
import com.example.presentation.home.HomeScreen
import com.example.presentation.home.HomeViewModel
import com.example.presentation.scanner.ScannerViewModel
import com.example.presentation.scanner.VietQrScannerScreen
import com.example.presentation.settings.SettingsScreen
import com.example.presentation.statistics.StatisticsScreen
import com.example.presentation.statistics.StatisticsViewModel
import com.example.ui.theme.BorderOutline
import com.example.ui.theme.FinAiTheme
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.PrimaryBlueContainer
import com.example.ui.theme.TextSlate400
import com.example.ui.theme.TextSlate900

enum class NavigationTab(val label: String) {
    HOME("Trang chủ"),
    STATISTICS("Thống kê"),
    VIETQR("VietQR"),
    CHAT_AI("Trợ lý AI"),
    SETTINGS("Cài đặt")
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FinAiTheme {
                MainAppScreen()
            }
        }
    }
}

@Composable
fun MainAppScreen() {
    var currentTab by remember { mutableStateOf(NavigationTab.HOME) }

    // ViewModels with factory pattern
    val homeViewModel: HomeViewModel = viewModel(factory = HomeViewModel.Factory)
    val statisticsViewModel: StatisticsViewModel = viewModel(factory = StatisticsViewModel.Factory)
    val scannerViewModel: ScannerViewModel = viewModel(factory = ScannerViewModel.Factory)
    val chatViewModel: ChatViewModel = viewModel(factory = ChatViewModel.Factory)

    // Handle back press to return to HOME screen
    if (currentTab != NavigationTab.HOME) {
        BackHandler {
            currentTab = NavigationTab.HOME
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            FinAiBottomBar(
                currentTab = currentTab,
                onSelectTab = { currentTab = it }
            )
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                NavigationTab.HOME -> {
                    HomeScreen(
                        viewModel = homeViewModel,
                        onNavigateToScanner = { currentTab = NavigationTab.VIETQR },
                        onNavigateToChat = { currentTab = NavigationTab.CHAT_AI },
                        onNavigateToStats = { currentTab = NavigationTab.STATISTICS }
                    )
                }

                NavigationTab.STATISTICS -> {
                    StatisticsScreen(viewModel = statisticsViewModel)
                }

                NavigationTab.VIETQR -> {
                    VietQrScannerScreen(viewModel = scannerViewModel)
                }

                NavigationTab.CHAT_AI -> {
                    AiChatScreen(viewModel = chatViewModel)
                }

                NavigationTab.SETTINGS -> {
                    SettingsScreen()
                }
            }
        }
    }
}

@Composable
fun FinAiBottomBar(
    currentTab: NavigationTab,
    onSelectTab: (NavigationTab) -> Unit
) {
    Surface(
        color = Color.White.copy(alpha = 0.98f),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderOutline),
        shadowElevation = 8.dp,
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .testTag("bottom_nav_bar")
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 6.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Tab 1: Trang chủ
            BottomBarItem(
                label = "Trang chủ",
                selectedIcon = Icons.Filled.AccountBalanceWallet,
                unselectedIcon = Icons.Outlined.AccountBalanceWallet,
                isSelected = currentTab == NavigationTab.HOME,
                onClick = { onSelectTab(NavigationTab.HOME) },
                modifier = Modifier.weight(1f)
            )

            // Tab 2: Thống kê
            BottomBarItem(
                label = "Thống kê",
                selectedIcon = Icons.Filled.QueryStats,
                unselectedIcon = Icons.Outlined.QueryStats,
                isSelected = currentTab == NavigationTab.STATISTICS,
                onClick = { onSelectTab(NavigationTab.STATISTICS) },
                modifier = Modifier.weight(1f)
            )

            // Tab 3: Center VietQR Floating Action Button (Matching screenshots)
            Box(
                modifier = Modifier
                    .weight(1.1f)
                    .offset(y = (-10).dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable { onSelectTab(NavigationTab.VIETQR) }
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .shadow(8.dp, CircleShape)
                            .background(PrimaryBlue, CircleShape)
                            .border(2.dp, Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.QrCodeScanner,
                            contentDescription = "VietQR",
                            tint = Color.White,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                    Text(
                        text = "VietQR",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryBlue,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }

            // Tab 4: Trợ lý AI
            BottomBarItem(
                label = "Trợ lý AI",
                selectedIcon = Icons.Filled.SmartToy,
                unselectedIcon = Icons.Outlined.SmartToy,
                isSelected = currentTab == NavigationTab.CHAT_AI,
                onClick = { onSelectTab(NavigationTab.CHAT_AI) },
                modifier = Modifier.weight(1f)
            )

            // Tab 5: Cài đặt
            BottomBarItem(
                label = "Cài đặt",
                selectedIcon = Icons.Filled.Settings,
                unselectedIcon = Icons.Outlined.Settings,
                isSelected = currentTab == NavigationTab.SETTINGS,
                onClick = { onSelectTab(NavigationTab.SETTINGS) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun BottomBarItem(
    label: String,
    selectedIcon: ImageVector,
    unselectedIcon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .fillMaxHeight()
            .clickable { onClick() }
    ) {
        if (isSelected) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = PrimaryBlueContainer.copy(alpha = 0.5f),
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Icon(
                    imageVector = selectedIcon,
                    contentDescription = label,
                    tint = PrimaryBlue,
                    modifier = Modifier
                        .size(22.dp)
                        .padding(2.dp)
                )
            }
        } else {
            Icon(
                imageVector = unselectedIcon,
                contentDescription = label,
                tint = TextSlate400,
                modifier = Modifier.size(22.dp)
            )
        }

        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) PrimaryBlue else TextSlate400,
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}
