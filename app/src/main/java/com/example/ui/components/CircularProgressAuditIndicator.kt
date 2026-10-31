package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.theme.MetaBlue
import com.example.ui.theme.MetaCyan
import com.example.ui.theme.StatusActive

/**
 * Circular animated loading indicator featuring:
 * - Rotating Meta-inspired blue gradient arc
 * - Profile avatar in center
 * - Animated verification badge
 */
@Composable
fun CircularProgressAuditIndicator(
    username: String,
    profilePictureUrl: String?,
    isScanning: Boolean,
    isComplete: Boolean,
    modifier: Modifier = Modifier,
    size: Dp = 120.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "arcRotate")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotationAngle"
    )

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.size(size)
    ) {
        // Subtle outer pulse wave when actively scanning
        if (isScanning && !isComplete) {
            Box(
                modifier = Modifier
                    .size(size * pulseScale)
                    .clip(CircleShape)
                    .background(Color(0x140866FF))
            )
        }

        // Background track circle
        Canvas(modifier = Modifier.size(size)) {
            drawCircle(
                color = Color(0xFFF1F5F9),
                radius = (size.toPx() - 14.dp.toPx()) / 2,
                style = Stroke(width = 4.dp.toPx())
            )
        }

        // Rotating gradient arc
        if (isScanning && !isComplete) {
            Canvas(
                modifier = Modifier
                    .size(size)
                    .rotate(rotation)
            ) {
                drawArc(
                    brush = Brush.sweepGradient(
                        listOf(
                            Color.Transparent,
                            MetaCyan.copy(alpha = 0.4f),
                            MetaBlue,
                            Color(0xFF00C6FF)
                        )
                    ),
                    startAngle = 0f,
                    sweepAngle = 280f,
                    useCenter = false,
                    style = Stroke(width = 5.dp.toPx(), cap = StrokeCap.Round)
                )
            }
        } else if (isComplete) {
            // Completed static gradient full circle
            Canvas(modifier = Modifier.size(size)) {
                drawCircle(
                    brush = Brush.linearGradient(listOf(StatusActive, Color(0xFF34D399))),
                    radius = (size.toPx() - 14.dp.toPx()) / 2,
                    style = Stroke(width = 4.5.dp.toPx())
                )
            }
        }

        // Inner Avatar Box
        val avatarSize = size - 24.dp
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(avatarSize)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(Color(0xFFF8FAFC), Color(0xFFE2E8F0))
                    )
                )
                .border(2.dp, Color.White, CircleShape)
        ) {
            if (!profilePictureUrl.isNullOrBlank()) {
                AsyncImage(
                    model = profilePictureUrl,
                    contentDescription = "Profile picture for $username",
                    modifier = Modifier
                        .size(avatarSize)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                // Monogram avatar
                val initial = username.firstOrNull()?.uppercaseChar()?.toString() ?: "S"
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(avatarSize)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0xFF0866FF), Color(0xFF00B2FF))
                            )
                        )
                ) {
                    Text(
                        text = initial,
                        fontSize = (avatarSize.value * 0.42f).sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }

        // Security / Verification Badge overlay at bottom right
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = (-4).dp, y = (-4).dp)
        ) {
            AnimatedVisibility(
                visible = isComplete,
                enter = fadeIn() + scaleIn()
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .border(2.dp, Color.White, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Verified",
                        tint = StatusActive,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            AnimatedVisibility(
                visible = !isComplete,
                enter = fadeIn()
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(26.dp)
                        .clip(CircleShape)
                        .background(MetaBlue)
                        .border(2.dp, Color.White, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = "Security Shield",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
