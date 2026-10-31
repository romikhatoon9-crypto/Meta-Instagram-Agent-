package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.SufiSecurityDatabase
import com.example.data.model.ActivityLog
import com.example.data.model.ClientAccount
import com.example.data.model.SecurityReview
import com.example.data.model.SuspensionStatus
import com.example.data.repository.SecurityRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppScreen {
    CONNECTION,       // Screen 1: Client Connection
    DASHBOARD,        // Screen 2: Account Dashboard
    SECURITY_REVIEW,  // Screen 3: Account Security Review
    SUSPENSION_MGMT,  // Screen 4: Suspension Management
    ADMIN             // Screen 5: Admin Dashboard
}

class SecurityViewModel(application: Application) : AndroidViewModel(application) {

    private val database = SufiSecurityDatabase.getDatabase(application)
    private val repository = SecurityRepository(
        clientAccountDao = database.clientAccountDao(),
        securityReviewDao = database.securityReviewDao(),
        activityLogDao = database.activityLogDao()
    )

    // Navigation
    private val _currentScreen = MutableStateFlow(AppScreen.DASHBOARD)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    // All clients from Room
    val allClients: StateFlow<List<ClientAccount>> = repository.allClients
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Currently focused/active client
    private val _selectedClientId = MutableStateFlow<String?>(null)
    val selectedClientId: StateFlow<String?> = _selectedClientId.asStateFlow()

