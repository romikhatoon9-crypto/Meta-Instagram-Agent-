package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "security_reviews")
data class SecurityReview(
    @PrimaryKey
    val id: String,
    val clientAccountId: String,
    val timestamp: Long = System.currentTimeMillis(),
    val connectionVerified: Boolean = true,
    val accountInfoVerified: Boolean = true,
    val securityConcernsReviewed: Boolean = true,
    val reportFinalized: Boolean = true,
    val overallScore: Int = 92, // 0 - 100
    val tokenValid: Boolean = true,
    val twoFactorAdvised: Boolean = true,
    val thirdPartyAppsFlagged: Int = 0,
    val suspiciousLoginsFlagged: Int = 0,
    val metaApiEnforcementNotice: String = "Not available through Meta API. Real-time enforcement statuses are managed via client manual intake.",
    val recommendationsSummary: String = "Enable hardware-based security keys, audit authorized third-party OAuth apps, and verify Business Manager partner permissions."
)
