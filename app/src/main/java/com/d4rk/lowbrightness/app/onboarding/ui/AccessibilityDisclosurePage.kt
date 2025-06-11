package com.d4rk.lowbrightness.app.onboarding.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.d4rk.android.libs.apptoolkit.core.ui.components.spacers.LargeVerticalSpacer
import com.d4rk.android.libs.apptoolkit.core.ui.components.spacers.MediumHorizontalSpacer
import com.d4rk.android.libs.apptoolkit.core.utils.constants.ui.SizeConstants
import com.d4rk.lowbrightness.R

@Composable
fun AccessibilityDisclosurePage() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(SizeConstants.LargeSize)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Filled.Visibility,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )

        LargeVerticalSpacer()

        Text(
            text = stringResource(id = R.string.onboarding_accessibility_title),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        LargeVerticalSpacer()

        Text(
            text = stringResource(id = R.string.onboarding_accessibility_intro),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )

        LargeVerticalSpacer()

        Text(
            text = stringResource(id = R.string.onboarding_accessibility_what_it_means),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.Start)
        )

        DisclosurePoint(
            icon = Icons.Filled.CheckCircle,
            text = stringResource(id = R.string.onboarding_accessibility_benefit_comfort)
        )
        DisclosurePoint(
            icon = Icons.Filled.PrivacyTip,
            text = stringResource(id = R.string.onboarding_accessibility_benefit_no_data)
        )
        DisclosurePoint(
            icon = Icons.Filled.Settings,
            text = stringResource(id = R.string.onboarding_accessibility_benefit_control)
        )
    }
}

@Composable
private fun DisclosurePoint(icon: ImageVector, text: String) {
    Row(
        verticalAlignment = Alignment.Top,
        modifier = Modifier.padding(vertical = SizeConstants.ExtraSmallSize + SizeConstants.ExtraTinySize)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(top = SizeConstants.ExtraTinySize)
        )
        MediumHorizontalSpacer()
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}