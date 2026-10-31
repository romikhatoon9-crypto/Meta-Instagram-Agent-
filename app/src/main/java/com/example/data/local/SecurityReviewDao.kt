package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.SecurityReview
import kotlinx.coroutines.flow.Flow

@Dao
interface SecurityReviewDao {
    @Query("SELECT * FROM security_reviews WHERE clientAccountId = :clientId ORDER BY timestamp DESC")
    fun getReviewsForClient(clientId: String): Flow<List<SecurityReview>>

    @Query("SELECT * FROM security_reviews WHERE clientAccountId = :clientId ORDER BY timestamp DESC LIMIT 1")
    fun getLatestReviewForClient(clientId: String): Flow<SecurityReview?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReview(review: SecurityReview)

    @Query("DELETE FROM security_reviews WHERE clientAccountId = :clientId")
    suspend fun deleteReviewsForClient(clientId: String)
}
