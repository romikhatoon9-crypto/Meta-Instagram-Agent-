package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.ClientAccount
import kotlinx.coroutines.flow.Flow

@Dao
interface ClientAccountDao {
    @Query("SELECT * FROM clients ORDER BY lastSyncTime DESC")
    fun getAllClients(): Flow<List<ClientAccount>>

    @Query("SELECT * FROM clients WHERE id = :id LIMIT 1")
    fun getClientById(id: String): Flow<ClientAccount?>

    @Query("SELECT * FROM clients WHERE username = :username LIMIT 1")
    suspend fun getClientByUsernameDirect(username: String): ClientAccount?

    @Query("SELECT * FROM clients WHERE connectionStatus = 'CONNECTED' LIMIT 1")
    fun getFirstActiveClient(): Flow<ClientAccount?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateClient(client: ClientAccount)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClients(clients: List<ClientAccount>)

    @Update
    suspend fun updateClient(client: ClientAccount)

    @Query("""
        UPDATE clients 
        SET suspensionStatus = :status, 
            notes = :notes, 
            appealTicketNumber = :ticket, 
            appealDate = :date, 
            appealMethod = :method, 
            evidenceDetails = :evidence 
        WHERE id = :id
    """)
    suspend fun updateSuspensionStatus(
        id: String,
        status: String,
        notes: String?,
        ticket: String?,
        date: Long?,
        method: String?,
        evidence: String?
    )

    @Query("UPDATE clients SET connectionStatus = :status, lastSyncTime = :syncTime WHERE id = :id")
    suspend fun updateConnectionStatus(id: String, status: String, syncTime: Long)

    @Delete
    suspend fun deleteClient(client: ClientAccount)

    @Query("DELETE FROM clients WHERE id = :id")
    suspend fun deleteClientById(id: String)

    @Query("SELECT COUNT(*) FROM clients")
    suspend fun getClientCountDirect(): Int
}
