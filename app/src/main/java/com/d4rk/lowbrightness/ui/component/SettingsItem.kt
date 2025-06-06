package com.d4rk.lowbrightness.ui.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

val LocalUseColorfulIcon = compositionLocalOf { false }

@Composable
fun ColorSettingsItem(
    icon: ImageVector,
    text: String,
    description: String? = null,
    onClick: (() -> Unit)? = null,
    initColor: Color,
) {
    ColorSettingsItem(
        icon = rememberVectorPainter(image = icon),
        text = text,
        description = description,
        onClick = onClick,
        initColor = initColor,
    )
}

@Composable
fun ColorSettingsItem(
    icon: Painter,
    text: String,
    description: String? = null,
    onClick: (() -> Unit)? = null,
    initColor: Color,
) {
    BaseSettingsItem(
        icon = icon,
        text = text,
        descriptionText = description,
        onClick = onClick
    ) {
        IconButton(onClick = { onClick?.invoke() }) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        color = initColor,
                        shape = RoundedCornerShape(50.dp)
                    )
            )
        }
    }
}

@Composable
fun BaseSettingsItem(
    modifier: Modifier = Modifier,
    icon: Painter,
    text: String,
    descriptionText: String? = null,
    enabled: Boolean = true,
    onClick: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null,
    content: (@Composable () -> Unit)? = null
) {
    BaseSettingsItem(
        modifier = modifier,
        icon = icon,
        text = text,
        description = if (descriptionText != null) {
            {
                Text(
                    modifier = Modifier.padding(top = 5.dp),
                    text = descriptionText,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        } else null,
        enabled = enabled,
        onClick = if (enabled) onClick else null,
        onLongClick = if (enabled) onLongClick else null,
        content = content,
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BaseSettingsItem(
    modifier: Modifier = Modifier,
    icon: Painter,
    text: String,
    description: (@Composable () -> Unit)? = null,
    enabled: Boolean = true,
    onClick: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null,
    content: (@Composable () -> Unit)? = null
) {
    CompositionLocalProvider(
        LocalContentColor provides if (enabled) {
            LocalContentColor.current
        } else {
            LocalContentColor.current.copy(alpha = 0.38f)
        },
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .run {
                    if (onClick != null && enabled) {
                        combinedClickable(onLongClick = onLongClick) { onClick() }
                    } else this
                }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (LocalUseColorfulIcon.current) {
                Image(
                    modifier = Modifier
                        .padding(10.dp)
                        .size(24.dp),
                    painter = icon,
                    contentDescription = null
                )
            } else {
                Icon(
                    modifier = Modifier
                        .padding(10.dp)
                        .size(24.dp),
                    painter = icon,
                    contentDescription = null,
                )
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 10.dp)
            ) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.titleLarge,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                )
                if (description != null) {
                    Box(modifier = Modifier.padding(top = 5.dp)) {
                        description.invoke()
                    }
                }
            }
            content?.let {
                Box(modifier = Modifier.padding(end = 5.dp)) { it.invoke() }
            }
        }
    }
}