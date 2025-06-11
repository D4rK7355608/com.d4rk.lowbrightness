package com.d4rk.lowbrightness.app.brightness.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import com.d4rk.android.libs.apptoolkit.core.ui.components.modifiers.bounceClick
import com.d4rk.android.libs.apptoolkit.core.ui.components.spacers.LargeIncreasedHorizontalSpacer
import com.d4rk.android.libs.apptoolkit.core.utils.constants.ui.SizeConstants

@Composable
fun HomeItem(
    imageVector: ImageVector,
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .bounceClick()
            .clip(shape = CircleShape)
            .clickable { onClick() }
            .padding(
                vertical = SizeConstants.LargeSize,
                horizontal = SizeConstants.LargeSize + SizeConstants.SmallSize
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier.size(size = SizeConstants.LargeSize + SizeConstants.SmallSize),
            imageVector = imageVector,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )

        LargeIncreasedHorizontalSpacer()

        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            maxLines = 1
        )
    }
}