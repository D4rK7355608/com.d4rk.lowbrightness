package com.d4rk.lowbrightness.app.brightness.ui.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter

enum class NsIconButtonStyle {
    Normal, Filled, FilledTonal, Outlined
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NsIconButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    painter: Painter,
    tint: Color? = null,
    style: NsIconButtonStyle = NsIconButtonStyle.Normal,
    contentDescription: String? = null,
    enabled: Boolean = true,
    colors: IconButtonColors? = null,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    val iconButton: @Composable (modifier: Modifier) -> Unit = {
        val icon: @Composable () -> Unit = {
            Icon(
                painter = painter,
                tint = tint ?: LocalContentColor.current,
                contentDescription = contentDescription,
            )
        }
        when (style) {
            NsIconButtonStyle.Normal -> IconButton(
                modifier = it,
                onClick = onClick,
                enabled = enabled,
                colors = colors ?: IconButtonDefaults.iconButtonColors(),
                interactionSource = interactionSource,
                content = icon,
            )
            NsIconButtonStyle.Filled -> FilledIconButton(
                modifier = it,
                onClick = onClick,
                enabled = enabled,
                colors = colors ?: IconButtonDefaults.filledIconButtonColors(),
                interactionSource = interactionSource,
                content = icon,
            )
            NsIconButtonStyle.FilledTonal -> FilledTonalIconButton(
                modifier = it,
                onClick = onClick,
                enabled = enabled,
                colors = colors ?: IconButtonDefaults.filledTonalIconButtonColors(),
                interactionSource = interactionSource,
                content = icon,
            )
            NsIconButtonStyle.Outlined -> OutlinedIconButton(
                modifier = it,
                onClick = onClick,
                enabled = enabled,
                colors = colors ?: IconButtonDefaults.outlinedIconButtonColors(),
                interactionSource = interactionSource,
                content = icon,
            )
        }
    }

    if (contentDescription.isNullOrEmpty()) {
        iconButton(modifier)
    }
}

@Composable
fun NsIconButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    imageVector: ImageVector,
    tint: Color? = null,
    style: NsIconButtonStyle = NsIconButtonStyle.Normal,
    contentDescription: String? = null,
    enabled: Boolean = true,
    colors: IconButtonColors? = null,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    NsIconButton(
        modifier = modifier,
        onClick = onClick,
        painter = rememberVectorPainter(image = imageVector),
        style = style,
        contentDescription = contentDescription,
        tint = tint,
        enabled = enabled,
        colors = colors,
        interactionSource = interactionSource,
    )
}
