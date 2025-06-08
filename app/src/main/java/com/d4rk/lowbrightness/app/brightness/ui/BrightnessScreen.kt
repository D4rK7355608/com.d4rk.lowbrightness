package com.d4rk.lowbrightness.app.brightness.ui

import android.content.Intent
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.d4rk.android.libs.apptoolkit.core.ui.components.modifiers.bounceClick
import com.d4rk.lowbrightness.R
import com.d4rk.lowbrightness.app.brightness.domain.ext.activity
import com.d4rk.lowbrightness.app.brightness.domain.ext.plus
import com.d4rk.lowbrightness.app.brightness.domain.ext.requestAllPermissions
import com.d4rk.lowbrightness.app.brightness.domain.services.isAccessibilityServiceRunning
import com.d4rk.lowbrightness.app.brightness.ui.components.alphaRange
import com.d4rk.lowbrightness.app.brightness.ui.components.calculatedColor
import com.d4rk.lowbrightness.app.brightness.ui.components.dialogs.ColorDialog
import com.d4rk.lowbrightness.app.brightness.ui.components.dialogs.requestAllPermissionsWithAccessibilityAndShow
import com.d4rk.lowbrightness.app.brightness.ui.components.screenAlpha
import com.d4rk.lowbrightness.app.brightness.ui.components.screenColor
import com.d4rk.lowbrightness.app.brightness.ui.ScheduleCard
import com.d4rk.lowbrightness.ui.component.showToast

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrightnessScreen(paddingValues: PaddingValues) {
    val context = LocalContext.current

    val startForResult = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (isAccessibilityServiceRunning(context)) {
            context.activity.requestAllPermissions()
        } else {
            context.getString(R.string.no_accessibility_permission).showToast()
        }
    }
    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp) + paddingValues
    ) {
        item { IntensityCard() }
        item { ColorCard() }
        item {
            ActionsCard(
                onRunNightScreenClick = { requestAllPermissionsWithAccessibilityAndShow(context) },
                onRequestPermissionsClick = {
                    if (isAccessibilityServiceRunning(context)) {
                        context.activity.requestAllPermissions()
                    } else {
                        startForResult.launch(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
                    }
                }
            )
        }
        item { ScheduleCard() }
        item { BottomImage() }
    }
}

@Composable
private fun HomeItem(
    imageVector: ImageVector,
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .bounceClick()
            .clip(CircleShape)
            .clickable { onClick() }
            .padding(vertical = 16.dp, horizontal = 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier.size(24.dp),
            imageVector = imageVector,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
        Text(
            modifier = Modifier.padding(start = 20.dp),
            text = text,
            style = MaterialTheme.typography.titleMedium,
            maxLines = 1
        )
    }
}

@Composable
private fun ActionsCard(
    onRunNightScreenClick: () -> Unit,
    onRequestPermissionsClick: () -> Unit
) {
    Card(
        shape = MaterialTheme.shapes.extraLarge,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
    ) {
        Column {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(12.dp),
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
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                HomeItem(
                    imageVector = Icons.Default.DoneAll,
                    text = stringResource(id = R.string.request_permissions),
                    onClick = onRequestPermissionsClick
                )
            }
        }
    }
}

@Composable
private fun IntensityCard() {
    var alpha by remember { mutableFloatStateOf(screenAlpha) }
    Card(
        shape = MaterialTheme.shapes.extraLarge, modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
    ) {
        Column {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(12.dp), contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(id = R.string.intensity),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = stringResource(id = R.string.summary_brightness),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )
                Slider(
                    modifier = Modifier.padding(top = 8.dp).bounceClick(), value = alpha, onValueChange = {
                        alpha = it
                        screenAlpha = it
                    }, valueRange = alphaRange
                )
            }
        }
    }
}

@Composable
private fun ColorCard() {
    var showDialog by remember { mutableStateOf(false) }
    Card(
        shape = MaterialTheme.shapes.extraLarge, modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
    ) {
        Column {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(12.dp), contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(id = R.string.customize_overlay_color),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp), verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        modifier = Modifier.weight(1f),
                        text = stringResource(id = R.string.summary_filter),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    IconButton(
                        modifier = Modifier.bounceClick(),
                        onClick = { showDialog = true }) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(
                                    color = Color(calculatedColor),
                                    shape = RoundedCornerShape(percent = 50)
                                )
                        )
                    }
                }
            }
        }
    }
    if (showDialog) {
        ColorDialog(
            initColor = Color(screenColor),
            onDismissRequest = { showDialog = false },
            onColorSelected = { color ->
                screenColor = color.toArgb()
            })
    }
}

@Composable
private fun BottomImage() {
    Card(
        shape = MaterialTheme.shapes.extraLarge, modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
            .height(192.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.il_brightness),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}