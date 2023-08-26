package net.ddns.muchserver.levelgaugecompose.composables

import android.graphics.Typeface
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import net.ddns.muchserver.levelgaugecompose.AccelerometerViewModel
import net.ddns.muchserver.levelgaugecompose.MainActivity
import net.ddns.muchserver.levelgaugecompose.repository.THEME_LIGHT
import net.ddns.muchserver.levelgaugecompose.viewmodels.PreferenceViewModel

const val ACCELERATION_DUE_TO_GRAVITY = 9.81

@Composable
fun LevelGauge(
    accelerometerViewModel: AccelerometerViewModel,
    preferenceViewModel: PreferenceViewModel,
    activity: MainActivity
) {
    val verticalAcceleration = "%.3f".format(-1 * accelerometerViewModel.accelX)
    val horizontalAcceleration = "%.3f".format(accelerometerViewModel.accelY)
    val accelZ = "%.3f".format(accelerometerViewModel.accelZ)

    var theme = THEME_LIGHT
    preferenceViewModel.readFromDataStore.observe(activity) { colors ->
        theme = colors
    }

    val colorText = if(theme == THEME_LIGHT) Color.Black else Color.White

    val darkTheme = listOf(Color.Black, Color.DarkGray, Color.DarkGray)
    val lightTheme = listOf(Color(0xFFAAAAAA), Color(0xFFBBBBBB), Color.White)
    val brush = Brush.verticalGradient(
        colors = if(theme == THEME_LIGHT) lightTheme else darkTheme
    )
    val brushReverse = Brush.verticalGradient(
        colors = if(theme == THEME_LIGHT) darkTheme else lightTheme
    )

    val colorsLightHorizontal = listOf(Color(0xDD90CAF9), Color(0xDD1976D2), Color(0xDDE0E0E0), Color(0xDDE0E0E0))
    val colorsDarkHorizontal = listOf(Color(0xDDEF9A9A), Color(0xDDD32F2F), Color(0xDDE0E0E0), Color(0xDDE0E0E0))
    val brushBallHorizontal = Brush.verticalGradient(
        colors = if(theme == THEME_LIGHT) colorsLightHorizontal else colorsDarkHorizontal
    )

    val colorsLightVertical = listOf(Color(0x99A5D6A7), Color(0x99388E3C), Color(0x99E0E0E0), Color(0x99E0E0E0))
    val colorsDarkVertical = listOf(Color(0x99FFCC80), Color(0x99F57C00), Color(0x99E0E0E0), Color(0x99E0E0E0))
    val brushBallVertical = Brush.horizontalGradient(
        colors = if(theme == THEME_LIGHT) colorsLightVertical else colorsDarkVertical
    )

    Canvas(
        modifier = Modifier.fillMaxSize(),
        onDraw = {

            val radius = size.height / 10
            val edgeLeft = size.width / 20
            val edgeRight = size.width - edgeLeft
            val magnitudeHorizontal = edgeRight - size.width / 2 - radius
            val centerXCalculated = (size.width / 2) + (accelerometerViewModel.accelY / ACCELERATION_DUE_TO_GRAVITY) * magnitudeHorizontal
            val horizontalBubbleCenter = getClampedEnds(centerXCalculated.toFloat(), edgeLeft + radius, edgeRight - radius)

            val edgeTop = size.height / 20
            val edgeBottom = size.height - edgeTop
            val magnitudeVertical = edgeBottom - size.height / 2 - radius
            val centerYCalculated = (size.height / 2) + (accelerometerViewModel.accelX / ACCELERATION_DUE_TO_GRAVITY) * magnitudeVertical
            val verticalBubbleCenter = getClampedEnds(centerYCalculated.toFloat(), edgeTop + radius, edgeBottom - radius)

            val rectCornerRadius = 15f
            val yOffsetText = size.height / 15
            val xOffsetText = size.height / 12
            val textPaint = Paint().asFrameworkPaint().apply{
                isAntiAlias = true
                color = colorText.toArgb()
                typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
                textSize = size.height / 20
            }

            drawIntoCanvas {
                drawRect(
                    brush = brush,
                    size = size
                )
                it.nativeCanvas.drawText(
                    "X: $horizontalAcceleration",
                    xOffsetText,
                    yOffsetText,
                    textPaint
                )
                it.nativeCanvas.drawText(
                    "Y: $verticalAcceleration",
                    xOffsetText,
                    2 * yOffsetText,
                    textPaint
                )
                it.nativeCanvas.drawText(
                    "Z: $accelZ",
                    xOffsetText,
                    3 * yOffsetText,
                    textPaint
                )
                drawRoundRect(
                    brush = brushReverse,
                    size = Size(width = 2 * radius, size.height - 2 * edgeTop),
                    cornerRadius = CornerRadius(x = rectCornerRadius, y = rectCornerRadius),
                    topLeft = Offset(x = size.width / 2 - radius, y = edgeTop),
                    style = Stroke(width = 2.0f)
                )

                drawRoundRect(
                    brush = brushReverse,
                    size = Size(width = size.width - 2 * edgeLeft, height = 2 * radius),
                    cornerRadius = CornerRadius(x = rectCornerRadius, y = rectCornerRadius),
                    topLeft = Offset(x = edgeLeft, y = size.height / 2 - radius),
                    style = Stroke(width = 2.0f)
                )

                drawRoundRect(
                    brush = brush,
                    size = Size(width = 2 * radius, size.height - 2 * edgeTop),
                    cornerRadius = CornerRadius(x = rectCornerRadius, y = rectCornerRadius),
                    topLeft = Offset(x = size.width / 2 - radius, y = edgeTop),
                    style = Fill
                )

                drawRoundRect(
                    brush = brush,
                    size = Size(width = size.width - 2 * edgeLeft, height = 2 * radius),
                    cornerRadius = CornerRadius(x = rectCornerRadius, y = rectCornerRadius),
                    topLeft = Offset(x = edgeLeft, y = size.height / 2 - radius),
                    style = Fill
                )
            }
            drawCircle(
                brush = brushBallVertical,
                radius = radius,
                center =  Offset(size.width / 2, verticalBubbleCenter)
            )
            drawCircle(
                brush = brushBallHorizontal,
                radius = radius,
                center = Offset(horizontalBubbleCenter, size.height / 2)
            )
        }
    )
}

private fun getClampedEnds(value: Float, min: Float, max: Float): Float {
    if(value < min) {
        return min
    }
    if (value > max) {
        return max
    }
    return value
}