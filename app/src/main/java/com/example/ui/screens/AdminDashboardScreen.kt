package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ClientAccount
import com.example.data.model.SuspensionStatus
import com.example.ui.AppScreen
import com.example.ui.SecurityViewModel
import com.example.ui.components.StatusBadge
import com.example.ui.theme.MetaBlue
import com.example.ui.theme.MetaCyan
import com.example.ui.theme.StatusActive
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AdminDashboardScreen(
    viewModel: SecurityViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isAuthenticated by viewModel.isAdminAuthenticated.collectAsState()
    val adminPinInput by viewModel.adminPinInput.collectAsState()
    val pinError by viewModel.adminPinError.collectAsState()

    val allClients by viewModel.allClients.collectAsState()
    val activityLogs by viewModel.activityLogs.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val statusFilter by viewModel.statusFilter.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) } // 0: Clients, 1: Activity Logs
    var clientToDelete by remember { mutableStateOf<ClientAccount?>(null) }

    // If not authenticated, display Admin Security Passcode Screen
    if (!isAuthenticated) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = modifier
                .fillMaxSize()
                .background(Color(0xFFF8FAFC))
                .padding(24.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFEFF6FF))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Admin Lock",
                            tint = MetaBlue,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Administrator Security Gate",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Enter administrator passcode to access client security overviews and appeal management.",
                        fontSize = 13.sp,
                        color = Color(0xFF64748B),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    OutlinedTextField(
                        value = adminPinInput,
                        onValueChange = {
                            viewModel.adminPinInput.value = it
                            if (pinError) viewModel.adminPinError.value = false
                        },
                        placeholder = { Text("Passcode (Default: 1234)") },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_pin_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MetaBlue,
                            unfocusedBorderColor = if (pinError) Color.Red else Color(0xFFCBD5E1)
                        )
                    )

                    if (pinError) {
                        Text(
                            text = "Invalid passcode. Please use '1234'.",
                            color = Color(0xFFDC2626),
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = { viewModel.authenticateAdmin(adminPinInput) },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MetaBlue),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("admin_login_button")
                    ) {
                        Text("Unlock Admin Dashboard", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
        return
    }

    // Authenticated Admin Dashboard
    val totalClients = allClients.size
    val activeCount = allClients.count { it.isConnected && it.statusEnum == SuspensionStatus.ACTIVE }
    val pendingReviewCount = allClients.count { it.statusEnum == SuspensionStatus.UNDER_REVIEW }
    val suspendedCount = allClients.count { it.statusEnum == SuspensionStatus.SUSPENDED || it.statusEnum == SuspensionStatus.DISABLED }
    val appealsCount = allClients.count { it.statusEnum == SuspensionStatus.APPEAL_SUBMITTED }

    val filteredClients = allClients.filter { client ->
        val matchesSearch = searchQuery.isBlank() ||
                client.username.contains(searchQuery, ignoreCase = true) ||
                client.fullName.contains(searchQuery, ignoreCase = true) ||
                (client.appealTicketNumber?.contains(searchQuery, ignoreCase = true) ?: false)

        val matchesFilter = statusFilter == null || client.statusEnum == statusFilter
        matchesSearch && matchesFilter
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .padding(horizontal = 18.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(14.dp))

            // Top Admin Header with Lock Action
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(
                        text = "Admin Security Control",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                    Text(
                        text = "Authorized Session Active • Role: Lead Admin",
                        fontSize = 12.sp,
                        color = Color(0xFF166534),
                        fontWeight = FontWeight.SemiBold
                    )
                }

                OutlinedButton(
                    onClick = { viewModel.logoutAdmin() },
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCBD5E1))
                ) {
                    Icon(
                        imageVector = Icons.Default.LockOpen,
                        contentDescription = "Lock",
                        tint = Color(0xFF64748B),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Lock", fontSize = 12.sp, color = Color(0xFF64748B))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Top Stat Metric Cards (FlowRow / Grid)
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatMetricCard(
                    title = "Total Clients",
                    value = "$totalClients",
                    color = MetaBlue,
                    modifier = Modifier.weight(1f)
                )
                StatMetricCard(
                    title = "Active Valid",
                    value = "$activeCount",
                    color = StatusActive,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatMetricCard(
                    title = "Pending Reviews",
                    value = "$pendingReviewCount",
                    color = Color(0xFF2563EB),
                    modifier = Modifier.weight(1f)
                )
                StatMetricCard(
                    title = "Reported Suspended",
                    value = "$suspendedCount",
                    color = Color(0xFFEF4444),
                    modifier = Modifier.weight(1f)
                )
                StatMetricCard(
                    title = "Active Appeals",
                    value = "$appealsCount",
                    color = Color(0xFFF59E0B),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Tab Row
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.White,
                contentColor = MetaBlue,
                modifier = Modifier.clip(RoundedCornerShape(12.dp))
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Clients & Cases ($totalClients)", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Activity Audit Logs", fontWeight = FontWeight.Bold) }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        if (selectedTab == 0) {
            // Clients Tab: Search & Filters
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.searchQuery.value = it },
                    placeholder = { Text("Search by username, name, or ticket...") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = Color(0xFF64748B)
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotBlank()) {
                            IconButton(onClick = { viewModel.searchQuery.value = "" }) {
                                Icon(imageVector = Icons.Default.Close, contentDescription = "Clear")
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("admin_search_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MetaBlue,
                        unfocusedBorderColor = Color(0xFFE2E8F0),
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Filter Chips
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val isAll = statusFilter == null
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (isAll) MetaBlue else Color.White,
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isAll) MetaBlue else Color(0xFFE2E8F0)),
                        modifier = Modifier.clickable { viewModel.statusFilter.value = null }
                    ) {
                        Text(
                            text = "All",
                            color = if (isAll) Color.White else Color(0xFF475569),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }

                    SuspensionStatus.entries.forEach { status ->
                        val isSelected = statusFilter == status
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = if (isSelected) status.color else Color.White,
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) status.color else Color(0xFFE2E8F0)),
                            modifier = Modifier.clickable { viewModel.statusFilter.value = status }
                        ) {
                            Text(
                                text = status.displayName,
                                color = if (isSelected) Color.White else status.color,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
            }

            // Client Cards
            if (filteredClients.isEmpty()) {
                item {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp)
                    ) {
                        Text(
                            text = "No clients matching current criteria.",
                            color = Color(0xFF64748B),
                            fontSize = 14.sp
                        )
                    }
                }
            } else {
                items(filteredClients, key = { it.id }) { client ->
                    AdminClientCard(
                        client = client,
                        onViewDashboard = {
                            viewModel.selectClient(client.id)
                            viewModel.navigateTo(AppScreen.DASHBOARD)
                        },
                        onRunAudit = {
                            viewModel.selectClient(client.id)
                            viewModel.navigateTo(AppScreen.SECURITY_REVIEW)
                        },
                        onEditStatus = {
                            viewModel.prepareSuspensionForm(client)
                        },
                        onDelete = { clientToDelete = client }
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        } else {
            // Activity Logs Tab
            if (activityLogs.isEmpty()) {
                item {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp)
                    ) {
                        Text("No recorded activity logs.", color = Color(0xFF64748B))
                    }
                }
            } else {
                items(activityLogs, key = { it.id }) { log ->
                    ActivityLogItem(log = log)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(30.dp))
        }
    }

    // Delete Confirmation
    if (clientToDelete != null) {
        val client = clientToDelete!!
        AlertDialog(
            onDismissRequest = { clientToDelete = null },
            title = { Text("Remove Client Account?", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to remove @${client.username} and its audit history from the local database?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteClientAccount(client)
                        clientToDelete = null
                        Toast.makeText(context, "@${client.username} removed", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { clientToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun StatMetricCard(
    title: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = title,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF64748B)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}

@Composable
fun AdminClientCard(
    client: ClientAccount,
    onViewDashboard: () -> Unit,
    onRunAudit: () -> Unit,
    onEditStatus: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFEFF6FF))
                    ) {
                        Text(
                            text = client.username.first().uppercase(),
                            color = MetaBlue,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "@${client.username}",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                        Text(
                            text = "ID: ${client.metaAccountId} • ${client.accountType}",
                            fontSize = 11.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }

                StatusBadge(status = client.statusEnum)
            }

            if (!client.appealTicketNumber.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Appeal Ticket: ${client.appealTicketNumber}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFD97706)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            androidx.compose.material3.Divider(color = Color(0xFFF1F5F9))
            Spacer(modifier = Modifier.height(10.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    OutlinedButton(
                        onClick = onViewDashboard,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Text("View", fontSize = 11.sp)
                    }
                    OutlinedButton(
                        onClick = onRunAudit,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Text("Audit", fontSize = 11.sp)
                    }
                    OutlinedButton(
                        onClick = onEditStatus,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Text("Status", fontSize = 11.sp)
                    }
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = Color(0xFFDC2626),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ActivityLogItem(log: com.example.data.model.ActivityLog) {
    val dateFormat = remember { SimpleDateFormat("MMM dd, hh:mm a", Locale.getDefault()) }
    val formattedTime = remember(log.timestamp) { dateFormat.format(Date(log.timestamp)) }

    val levelColor = when (log.level) {
        "SUCCESS" -> StatusActive
        "ALERT" -> Color(0xFFEF4444)
        "WARNING" -> Color(0xFFF59E0B)
        else -> MetaBlue
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(levelColor)
                    .padding(top = 4.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = log.action,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                    Text(
                        text = formattedTime,
                        fontSize = 11.sp,
                        color = Color(0xFF94A3B8)
                    )
                }
                Text(
                    text = log.details,
                    fontSize = 12.sp,
                    color = Color(0xFF475569),
                    modifier = Modifier.padding(top = 2.dp)
                )
                Text(
                    text = "Actor: ${log.adminUser}" + if (log.clientUsername != null) " • Client: @${log.clientUsername}" else "",
                    fontSize = 10.sp,
                    color = Color(0xFF64748B),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}
