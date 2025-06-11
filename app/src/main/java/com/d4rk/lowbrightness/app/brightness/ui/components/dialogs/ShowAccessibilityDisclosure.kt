package com.d4rk.lowbrightness.app.brightness.ui.components.dialogs

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PermDeviceInformation
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.d4rk.android.libs.apptoolkit.core.ui.components.dialogs.BasicAlertDialog
import com.d4rk.lowbrightness.R

@Composable
fun ShowAccessibilityDisclosure(
    onDismissRequest: () -> Unit,
    onContinue: () -> Unit
) {
    BasicAlertDialog(
        onDismiss = onDismissRequest,
        onConfirm = onContinue,
        onCancel = onDismissRequest,
        icon = Icons.Outlined.PermDeviceInformation,
        title = stringResource(id = R.string.accessibility_permission_disclosure_title),
        content = {
            Text(text = stringResource(id = R.string.accessibility_permission_disclosure_message))
        },
        confirmButtonText = stringResource(id = com.d4rk.android.libs.apptoolkit.R.string.button_continue),
    )
}