package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.AdminPanelSettings
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.screens.AccountDashboardScreen
import com.example.ui.screens.AccountSecurityReviewScreen
import com.example.ui.screens.AdminDashboardScreen
import com.example.ui.screens.ClientConnectionScreen
import com.example.ui.screens.SuspensionManagementScreen
import com.example.ui.theme.MetaBlue
import com.example.ui.theme.MetaCyan

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: SecurityViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val currentClient by viewModel.currentClient.collectAsState()

    // Handle back button to return to Dashboard from sub-screens
    BackHandler(enabled = currentScreen != AppScreen.DASHBOARD) {
        viewModel.navigateTo(AppScreen.DASHBOARD)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        listOf(MetaBlue, MetaCyan)
                                    )
                                )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = "Sufi Security",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Sufi Security",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color(0xFF0F172A)
                        )
                        if (currentClient != null) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                shape = CircleShape,
                                color = Color(0xFFE0F2FE)
                            ) {
                                Text(
                                    text = "@${currentClient!!.username}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MetaBlue,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color(0xFF0F172A)
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 8.dp,
                modifier = Modifier.testTag("bottom_nav_bar")
            ) {
                // Connect
                NavigationBarItem(
                    selected = currentScreen == AppScreen.CONNECTION,
                    onClick = { viewModel.navigateTo(AppScreen.CONNECTION) },
                    icon = {
                        Icon(
                            imageVector = if (currentScreen == AppScreen.CONNECTION) Icons.Default.Link else Icons.Outlined.Link,
                            contentDescription = "Connect"
                        )
                    },
                    label = { Text("Connect", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MetaBlue,
                        indicatorColor = Color(0xFFE8F2FF),
                        unselectedIconColor = Color(0xFF64748B)
                    ),
                    modifier = Modifier.testTag("nav_item_connect")
                )

                // Dashboard
                NavigationBarItem(
                    selected = currentScreen == AppScreen.DASHBOARD,
                    onClick = { viewModel.navigateTo(AppScreen.DASHBOARD) },
                    icon = {
                        Icon(
                            imageVector = if (currentScreen == AppScreen.DASHBOARD) Icons.Default.Dashboard else Icons.Outlined.Dashboard,
                            contentDescription = "Dashboard"
                        )
                    },
                    label = { Text("Dashboard", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MetaBlue,
                        indicatorColor = Color(0xFFE8F2FF),
                        unselectedIconColor = Color(0xFF64748B)
                    ),
                    modifier = Modifier.testTag("nav_item_dashboard")
                )

                // Security Review
                NavigationBarItem(
                    selected = currentScreen == AppScreen.SECURITY_REVIEW,
                    onClick = { viewModel.navigateTo(AppScreen.SECURITY_REVIEW) },
                    icon = {
                        Icon(
                            imageVector = if (currentScreen == AppScreen.SECURITY_REVIEW) Icons.Default.Security else Icons.Outlined.Security,
                            contentDescription = "Security Review"
                        )
                    },
                    label = { Text("Review", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MetaBlue,
                        indicatorColor = Color(0xFFE8F2FF),
                        unselectedIconColor = Color(0xFF64748B)
                    ),
                    modifier = Modifier.testTag("nav_item_review")
                )

                // Suspension Management
                NavigationBarItem(
                    selected = currentScreen == AppScreen.SUSPENSION_MGMT,
                    onClick = { viewModel.navigateTo(AppScreen.SUSPENSION_MGMT) },
                    icon = {
                        Icon(
                            imageVector = if (currentScreen == AppScreen.SUSPENSION_MGMT) Icons.Default.Warning else Icons.Outlined.Warning,
                            contentDescription = "Suspensions"
                        )
                    },
                    label = { Text("Policy", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MetaBlue,
                        indicatorColor = Color(0xFFE8F2FF),
                        unselectedIconColor = Color(0xFF64748B)
                    ),
                    modifier = Modifier.testTag("nav_item_suspension")
                )

                // Admin Dashboard
                NavigationBarItem(
                    selected = currentScreen == AppScreen.ADMIN,
                    onClick = { viewModel.navigateTo(AppScreen.ADMIN) },
                    icon = {
                        Icon(
                            imageVector = if (currentScreen == AppScreen.ADMIN) Icons.Default.AdminPanelSettings else Icons.Outlined.AdminPanelSettings,
                            contentDescription = "Admin"
                        )
                    },
                    label = { Text("Admin", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MetaBlue,
                        indicatorColor = Color(0xFFE8F2FF),
                        unselectedIconColor = Color(0xFF64748B)
                    ),
                    modifier = Modifier.testTag("nav_item_admin")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = currentScreen,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "screenTransition"
            ) { targetScreen ->
                when (targetScreen) {
                    AppScreen.CONNECTION -> ClientConnectionScreen(viewModel)
                    AppScreen.DASHBOARD -> AccountDashboardScreen(viewModel)
                    AppScreen.SECURITY_REVIEW -> AccountSecurityReviewScreen(viewModel)
                    AppScreen.SUSPENSION_MGMT -> SuspensionManagementScreen(viewModel)
                    AppScreen.ADMIN -> AdminDashboardScreen(viewModel)
                }
            }
        }
    }
}
