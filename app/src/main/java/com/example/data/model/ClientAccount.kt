package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "clients")
data class ClientAccount(
    @PrimaryKey
    val id: String,
    val username: String,
    val fullName: String,
    val profilePictureUrl: String? = null,
    val metaAccountId: String,
    val accountType: String = "BUSINESS", // BUSINESS, CREATOR, PERSONAL
    val platform: String = "Instagram", // Instagram or Facebook
    val mediaCount: Int = 0,
    val followersCount: Int = 0,
    val followingCount: Int = 0,
    val linkedPageName: String? = null,
    val authorizedScopes: String = "instagram_basic,pages_show_list",
    val tokenExpiresAt: Long = System.currentTimeMillis() + (60L * 24 * 60 * 60 * 1000), // 60 days
    val lastSyncTime: Long = System.currentTimeMillis(),
    val connectionStatus: String = "CONNECTED", // CONNECTED, DISCONNECTED, TOKEN_EXPIRED
    val suspensionStatus: String = "ACTIVE", // From SuspensionStatus enum
    val notes: String? = null,
    val appealTicketNumber: String? = null,
    val appealDate: Long? = null,
    val appealMethod: String? = null,
    val appealNotes: String? = null,
    val evidenceDetails: String? = null,
    val twoFactorEnabled: Boolean = true,
    val thirdPartyAppsCount: Int = 2,
    val recentSecurityAlerts: Int = 0
) {
    val statusEnum: SuspensionStatus
        get() = SuspensionStatus.fromString(suspensionStatus)

    val isConnected: Boolean
        get() = connectionStatus == "CONNECTED"
}
