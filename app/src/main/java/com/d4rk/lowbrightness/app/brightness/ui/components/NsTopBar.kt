package com.d4rk.lowbrightness.app.brightness.ui.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.largeTopAppBarColors
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource

enum class NsTopBarStyle {
    Small, Large
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NsTopBar(
    style: NsTopBarStyle = NsTopBarStyle.Small,
    title: @Composable () -> Unit,
    navigationIcon: @Composable () -> Unit = { BackIcon() },
    actions: @Composable RowScope.() -> Unit = {},
    scrollBehavior: TopAppBarScrollBehavior? = null,
) {
    val colors = when (style) {
        NsTopBarStyle.Small -> topAppBarColors()
        NsTopBarStyle.Large -> largeTopAppBarColors()
    }
    when (style) {
        NsTopBarStyle.Small -> {
            TopAppBar(
                title = title,
                navigationIcon = navigationIcon,
                actions = actions,
                colors = colors,
                scrollBehavior = scrollBehavior
            )
        }
        NsTopBarStyle.Large -> {
            LargeTopAppBar(
                title = title,
                navigationIcon = navigationIcon,
                actions = actions,
                colors = colors,
                scrollBehavior = scrollBehavior
            )
        }
    }
}

@Composable
fun TopBarIcon(
    imageVector: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    tint: Color = LocalContentColor.current,
    contentDescription: String?,
) {
    NsIconButton(
        modifier = modifier,
        imageVector = imageVector,
        tint = tint,
        contentDescription = contentDescription,
        onClick = onClick
    )
}

@Composable
fun BackIcon(onClick: () -> Unit = {}) {
    TopBarIcon(
        imageVector = Icons.AutoMirrored.Rounded.ArrowBack ,
        contentDescription = stringResource(id = com.d4rk.android.libs.apptoolkit.R.string.back_button_content_description) ,
        onClick = onClick
    )
}