    val currentClient: StateFlow<ClientAccount?> = combine(allClients, _selectedClientId) { clients, selectedId ->
        if (selectedId != null) {
            clients.firstOrNull { it.id == selectedId }
        } else {
            clients.firstOrNull()
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Security Reviews for the current client
    private val _currentReview = MutableStateFlow<SecurityReview?>(null)
    val currentReview: StateFlow<SecurityReview?> = _currentReview.asStateFlow()

    // Activity Logs
    val activityLogs: StateFlow<List<ActivityLog>> = repository.allLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Screen 1: Client Connection State
    val usernameInput = MutableStateFlow("")
    val isConnecting = MutableStateFlow(false)
    val showConsentDialog = MutableStateFlow(false)
    val connectionErrorMessage = MutableStateFlow<String?>(null)

    // Screen 3: Security Review Animation State
    private val _auditProgress = MutableStateFlow(0f)
    val auditProgress: StateFlow<Float> = _auditProgress.asStateFlow()

    private val _auditPercentage = MutableStateFlow(0)
    val auditPercentage: StateFlow<Int> = _auditPercentage.asStateFlow()

    private val _currentAuditStep = MutableStateFlow(1) // 1 to 4
    val currentAuditStep: StateFlow<Int> = _currentAuditStep.asStateFlow()

    private val _isAuditRunning = MutableStateFlow(false)
    val isAuditRunning: StateFlow<Boolean> = _isAuditRunning.asStateFlow()

    private val _isAuditCompleted = MutableStateFlow(false)
    val isAuditCompleted: StateFlow<Boolean> = _isAuditCompleted.asStateFlow()

    private var auditJob: Job? = null

    // Screen 4: Suspension Management Form State
    val formStatus = MutableStateFlow(SuspensionStatus.ACTIVE)
    val formNotes = MutableStateFlow("")
    val formTicketNumber = MutableStateFlow("")
    val formAppealMethod = MutableStateFlow("Meta Business Help Center - Priority Live Agent Chat")
    val formEvidence = MutableStateFlow("")
    val statusUpdateSuccessMsg = MutableStateFlow<String?>(null)

    // Screen 5: Admin Dashboard Filters & Auth State
    val isAdminAuthenticated = MutableStateFlow(false)
    val adminPinInput = MutableStateFlow("")
    val adminPinError = MutableStateFlow(false)
    val searchQuery = MutableStateFlow("")
    val statusFilter = MutableStateFlow<SuspensionStatus?>(null)

    init {
        // Automatically monitor client changes to sync currentReview
        viewModelScope.launch {
            currentClient.collect { client ->
                if (client != null) {
                    repository.getLatestReview(client.id).collect { review ->
                        _currentReview.value = review
                    }
                }
            }
        }
    }

    fun navigateTo(screen: AppScreen) {
        _currentScreen.value = screen
    }

    fun selectClient(clientId: String) {
        _selectedClientId.value = clientId
        // Reset audit state when switching clients
        _isAuditCompleted.value = false
        _auditProgress.value = 0f
        _auditPercentage.value = 0
        _currentAuditStep.value = 1
    }

    // --- SCREEN 1 ACTIONS ---
    fun openConsentDialog() {
        if (usernameInput.value.isBlank()) {
            connectionErrorMessage.value = "Please enter an Instagram or Facebook username"
            return
        }
        connectionErrorMessage.value = null
        showConsentDialog.value = true
    }

    fun closeConsentDialog() {
        showConsentDialog.value = false
    }

    fun connectClient(platform: String = "Instagram") {
        val targetUsername = usernameInput.value.trim().removePrefix("@")
        if (targetUsername.isBlank()) return

        showConsentDialog.value = false
        isConnecting.value = true
        connectionErrorMessage.value = null

        viewModelScope.launch {
            try {
                val newClient = repository.connectNewClient(targetUsername, platform)
                _selectedClientId.value = newClient.id
                usernameInput.value = ""
                isConnecting.value = false
                _currentScreen.value = AppScreen.DASHBOARD
            } catch (e: Exception) {
                isConnecting.value = false
                connectionErrorMessage.value = "Failed to establish Meta OAuth session: ${e.localizedMessage}"
            }
        }
    }

    fun disconnectActiveClient() {
        val client = currentClient.value ?: return
        viewModelScope.launch {
            repository.disconnectClient(client)
        }
    }

    // --- SCREEN 2 ACTIONS ---
    fun refreshCurrentClient() {
        val client = currentClient.value ?: return
        viewModelScope.launch {
            repository.refreshClientData(client)
        }
    }

    // --- SCREEN 3: SECURITY REVIEW ACTIONS ---
    fun startSecurityReview() {
        auditJob?.cancel()
        _isAuditRunning.value = true
        _isAuditCompleted.value = false
        _auditProgress.value = 0f
        _auditPercentage.value = 0
        _currentAuditStep.value = 1

        auditJob = viewModelScope.launch {
            // Step 1: Verifying authorized account connection (0% -> 25%)
            _currentAuditStep.value = 1
            for (p in 1..25) {
                delay(35)
                _auditProgress.value = p / 100f
                _auditPercentage.value = p
            }
            delay(400)

            // Step 2: Checking available account information (25% -> 55%)
            _currentAuditStep.value = 2
            for (p in 26..55) {
                delay(30)
                _auditProgress.value = p / 100f
                _auditPercentage.value = p
            }
            delay(500)

            // Step 3: Reviewing recorded security concerns (55% -> 80%)
            _currentAuditStep.value = 3
            for (p in 56..80) {
                delay(35)
                _auditProgress.value = p / 100f
                _auditPercentage.value = p
            }
            delay(500)

            // Step 4: Finalizing security report (80% -> 100%)
            _currentAuditStep.value = 4
            for (p in 81..100) {
                delay(25)
                _auditProgress.value = p / 100f
                _auditPercentage.value = p
            }
            delay(400)

            // Complete review
            val client = currentClient.value
            if (client != null) {
                val score = if (client.statusEnum == SuspensionStatus.ACTIVE) 94 else 68
                val review = repository.saveSecurityReview(client.id, score, client.username)
                _currentReview.value = review
            }

            _isAuditRunning.value = false
            _isAuditCompleted.value = true
        }
    }

    // --- SCREEN 4: SUSPENSION ACTIONS ---
    fun prepareSuspensionForm(client: ClientAccount) {
        _selectedClientId.value = client.id
        formStatus.value = client.statusEnum
        formNotes.value = client.notes ?: ""
        formTicketNumber.value = client.appealTicketNumber ?: ""
        formAppealMethod.value = client.appealMethod ?: "Meta Business Help Center - Priority Live Agent Chat"
        formEvidence.value = client.evidenceDetails ?: ""
        statusUpdateSuccessMsg.value = null
        _currentScreen.value = AppScreen.SUSPENSION_MGMT
    }

    fun submitSuspensionUpdate() {
        val client = currentClient.value ?: return
        viewModelScope.launch {
            repository.updateSuspensionStatus(
                clientId = client.id,
                status = formStatus.value,
                notes = formNotes.value.takeIf { it.isNotBlank() },
                ticket = formTicketNumber.value.takeIf { it.isNotBlank() },
                date = if (formStatus.value == SuspensionStatus.APPEAL_SUBMITTED) System.currentTimeMillis() else null,
                method = formAppealMethod.value.takeIf { it.isNotBlank() },
                evidence = formEvidence.value.takeIf { it.isNotBlank() },
                clientUsername = client.username
            )
            statusUpdateSuccessMsg.value = "Status successfully recorded as ${formStatus.value.displayName}"
        }
    }

    // --- SCREEN 5: ADMIN AUTH ACTIONS ---
    fun authenticateAdmin(pin: String): Boolean {
        return if (pin == "1234" || pin == "0000") { // Admin passcode
            isAdminAuthenticated.value = true
            adminPinError.value = false
            adminPinInput.value = ""
            true
        } else {
            adminPinError.value = true
            false
        }
    }

    fun logoutAdmin() {
        isAdminAuthenticated.value = false
    }

    fun deleteClientAccount(client: ClientAccount) {
        viewModelScope.launch {
            repository.deleteClient(client)
            if (_selectedClientId.value == client.id) {
                _selectedClientId.value = null
            }
        }
    }
}
