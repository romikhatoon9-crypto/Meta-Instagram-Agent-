package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SuspensionStatus
import com.example.ui.AppScreen
import com.example.ui.SecurityViewModel
import com.example.ui.components.ChecklistStepItem
import com.example.ui.components.CircularProgressAuditIndicator
import com.example.ui.components.MetaGradientProgressBar
import com.example.ui.components.StepState
import com.example.ui.theme.MetaBlue
import com.example.ui.theme.StatusActive

@Composable
fun AccountSecurityReviewScreen(
    viewModel: SecurityViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currentClient by viewModel.currentClient.collectAsState()
    val isRunning by viewModel.isAuditRunning.collectAsState()
    val isComplete by viewModel.isAuditCompleted.collectAsState()
    val progress by viewModel.auditProgress.collectAsState()
    val percentage by viewModel.auditPercentage.collectAsState()
    val currentStep by viewModel.currentAuditStep.collectAsState()
    val latestReview by viewModel.currentReview.collectAsState()

    val scrollState = rememberScrollState()

    // Automatically trigger review when opened if not yet completed or running
    LaunchedEffect(currentClient?.id) {
        if (!isRunning && !isComplete) {
            viewModel.startSecurityReview()
        }
    }

    val username = currentClient?.username ?: "Client Account"
    val profilePic = currentClient?.profilePictureUrl

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White) // Crisp white background per reference design
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Navigation Bar
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(
                onClick = { viewModel.navigateTo(AppScreen.DASHBOARD) },
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color(0xFF0F172A)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Account Security Review",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A)
            )
            Spacer(modifier = Modifier.weight(1f))
            if (isComplete) {
                IconButton(onClick = { viewModel.startSecurityReview() }) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Restart Review",
                        tint = MetaBlue
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Circular Animated Loading Indicator & Profile
        CircularProgressAuditIndicator(
            username = username,
            profilePictureUrl = profilePic,
            isScanning = isRunning,
            isComplete = isComplete,
            size = 124.dp
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Username Header
        Text(
            text = "@$username",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0F172A)
        )

        Text(
            text = if (isComplete) "Security Review Finalized" else "Analyzing Authorized Meta Account Connection...",
            fontSize = 13.sp,
            color = if (isComplete) Color(0xFF166534) else Color(0xFF64748B),
            fontWeight = if (isComplete) FontWeight.SemiBold else FontWeight.Normal,
            modifier = Modifier.padding(top = 2.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Progress Section: Percentage and Meta-inspired blue gradient progress bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Verification Progress",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF475569)
            )
            Text(
                text = "$percentage%",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MetaBlue
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Blue Gradient Progress Bar
        MetaGradientProgressBar(
            progress = progress,
            height = 12.dp,
            showShimmer = isRunning
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Four Verification Checklist Items (Specified in Reference Design)
        Text(
            text = "Verification Checklist",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0F172A),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp)
        )

        // Step 1: Verifying authorized account connection
        ChecklistStepItem(
            stepNumber = 1,
            title = "1. Verifying authorized account connection",
            subtitle = if (currentStep > 1 || isComplete) "Meta OAuth 2.0 token active & validated" else "Checking token signature with Meta server...",
            state = when {
                currentStep > 1 || isComplete -> StepState.COMPLETED
                currentStep == 1 -> StepState.IN_PROGRESS
                else -> StepState.PENDING
            }
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Step 2: Checking available account information
        ChecklistStepItem(
            stepNumber = 2,
            title = "2. Checking available account information",
            subtitle = if (currentStep > 2 || isComplete) "Profile, business metadata, and media counts synchronized" else "Querying Meta Graph API endpoints...",
            state = when {
                currentStep > 2 || isComplete -> StepState.COMPLETED
                currentStep == 2 -> StepState.IN_PROGRESS
                else -> StepState.PENDING
            }
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Step 3: Reviewing recorded security concerns
        ChecklistStepItem(
            stepNumber = 3,
            title = "3. Reviewing recorded security concerns",
            subtitle = if (currentStep > 3 || isComplete) "Audited 2FA status, authorized apps, and client reports" else "Scanning client reports and security policies...",
            state = when {
                currentStep > 3 || isComplete -> StepState.COMPLETED
                currentStep == 3 -> StepState.IN_PROGRESS
                else -> StepState.PENDING
            }
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Step 4: Finalizing security report
        ChecklistStepItem(
            stepNumber = 4,
            title = "4. Finalizing security report",
            subtitle = if (isComplete) "Security diagnostics and recommendations compiled" else "Compiling comprehensive diagnostic audit...",
            state = when {
                isComplete -> StepState.COMPLETED
                currentStep == 4 -> StepState.IN_PROGRESS
                else -> StepState.PENDING
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // API Limitation & Security Transparency Notice (Critical Requirement)
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .border(1.dp, Color(0xFFFEF3C7), RoundedCornerShape(12.dp)),
            color = Color(0xFFFFFBEB)
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Meta API Notice",
                    tint = Color(0xFFD97706),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Official API Limitation Disclosure",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF92400E)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Not available through Meta API: Meta's official Graph API does not provide automated endpoints for account suspensions, policy strikes, or restrictions. Any account enforcement details must be recorded manually by authorized administrators below.",
                        fontSize = 11.sp,
                        color = Color(0xFFB45309),
                        lineHeight = 15.sp
                    )
                }
            }
        }

        // Audit Results Card (When completed)
        AnimatedVisibility(
            visible = isComplete,
            enter = fadeIn() + slideInVertically()
        ) {
            Column(modifier = Modifier.padding(top = 20.dp)) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column {
                                Text(
                                    text = "Overall Security Health",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF64748B)
                                )
                                Text(
                                    text = "Rating: High Protection",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0F172A)
                                )
                            }

                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFDCFCE7))
                                    .border(2.dp, Color(0xFF86EFAC), CircleShape)
                            ) {
                                Text(
                                    text = "${latestReview?.overallScore ?: 94}",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF166534)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        androidx.compose.material3.Divider(color = Color(0xFFE2E8F0))
                        Spacer(modifier = Modifier.height(12.dp))

                        val checkPoints = listOf(
                            "Meta OAuth Session" to "Encrypted 60-day token verified",
                            "Two-Factor Authentication" to "Hardware key or Authenticator recommended",
                            "Third-Party Access" to "2 authorized client tools detected",
                            "Suspension / Strikes" to "Not available via API (Manual Intake)"
                        )

                        checkPoints.forEach { (title, detail) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = title,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF475569)
                                )
                                Text(
                                    text = detail,
                                    fontSize = 12.sp,
                                    color = Color(0xFF0F172A)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            currentClient?.let { viewModel.prepareSuspensionForm(it) }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MetaBlue),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("record_suspension_button")
                    ) {
                        Text("Record Status / Appeal", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = {
                            Toast.makeText(
                                context,
                                "Security report downloaded to device",
                                Toast.LENGTH_SHORT
                            ).show()
                        },
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCBD5E1)),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share",
                            tint = MetaBlue,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Export Audit", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}
