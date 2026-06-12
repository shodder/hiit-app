package me.hodders.hitt.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.hodders.hitt.ui.theme.TextPrimary
import me.hodders.hitt.ui.theme.TextSecondary

@Composable
fun CircularProgressTimer(
    progress: Float,
    color: Color,
    timeLeft: Int,
    modifier: Modifier = Modifier,
    size: Dp = 220.dp,
    label: String? = null,
    sublabel: String? = null
) {
    val strokeWidth = 12.dp

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.size(size)
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val stroke = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
            val diameter = this.size.minDimension - strokeWidth.toPx()
            val topLeft = Offset(
                (this.size.width - diameter) / 2f,
                (this.size.height - diameter) / 2f
            )
            val arcSize = Size(diameter, diameter)

            // Background track
            drawArc(
                color = Color.White.copy(alpha = 0.1f),
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = stroke
            )

            // Progress arc
            if (progress > 0f) {
                drawArc(
                    color = color,
                    startAngle = -90f,
                    sweepAngle = 360f * progress.coerceIn(0f, 1f),
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = stroke
                )
            }
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = timeLeft.toString(),
                fontSize = 64.sp,
                fontWeight = FontWeight.Bold,
                color = if (progress > 0f) color else TextPrimary,
                textAlign = TextAlign.Center
            )
            if (label != null) {
                Text(
                    text = label,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary,
                    textAlign = TextAlign.Center
                )
            }
            if (sublabel != null) {
                Text(
                    text = sublabel,
                    fontSize = 13.sp,
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

// --- Previews ---

@Preview(showBackground = true, backgroundColor = 0xFF0F0F14, name = "Timer ring — work")
@Composable
private fun CircularProgressWorkPreview() {
    me.hodders.hitt.ui.theme.HiitTheme {
        CircularProgressTimer(
            progress = 0.65f,
            color = Color(0xFFFF6B35),
            timeLeft = 14,
            label = "Burpees",
            modifier = Modifier.size(220.dp)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0F0F14, name = "Timer ring — rest")
@Composable
private fun CircularProgressRestPreview() {
    me.hodders.hitt.ui.theme.HiitTheme {
        CircularProgressTimer(
            progress = 0.3f,
            color = Color(0xFF4ECDC4),
            timeLeft = 7,
            label = "Jump Squats",
            modifier = Modifier.size(220.dp)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0F0F14, name = "Timer ring — with weight sublabel")
@Composable
private fun CircularProgressWeightPreview() {
    me.hodders.hitt.ui.theme.HiitTheme {
        CircularProgressTimer(
            progress = 0.5f,
            color = Color(0xFFF59E0B),
            timeLeft = 5,
            label = "Bicep Curls",
            sublabel = "heavy  •  16kg",
            modifier = Modifier.size(220.dp)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0F0F14, name = "Timer ring — idle (no progress)")
@Composable
private fun CircularProgressIdlePreview() {
    me.hodders.hitt.ui.theme.HiitTheme {
        CircularProgressTimer(
            progress = 0f,
            color = Color(0xFF666666),
            timeLeft = 0,
            modifier = Modifier.size(220.dp)
        )
    }
}
