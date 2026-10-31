package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.ActivityLog
import com.example.data.model.ClientAccount
import com.example.data.model.SecurityReview
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [ClientAccount::class, SecurityReview::class, ActivityLog::class],
    version = 1,
    exportSchema = false
)
abstract class SufiSecurityDatabase : RoomDatabase() {
    abstract fun clientAccountDao(): ClientAccountDao
    abstract fun securityReviewDao(): SecurityReviewDao
    abstract fun activityLogDao(): ActivityLogDao

    companion object {
        @Volatile
        private var INSTANCE: SufiSecurityDatabase? = null

        fun getDatabase(context: Context): SufiSecurityDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SufiSecurityDatabase::class.java,
                    "sufi_security_db"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // Populate initial clients in background
                            CoroutineScope(Dispatchers.IO).launch {
                                INSTANCE?.let { database ->
                                    populateInitialData(database)
                                }
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private suspend fun populateInitialData(db: SufiSecurityDatabase) {
            val clients = listOf(
                ClientAccount(
                    id = "client_sufi_01",
                    username = "sufi_official",
                    fullName = "Sufi Global Enterprise",
                    profilePictureUrl = null,
                    metaAccountId = "17841400892019482",
                    accountType = "BUSINESS",
                    platform = "Instagram",
                    mediaCount = 342,
                    followersCount = 48250,
                    followingCount = 312,
                    linkedPageName = "Sufi Enterprise Page",
                    authorizedScopes = "instagram_basic,pages_show_list,instagram_manage_insights",
                    tokenExpiresAt = System.currentTimeMillis() + (58L * 24 * 60 * 60 * 1000),
                    lastSyncTime = System.currentTimeMillis() - 1000 * 60 * 24, // 24m ago
                    connectionStatus = "CONNECTED",
                    suspensionStatus = "ACTIVE",
                    notes = "Enterprise tier client. High-volume business account verified via Meta Business Manager.",
                    twoFactorEnabled = true,
                    thirdPartyAppsCount = 1,
                    recentSecurityAlerts = 0
                ),
                ClientAccount(
                    id = "client_creative_02",
                    username = "creativestudio_pk",
                    fullName = "Creative Studio Agency",
                    profilePictureUrl = null,
                    metaAccountId = "17841405912849102",
                    accountType = "CREATOR",
                    platform = "Instagram",
                    mediaCount = 189,
                    followersCount = 19400,
                    followingCount = 420,
                    linkedPageName = "Creative Studio Media",
                    authorizedScopes = "instagram_basic,instagram_manage_comments",
                    tokenExpiresAt = System.currentTimeMillis() + (42L * 24 * 60 * 60 * 1000),
                    lastSyncTime = System.currentTimeMillis() - 1000 * 60 * 60 * 4,
                    connectionStatus = "CONNECTED",
                    suspensionStatus = "UNDER_REVIEW",
                    notes = "Client flagged unusual login prompts from unknown IP. Routine internal security review triggered.",
                    twoFactorEnabled = false,
                    thirdPartyAppsCount = 4,
                    recentSecurityAlerts = 2
                ),
                ClientAccount(
                    id = "client_fashion_03",
                    username = "boutique_couture",
                    fullName = "Boutique Luxury Couture",
                    profilePictureUrl = null,
                    metaAccountId = "17841409948201944",
                    accountType = "BUSINESS",
                    platform = "Instagram",
                    mediaCount = 512,
                    followersCount = 82100,
                    followingCount = 89,
                    linkedPageName = "Boutique Official Store",
                    authorizedScopes = "instagram_basic,pages_show_list",
                    tokenExpiresAt = System.currentTimeMillis() + (15L * 24 * 60 * 60 * 1000),
                    lastSyncTime = System.currentTimeMillis() - 1000 * 60 * 60 * 12,
                    connectionStatus = "CONNECTED",
                    suspensionStatus = "APPEAL_SUBMITTED",
                    notes = "Client reported advertising feature restriction after rapid campaign setup. Meta Case Ticket #8921094 filed.",
                    appealTicketNumber = "META-REF-8921094",
                    appealDate = System.currentTimeMillis() - (1000L * 60 * 60 * 48), // 2 days ago
                    appealMethod = "Meta Business Help Center - Priority Live Agent Chat",
                    appealNotes = "Submitted business registration certificate and authorized admin ID. Awaiting 48-72h Meta internal queue decision.",
                    evidenceDetails = "Certificate of Incorporation, Photo ID, Tax Identification confirmation.",
                    twoFactorEnabled = true,
                    thirdPartyAppsCount = 2,
                    recentSecurityAlerts = 1
                )
            )

            db.clientAccountDao().insertClients(clients)

            // Initial logs
            db.activityLogDao().insertLog(
                ActivityLog(
                    action = "System Initialized",
                    clientUsername = null,
                    adminUser = "System Admin",
                    details = "Sufi Account Security Manager initialized with encrypted audit repository.",
                    level = "INFO"
                )
            )
            db.activityLogDao().insertLog(
                ActivityLog(
                    action = "Client Connected",
                    clientUsername = "sufi_official",
                    adminUser = "OAuth Flow",
                    details = "Authorized Instagram Professional account connected with 60-day token.",
                    level = "SUCCESS"
                )
            )
            db.activityLogDao().insertLog(
                ActivityLog(
                    action = "Appeal Recorded",
                    clientUsername = "boutique_couture",
                    adminUser = "Lead Security Analyst",
                    details = "Recorded client appeal case META-REF-8921094 for restricted advertising access.",
                    level = "ALERT"
                )
            )
        }
    }
}
