package com.example.data.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.StatusActive
import com.example.ui.theme.StatusAppealSubmitted
import com.example.ui.theme.StatusDisabled
import com.example.ui.theme.StatusRestricted
import com.example.ui.theme.StatusSuspended
import com.example.ui.theme.StatusUnderReview

enum class SuspensionStatus(
    val displayName: String,
    val color: Color,
    val description: String
) {
    ACTIVE("Active", StatusActive, "Account is in good standing with valid Meta API credentials."),
    DISABLED("Disabled", StatusDisabled, "Client reported account disabled by Meta enforcement."),
    SUSPENDED("Suspended", StatusSuspended, "Client reported full temporary or permanent suspension."),
    RESTRICTED("Restricted", StatusRestricted, "Client reported partial action restrictions (e.g. messaging, advertising)."),
    UNDER_REVIEW("Under Review", StatusUnderReview, "Security audit in progress or internal compliance assessment."),
    APPEAL_SUBMITTED("Appeal Submitted", StatusAppealSubmitted, "Official appeal submitted via Meta Business Support / Help Center.");

    companion object {
        fun fromString(value: String): SuspensionStatus {
            return entries.firstOrNull { it.name.equals(value, ignoreCase = true) } ?: ACTIVE
        }
    }
}
