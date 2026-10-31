package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "activity_logs")
data class ActivityLog(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val action: String,
    val clientUsername: String? = null,
    val adminUser: String = "Security Admin",
    val details: String,
    val level: String = "INFO" // INFO, WARNING, ALERT, SUCCESS
)
