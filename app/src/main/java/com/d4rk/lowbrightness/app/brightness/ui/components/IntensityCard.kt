package com.d4rk.lowbrightness.app.brightness.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.d4rk.android.libs.apptoolkit.core.ui.components.modifiers.bounceClick
import com.d4rk.android.libs.apptoolkit.core.utils.constants.ui.SizeConstants
import com.d4rk.lowbrightness.R

@Composable
fun IntensityCard() {
    var alpha by remember { mutableFloatStateOf(screenAlpha) }
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
                    text = stringResource(id = R.string.intensity),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }

            Column(modifier = Modifier.padding(SizeConstants.LargeSize)) {
                Text(
                    text = stringResource(id = R.string.summary_brightness),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = SizeConstants.SmallSize)
                )
                Slider(
                    modifier = Modifier.padding(top = SizeConstants.SmallSize).bounceClick(), value = alpha, onValueChange = {
                        alpha = it
                        screenAlpha = it
                    }, valueRange = alphaRange
                )
            }
        }
    }
}