package net.ddns.muchserver.levelgaugecompose

import android.graphics.Typeface
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.Window
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.ddns.muchserver.levelgaugecompose.ui.theme.LevelGaugeComposeTheme

const val ACCELERATION_DUE_TO_GRAVITY = 9.81
const val DELAY_UPDATE = 50L

class MainActivity : ComponentActivity(), SensorEventListener {
    private val coroutineScopeMain = CoroutineScope(Dispatchers.Main)
    private var sensorManager: SensorManager? = null
    private var accelerometer: Sensor? = null
    private lateinit var accelerometerViewModel: AccelerometerViewModel
    private var shouldUpdate = false
    private var accelerometerIsRegistered = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        accelerometerViewModel = AccelerometerViewModel()

        setContent {
            LevelGaugeComposeTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    LevelGauge(accelerometerViewModel)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        registerAccelerometer()
        updateAccelerometer()
    }

    override fun onPause() {
        super.onPause()
        sensorManager!!.unregisterListener(this)
        accelerometerIsRegistered = false
    }

    override fun onSensorChanged(sensorEvent: SensorEvent?) {
        if(shouldUpdate) {
            accelerometerViewModel.setAcceleration(sensorEvent!!.values)
            shouldUpdate = false
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {  }

    private fun registerAccelerometer() {
        sensorManager!!.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME)
        accelerometerIsRegistered = true
        shouldUpdate = true
    }

    private fun updateAccelerometer() {
        coroutineScopeMain.launch {
            while(accelerometerIsRegistered) {
                delay(DELAY_UPDATE)
                shouldUpdate = true
            }
        }
    }
}

@Composable
fun LevelGauge(
    viewModel: AccelerometerViewModel
) {
    val accelX = "%.3f".format(viewModel.accelX)
    val verticalAcceleration = "%.3f".format(-1 * viewModel.accelX)
    val accelY = "%.3f".format(viewModel.accelY)
    val horizontalAcceleration = "%.3f".format(viewModel.accelY)
    val accelZ = "%.3f".format(viewModel.accelZ)

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp

    val screenHeight = configuration.screenHeightDp

    val sideShorter = kotlin.math.min(screenWidth, screenHeight)
    val paddingEnd = sideShorter / 12
    val radius = sideShorter / 10

    val textSizeLocal = (sideShorter / 12).toFloat()

    val drawWidth = screenWidth.dp
    val drawHeight = screenHeight.dp
    val offsetX = (drawWidth / 2) + (viewModel.accelY / ACCELERATION_DUE_TO_GRAVITY) * (3 * drawWidth / 7)
    val offsetY = (drawHeight / 2) + (viewModel.accelX / ACCELERATION_DUE_TO_GRAVITY) * (3 * drawHeight / 7)

    val textPaint = Paint().asFrameworkPaint().apply{
        isAntiAlias = true
        color = android.graphics.Color.BLACK
        typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
        textSize = textSizeLocal
    }
    val paint = Paint().apply {
        color = Color.Yellow
    }
    Canvas(
        modifier = Modifier.fillMaxSize(),
        onDraw = {
            drawIntoCanvas {
                it.nativeCanvas.drawText(
                    "X: $horizontalAcceleration",
                    (screenWidth - paddingEnd).toFloat(),
                    (screenWidth / 15).toFloat(),
                    textPaint
                )
                it.nativeCanvas.drawText(
                    "Y: $verticalAcceleration",
                    (screenWidth - paddingEnd).toFloat(),
                    (2 * screenWidth / 15).toFloat(),
                    textPaint
                )
                it.nativeCanvas.drawText(
                    "Z: $accelZ",
                    (screenWidth - paddingEnd).toFloat(),
                    (screenWidth / 5).toFloat(),
                    textPaint
                )
//                it.nativeCanvas.drawText(
//                    "Width: $screenWidth, Height: $screenHeight",
//                    paddingEnd.toFloat(),
//                    (screenWidth / 15).toFloat(),
//                    textPaint
//                )
                paint.apply {
                    color = Color.Black
                }
                it.drawLine(
                    Offset((drawWidth / 2).toPx(), (drawHeight / 8).toPx()),
                    Offset((drawWidth / 2).toPx(), (7 * drawHeight / 8).toPx()),
                    paint
                )
                it.drawLine(
                    Offset((drawWidth / 8).toPx(), (drawHeight / 2).toPx()),
                    Offset((7 * drawWidth / 8).toPx(), (drawHeight / 2).toPx()),
                    paint
                )
//                it.drawCircle(
//                    Offset(offsetX.toPx(), offsetY.toPx()),
//                    radius.toFloat(),
//                    paint
//                )
                paint.apply {
                    color = Color(0x990000FF)
                }
                it.drawCircle(
                    Offset((drawWidth / 2).toPx(), offsetY.toPx()),
                    radius.toFloat(),
                    paint
                )
                paint.apply {
                    color = Color(0x5500FF00)
                }
                it.drawCircle(
                    Offset(offsetX.toPx(), (drawHeight / 2).toPx()),
                    radius.toFloat(),
                    paint
                )
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    LevelGaugeComposeTheme {

    }
}