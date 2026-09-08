package com.mudassar.notes.design

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun StatusDot(color: Color, modifier: Modifier = Modifier, size: Dp = 20.dp) {
    Canvas(modifier = modifier.size(size)) {
        drawCircle(color = color)
    }
}

@Composable
fun FullScreenLoader() {
    Box {
        Text(text = "Loading...", modifier = Modifier.align(Alignment.Center))
    }
}

@Composable
fun FullErrorScreen(message: String) {
    Box {
        Text(text = message, modifier = Modifier.align(Alignment.Center))
    }
}

@Composable
fun AppHeader(
    title: String?,
    modifier: Modifier = Modifier,
    onBack: (() -> Unit)? = null,
    trailingContent: (@Composable () -> Unit)? = null,
    content: (@Composable () -> Unit)? = null,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
    ) {
        Box(Modifier.fillMaxWidth()) {
            if (onBack != null) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }

            if (title != null) {
                Text(
                    text = title,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(top = 8.dp),
                    fontWeight = FontWeight.W600,
                )
            }

            if (trailingContent != null) {
                Box(modifier = Modifier.align(Alignment.CenterEnd)) {
                    trailingContent()
                }
            }
        }

        content?.invoke()
    }
}
