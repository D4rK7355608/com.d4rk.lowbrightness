package com.d4rk.lowbrightness.app.brightness.ui.components.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.d4rk.android.libs.apptoolkit.core.utils.constants.ui.SizeConstants
import com.d4rk.lowbrightness.R
import com.d4rk.lowbrightness.app.brightness.ui.components.alphaRange

@Composable
fun AlphaDialog(
    initColor: Color,
    initAlpha: Float = initColor.alpha,
    onDismissRequest: () -> Unit,
    onAlphaSelected: (alpha: Float) -> Unit
) {
    val (r, g, b) = initColor
    var alpha: Float by remember { mutableFloatStateOf(initAlpha) }
    var color: Color by remember { mutableStateOf(initColor) }
    AlertDialog(
        onDismissRequest = onDismissRequest,
        icon = {
            Icon(imageVector = Icons.Default.Opacity, contentDescription = null)
        },
        title = {
            Text(text = stringResource(id = R.string.alpha_dialog_title))
        },
        confirmButton = {
            TextButton(onClick = {
                onAlphaSelected(alpha)
                onDismissRequest()
            }) {
                Text(text = stringResource(id = R.string.ok))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(text = stringResource(id = R.string.cancel))
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(70.dp)
                        .background(color = color, shape = RoundedCornerShape(SizeConstants.LargeIncreasedSize))
                        .wrapContentHeight(),
                    text = String.format("%.1f", alpha * 100) + "%",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        shadow = Shadow(
                            color = Color.Black,
                            offset = Offset(4f, 4f),
                            blurRadius = 10f
                        )
                    ),
                    textAlign = TextAlign.Center,
                    color = Color.White
                )
                Slider(
                    modifier = Modifier.padding(top = SizeConstants.SmallSize + SizeConstants.ExtraTinySize),
                    value = alpha,
                    valueRange = alphaRange,
                    onValueChange = {
                        alpha = it
                        color = Color(red = r, green = g, blue = b, alpha = alpha)
                    }
                )
            }
        }
    )
}