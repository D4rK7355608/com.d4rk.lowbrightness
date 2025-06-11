package com.d4rk.lowbrightness.app.brightness.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.d4rk.android.libs.apptoolkit.core.utils.constants.ui.SizeConstants
import com.d4rk.lowbrightness.R

@Composable
fun ActionsCard(
    onRunNightScreenClick: () -> Unit,
    onRequestPermissionsClick: () -> Unit
) {
    Card(
        shape = MaterialTheme.shapes.extraLarge,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = SizeConstants.SmallSize + SizeConstants.ExtraTinySize)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = MaterialTheme.colorScheme.primary)
                    .padding(SizeConstants.MediumSize),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(id = R.string.actions),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }

            Column {
                HomeItem(
                    imageVector = Icons.Default.PlayArrow,
                    text = stringResource(id = R.string.run_night_screen),
                    onClick = onRunNightScreenClick
                )
                HorizontalDivider(
                    modifier = Modifier
                        .padding(horizontal = SizeConstants.LargeSize)
                )
                HomeItem(
                    imageVector = Icons.Default.DoneAll,
                    text = stringResource(id = R.string.request_permissions),
                    onClick = onRequestPermissionsClick
                )
            }
        }
    }
}