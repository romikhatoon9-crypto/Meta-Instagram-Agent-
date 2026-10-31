package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Meta & Sufi Security Brand Palette
val MetaBlue = Color(0xFF0866FF)
val MetaCyan = Color(0xFF00B2FF)
val MetaNavy = Color(0xFF0A192F)
val MetaDarkBlue = Color(0xFF004499)

// Status Colors
val StatusActive = Color(0xFF10B981)       // Green
val StatusDisabled = Color(0xFFDC2626)     // Deep Red
val StatusSuspended = Color(0xFFEF4444)    // Red
val StatusRestricted = Color(0xFF8B5CF6)   // Purple
val StatusUnderReview = Color(0xFF2563EB)  // Blue
val StatusAppealSubmitted = Color(0xFFF59E0B) // Amber

// Surface & Neutral Colors
val SurfaceLight = Color(0xFFFFFFFF)
val SurfaceSubtle = Color(0xFFF8FAFC)
val SurfaceBorder = Color(0xFFE2E8F0)
val SurfaceCard = Color(0xFFFFFFFF)

val TextPrimaryLight = Color(0xFF0F172A)
val TextSecondaryLight = Color(0xFF64748B)
val TextMutedLight = Color(0xFF94A3B8)

// Dark Theme Colors
val SurfaceDark = Color(0xFF0B132B)
val SurfaceDarkCard = Color(0xFF1C2541)
val SurfaceDarkBorder = Color(0xFF3A506B)
val TextPrimaryDark = Color(0xFFF8FAFC)
val TextSecondaryDark = Color(0xFF94A3B8)

// Gradients
val MetaBlueGradient = Brush.horizontalGradient(
    colors = listOf(MetaBlue, MetaCyan)
)

val MetaSecurityGradient = Brush.linearGradient(
    colors = listOf(Color(0xFF0866FF), Color(0xFF00C4FF), Color(0xFF0072F5))
)

val CardGlowGradient = Brush.verticalGradient(
    colors = listOf(Color(0x1A0866FF), Color(0x00FFFFFF))
)
