package com.d4rk.lowbrightness.ui.screen.home

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.d4rk.lowbrightness.R
import com.d4rk.lowbrightness.ext.activity
import com.d4rk.lowbrightness.ext.plus
import com.d4rk.lowbrightness.ext.requestAllPermissions
import com.d4rk.lowbrightness.ui.component.NsTopBar
import com.d4rk.lowbrightness.ui.component.NsTopBarStyle
import com.d4rk.lowbrightness.ui.component.alphaRange
import com.d4rk.lowbrightness.ui.component.calculatedColor
import com.d4rk.lowbrightness.ui.component.dialog.requestAllPermissionsWithAccessibilityAndShow
import com.d4rk.lowbrightness.ui.component.dialog.ColorDialog
import com.d4rk.lowbrightness.ui.component.showToast
import com.d4rk.lowbrightness.ui.component.screenAlpha
import com.d4rk.lowbrightness.ui.component.screenColor
import com.d4rk.lowbrightness.ui.local.LocalNavController
import com.d4rk.lowbrightness.ui.screen.about.ABOUT_SCREEN_ROUTE
import com.d4rk.lowbrightness.ui.screen.settings.SETTINGS_SCREEN_ROUTE
import com.d4rk.lowbrightness.ui.service.isAccessibilityServiceRunning

const val HOME_SCREEN_ROUTE = "homeScreen"

@Composable
fun HomeScreen() {
    val navController = LocalNavController.current
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            NsTopBar(
                style = NsTopBarStyle.Large,
                title = {
                    Text(text = stringResource(R.string.app_name))
                },
                navigationIcon = {},
                scrollBehavior = scrollBehavior
            )
        }
    ) { innerPadding ->
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
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp) + innerPadding
        ) {
            item { IntensityCard() }
            item { ColorCard() }
            item {
                HomeItem(
                    imageVector = Icons.Default.PlayArrow,
                    text = stringResource(id = R.string.run_night_screen),
                ) { requestAllPermissionsWithAccessibilityAndShow(context) }
            }
            item {
                HomeItem(
                    imageVector = Icons.Default.DoneAll,
                    text = stringResource(id = R.string.request_permissions),
                ) {
                    if (isAccessibilityServiceRunning(context)) {
                        context.activity.requestAllPermissions()
                    } else {
                        startForResult.launch(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
                    }
                }
            }
            item {
                HomeItem(
                    imageVector = Icons.Default.Settings,
                    text = stringResource(id = R.string.settings),
                ) { navController.navigate(SETTINGS_SCREEN_ROUTE) }
            }
            item {
                HomeItem(
                    imageVector = Icons.Default.Info,
                    text = stringResource(id = R.string.about),
                ) { navController.navigate(ABOUT_SCREEN_ROUTE) }
            }
            item { BottomImage() }
        }
    }
}

@Composable
private fun HomeItem(imageVector: ImageVector, text: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier.padding(vertical = 10.dp),
        shape = RoundedCornerShape(percent = 100)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .padding(30.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier.size(30.dp),
                imageVector = imageVector,
                contentDescription = null
            )
            Text(
                modifier = Modifier.padding(start = 25.dp),
                text = text,
                style = MaterialTheme.typography.titleLarge,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun IntensityCard() {
    var alpha by remember { mutableFloatStateOf(screenAlpha) }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = stringResource(id = R.string.intensity),
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = stringResource(id = R.string.summary_brightness),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 8.dp)
            )
            Slider(
                modifier = Modifier.padding(top = 8.dp),
                value = alpha,
                onValueChange = {
                    alpha = it
                    screenAlpha = it
                },
                valueRange = alphaRange
            )
        }
    }
}

@Composable
private fun ColorCard() {
    var showDialog by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = stringResource(id = R.string.customize_overlay_color),
                style = MaterialTheme.typography.titleLarge
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { showDialog = true }) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                color = Color(calculatedColor),
                                shape = RoundedCornerShape(percent = 50)
                            )
                    )
                }
                Text(
                    modifier = Modifier.padding(start = 16.dp),
                    text = stringResource(id = R.string.summary_filter),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
    if (showDialog) {
        ColorDialog(
            initColor = Color(screenColor),
            onDismissRequest = { showDialog = false },
            onColorSelected = { color ->
                screenColor = color.toArgb()
            }
        )
    }
}

@Composable
private fun BottomImage() {
    Card(
        modifier = Modifier
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