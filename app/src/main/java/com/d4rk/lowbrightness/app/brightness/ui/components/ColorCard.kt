package com.d4rk.lowbrightness.app.brightness.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.d4rk.android.libs.apptoolkit.core.ui.components.modifiers.bounceClick
import com.d4rk.android.libs.apptoolkit.core.utils.constants.ui.SizeConstants
import com.d4rk.lowbrightness.R
import com.d4rk.lowbrightness.app.brightness.ui.components.dialogs.ColorDialog

@Composable
fun ColorCard() {
    val outlineColor = MaterialTheme.colorScheme.outline
    var showDialog by remember { mutableStateOf(false) }
    Card(
        shape = MaterialTheme.shapes.extraLarge, modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = SizeConstants.SmallSize + SizeConstants.ExtraTinySize)
    ) {
        Column {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(SizeConstants.MediumSize), contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(id = R.string.customize_overlay_color),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }

            Column(modifier = Modifier.padding(SizeConstants.LargeSize)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = SizeConstants.SmallSize), verticalAlignment = Alignment.CenterVertically
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
                                .size(size = SizeConstants.ExtraExtraLargeSize)
                                .background(
                                    color = Color(color = calculatedColor),
                                    shape = RoundedCornerShape(percent = 50),
                                )
                                .border(
                                    width = SizeConstants.ExtraTinySize,
                                    color = outlineColor, shape = RoundedCornerShape(percent = 50)
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