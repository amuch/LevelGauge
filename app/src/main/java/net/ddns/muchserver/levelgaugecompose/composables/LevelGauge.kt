package net.ddns.muchserver.levelgaugecompose.composables

import android.graphics.Typeface
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
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
const val COLOR_BUBBLE_HORIZONTAL_LIGHT = 0x990000FF
const val COLOR_BUBBLE_HORIZONTAL_DARK = 0x99FF0000
const val COLOR_BUBBLE_VERTICAL_LIGHT = 0x5500FF00
const val COLOR_BUBBLE_VERTICAL_DARK = 0x55FFA500

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

    val colorHorizontal = if(theme == THEME_LIGHT) COLOR_BUBBLE_HORIZONTAL_LIGHT else COLOR_BUBBLE_HORIZONTAL_DARK
    val colorVertical = if(theme == THEME_LIGHT) COLOR_BUBBLE_VERTICAL_LIGHT else COLOR_BUBBLE_VERTICAL_DARK
    val colorBackground = if(theme == THEME_LIGHT) Color.White else Color.Black
    val colorText = if(theme == THEME_LIGHT) Color.Black else Color.White

    Canvas(
        modifier = Modifier.fillMaxSize(),
        onDraw = {

            val radius = size.height / 10
            val edgeLeft = size.width / 20
            val edgeRight = size.width - edgeLeft
            val centerXCalculated = (size.width / 2) + (accelerometerViewModel.accelY / ACCELERATION_DUE_TO_GRAVITY) * edgeRight
            val horizontalBubbleCenter = getClampedEnds(centerXCalculated.toFloat(), edgeLeft + radius, edgeRight - radius)

            val edgeTop = size.height / 20
            val edgeBottom = size.height - edgeTop
            val centerYCalculated = (size.height / 2) + (accelerometerViewModel.accelX / ACCELERATION_DUE_TO_GRAVITY) * edgeBottom
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

            val paint = Paint().apply {
                color = Color.Yellow
            }

            drawIntoCanvas {
                drawRect(
                    colorBackground,
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
                it.nativeCanvas.drawText(
                    theme,
                    xOffsetText,
                    4 * yOffsetText,
                    textPaint
                )
                drawRoundRect(
                    color = colorText,
                    size = Size(width = 2 * radius, size.height - 2 * edgeTop),
                    cornerRadius = CornerRadius(x = rectCornerRadius, y = rectCornerRadius),
                    topLeft = Offset(x = size.width / 2 - radius, y = edgeTop),
                    style = Stroke(width = 2.0f)
                )

                drawRoundRect(
                    color = colorText,
                    size = Size(width = size.width - 2 * edgeLeft, height = 2 * radius),
                    cornerRadius = CornerRadius(x = rectCornerRadius, y = rectCornerRadius),
                    topLeft = Offset(x = edgeLeft, y = size.height / 2 - radius),
                    style = Stroke(width = 2.0f)
                )

                drawRoundRect(
                    color = colorBackground,
                    size = Size(width = 2 * radius, size.height - 2 * edgeTop),
                    cornerRadius = CornerRadius(x = rectCornerRadius, y = rectCornerRadius),
                    topLeft = Offset(x = size.width / 2 - radius, y = edgeTop),
                    style = Fill
                )

                drawRoundRect(
                    color = colorBackground,
                    size = Size(width = size.width - 2 * edgeLeft, height = 2 * radius),
                    cornerRadius = CornerRadius(x = rectCornerRadius, y = rectCornerRadius),
                    topLeft = Offset(x = edgeLeft, y = size.height / 2 - radius),
                    style = Fill
                )


//                it.drawCircle(
//                    Offset(horizontalBubbleCenter, verticalBubbleCenter),
//                    radius.toFloat(),
//                    paint
//                )
                paint.apply {
                    color = Color(colorVertical)
                }
                it.drawCircle(
                    Offset(size.width / 2, verticalBubbleCenter),
                    radius,
                    paint
                )
                paint.apply {
                    color = Color(colorHorizontal)
                }
                it.drawCircle(
                    Offset(horizontalBubbleCenter, size.height / 2),
                    radius,
                    paint
                )
            }
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