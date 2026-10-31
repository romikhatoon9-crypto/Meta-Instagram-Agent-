package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.MetaBlue
import com.example.ui.theme.StatusActive

enum class StepState {
    PENDING,
    IN_PROGRESS,
    COMPLETED
}

@Composable
fun ChecklistStepItem(
    stepNumber: Int,
    title: String,
    subtitle: String,
    state: StepState,
    modifier: Modifier = Modifier
) {
    val containerBg = when (state) {
        StepState.COMPLETED -> Color(0xFFF0FDF4)
        StepState.IN_PROGRESS -> Color(0xFFEFF6FF)
        StepState.PENDING -> Color(0xFFFAFAFA)
    }

    val borderColor = when (state) {
        StepState.COMPLETED -> Color(0xFFBBF7D0)
        StepState.IN_PROGRESS -> Color(0xFFBFDBFE)
        StepState.PENDING -> Color(0xFFE5E7EB)
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, borderColor, RoundedCornerShape(14.dp)),
        color = containerBg
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            // State Indicator Icon / Number
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(36.dp)
            ) {
                when (state) {
                    StepState.COMPLETED -> {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(StatusActive)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Step Complete",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    StepState.IN_PROGRESS -> {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFDBEAFE))
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(22.dp),
                                strokeWidth = 2.5.dp,
                                color = MetaBlue,
                                trackColor = Color(0x330866FF)
                            )
                        }
                    }
                    StepState.PENDING -> {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFF3F4F6))
                                .border(1.5.dp, Color(0xFFD1D5DB), CircleShape)
                        ) {
                            Text(
                                text = "$stepNumber",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF6B7280)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Step texts
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = if (state == StepState.IN_PROGRESS) FontWeight.Bold else FontWeight.SemiBold,
                        color = when (state) {
                            StepState.COMPLETED -> Color(0xFF166534)
                            StepState.IN_PROGRESS -> Color(0xFF1E40AF)
                            StepState.PENDING -> Color(0xFF374151)
                        }
                    )
                )

                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = when (state) {
                            StepState.COMPLETED -> Color(0xFF15803D)
                            StepState.IN_PROGRESS -> Color(0xFF3B82F6)
                            StepState.PENDING -> Color(0xFF9CA3AF)
                        }
                    )
                )
            }
        }
    }
}
