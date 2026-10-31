package com.example

import com.example.data.model.ClientAccount
import com.example.data.model.SuspensionStatus
import com.example.data.repository.MetaAuthService
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class SufiSecurityModelTest {

    @Test
    fun suspensionStatus_parsingWorksCorrectly() {
        assertEquals(SuspensionStatus.ACTIVE, SuspensionStatus.fromString("ACTIVE"))
        assertEquals(SuspensionStatus.SUSPENDED, SuspensionStatus.fromString("suspended"))
        assertEquals(SuspensionStatus.APPEAL_SUBMITTED, SuspensionStatus.fromString("APPEAL_SUBMITTED"))
        assertEquals(SuspensionStatus.ACTIVE, SuspensionStatus.fromString("unknown_status"))
    }

    @Test
    fun metaAuthUrl_containsRequiredSecurityScopes() {
        val service = MetaAuthService()
        val url = service.buildMetaAuthUrl()

        assertTrue(url.contains("instagram_basic"))
        assertTrue(url.contains("response_type=code"))
        assertTrue(url.contains("sufisecurity%3A%2F%2Foauth-callback") || url.contains("sufisecurity://oauth-callback"))
        assertFalse(url.contains("password"))
    }

    @Test
    fun clientAccount_connectionState_matchesStatus() {
        val client = ClientAccount(
            id = "c1",
            username = "test_user",
            fullName = "Test User",
            metaAccountId = "178410001",
            connectionStatus = "CONNECTED",
            suspensionStatus = "ACTIVE"
        )
        assertTrue(client.isConnected)
        assertEquals(SuspensionStatus.ACTIVE, client.statusEnum)

        val disconnected = client.copy(connectionStatus = "DISCONNECTED")
        assertFalse(disconnected.isConnected)
    }
}
