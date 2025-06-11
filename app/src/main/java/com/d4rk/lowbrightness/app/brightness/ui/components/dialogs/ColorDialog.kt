package com.d4rk.lowbrightness.app.brightness.ui.components.dialogs

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.d4rk.android.libs.apptoolkit.core.ui.components.dialogs.BasicAlertDialog
import com.d4rk.lowbrightness.R
import com.godaddy.android.colorpicker.ClassicColorPicker
import com.godaddy.android.colorpicker.HsvColor

@Composable
fun ColorDialog(
    initColor: Color,
    onDismissRequest: () -> Unit,
    onColorSelected: (color: Color) -> Unit
) {
    var color: Color by remember { mutableStateOf<Color>(value = initColor) }

    BasicAlertDialog(
        onDismiss = onDismissRequest,
        onConfirm = {
            onColorSelected(color)
            onDismissRequest()
        },
        icon = Icons.Default.ColorLens,
        title = stringResource(id = R.string.color_dialog_title),
        content = {
            ClassicColorPicker(
                showAlphaBar = false,
                color = HsvColor.from(initColor),
                onColorChanged = { hsv: HsvColor ->
                    color = hsv.toColor()
                }
            )
        }
    )
}