package com.d4rk.lowbrightness.app.brightness.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.d4rk.android.libs.apptoolkit.core.utils.constants.ui.SizeConstants
import com.d4rk.lowbrightness.R

@Composable
fun BottomImage() {
    Card(
        shape = MaterialTheme.shapes.extraLarge, modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = SizeConstants.SmallSize + SizeConstants.ExtraTinySize)
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