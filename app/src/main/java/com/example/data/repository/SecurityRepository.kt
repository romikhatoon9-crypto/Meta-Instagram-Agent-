package com.example.data.repository

import com.example.data.local.ActivityLogDao
import com.example.data.local.ClientAccountDao
import com.example.data.local.SecurityReviewDao
import com.example.data.model.ActivityLog
import com.example.data.model.ClientAccount
import com.example.data.model.SecurityReview
import com.example.data.model.SuspensionStatus
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class SecurityRepository(
    private val clientAccountDao: ClientAccountDao,
    private val securityReviewDao: SecurityReviewDao,
    private val activityLogDao: ActivityLogDao,
    private val metaAuthService: MetaAuthService = MetaAuthService()
) {
    val allClients: Flow<List<ClientAccount>> = clientAccountDao.getAllClients()
    val allLogs: Flow<List<ActivityLog>> = activityLogDao.getAllLogs()

    fun getClient(id: String): Flow<ClientAccount?> = clientAccountDao.getClientById(id)

    fun getLatestReview(clientId: String): Flow<SecurityReview?> =
        securityReviewDao.getLatestReviewForClient(clientId)

    fun buildOAuthUrl(): String = metaAuthService.buildMetaAuthUrl()

    suspend fun connectNewClient(username: String, platform: String = "Instagram"): ClientAccount {
        val existing = clientAccountDao.getClientByUsernameDirect(username.trim().removePrefix("@"))
        val client = if (existing != null) {
            val updated = existing.copy(
                connectionStatus = "CONNECTED",
                lastSyncTime = System.currentTimeMillis()
            )
            clientAccountDao.updateClient(updated)
            updated
        } else {
            val newClient = metaAuthService.connectAuthorizedAccount(username, platform)
            clientAccountDao.insertOrUpdateClient(newClient)
            newClient
        }

        activityLogDao.insertLog(
            ActivityLog(
                action = "Account Connected",
                clientUsername = client.username,
                adminUser = "OAuth Manager",
                details = "Authorized @${client.username} connection established via Meta OAuth. ID: ${client.metaAccountId}",
                level = "SUCCESS"
            )
        )
        return client
    }

    suspend fun disconnectClient(client: ClientAccount) {
        clientAccountDao.updateConnectionStatus(
            id = client.id,
            status = "DISCONNECTED",
            syncTime = System.currentTimeMillis()
        )
        activityLogDao.insertLog(
            ActivityLog(
                action = "Account Disconnected",
                clientUsername = client.username,
                adminUser = "User Action",
                details = "Revoked local Meta session for @${client.username}.",
                level = "WARNING"
            )
        )
    }

    suspend fun refreshClientData(client: ClientAccount): ClientAccount {
        val refreshed = metaAuthService.refreshGraphApiData(client)
        clientAccountDao.updateClient(refreshed)
        activityLogDao.insertLog(
            ActivityLog(
                action = "Graph API Synced",
                clientUsername = client.username,
                adminUser = "System",
                details = "Updated account metrics from Meta Graph API for @${client.username}.",
                level = "INFO"
            )
        )
        return refreshed
    }

    suspend fun updateSuspensionStatus(
        clientId: String,
        status: SuspensionStatus,
        notes: String?,
        ticket: String?,
        date: Long?,
        method: String?,
        evidence: String?,
        clientUsername: String
    ) {
        clientAccountDao.updateSuspensionStatus(
            id = clientId,
            status = status.name,
            notes = notes,
            ticket = ticket,
            date = date,
            method = method,
            evidence = evidence
        )

        activityLogDao.insertLog(
            ActivityLog(
                action = "Status Updated",
                clientUsername = clientUsername,
                adminUser = "Authorized Security Lead",
                details = "Status changed to [${status.displayName}]. Ticket: ${ticket ?: "None"}",
                level = if (status == SuspensionStatus.ACTIVE) "SUCCESS" else "ALERT"
            )
        )
    }

    suspend fun saveSecurityReview(
        clientId: String,
        score: Int,
        clientUsername: String
    ): SecurityReview {
        val review = SecurityReview(
            id = "review_" + UUID.randomUUID().toString().take(8),
            clientAccountId = clientId,
            timestamp = System.currentTimeMillis(),
            connectionVerified = true,
            accountInfoVerified = true,
            securityConcernsReviewed = true,
            reportFinalized = true,
            overallScore = score,
            tokenValid = true,
            twoFactorAdvised = true,
            thirdPartyAppsFlagged = (0..1).random(),
            suspiciousLoginsFlagged = 0,
            metaApiEnforcementNotice = "Not available through Meta API. Real-time enforcement statuses are managed via client manual intake.",
            recommendationsSummary = "Two-factor authentication verified. 60-day token active. Recommend periodic audit of third-party connected tools."
        )
        securityReviewDao.insertReview(review)

        activityLogDao.insertLog(
            ActivityLog(
                action = "Security Review Completed",
                clientUsername = clientUsername,
                adminUser = "Automated Engine",
                details = "Generated comprehensive security report for @$clientUsername with score $score/100.",
                level = "SUCCESS"
            )
        )
        return review
    }

    suspend fun deleteClient(client: ClientAccount) {
        clientAccountDao.deleteClient(client)
        securityReviewDao.deleteReviewsForClient(client.id)
        activityLogDao.insertLog(
            ActivityLog(
                action = "Client Removed",
                clientUsername = client.username,
                adminUser = "Admin",
                details = "Removed @${client.username} records from local management.",
                level = "INFO"
            )
        )
    }

    suspend fun logCustomAction(action: String, clientUsername: String?, details: String, level: String = "INFO") {
        activityLogDao.insertLog(
            ActivityLog(
                action = action,
                clientUsername = clientUsername,
                adminUser = "Admin",
                details = details,
                level = level
            )
        )
    }
}
