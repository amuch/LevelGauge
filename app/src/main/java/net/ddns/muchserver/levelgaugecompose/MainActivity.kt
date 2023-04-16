package net.ddns.muchserver.levelgaugecompose

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.Window
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.ddns.muchserver.levelgaugecompose.ui.theme.LevelGaugeComposeTheme

class MainActivity : ComponentActivity(), SensorEventListener {
    private var sensorManager: SensorManager? = null
    private var accelerometer: Sensor? = null
    private lateinit var accelerometerViewModel: AccelerometerViewModel

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
                    ScreenSetupMain(accelerometerViewModel)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        sensorManager!!.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME)
    }

    override fun onPause() {
        super.onPause()
        sensorManager!!.unregisterListener(this)
    }

    override fun onSensorChanged(sensorEvent: SensorEvent?) {
        accelerometerViewModel.setAcceleration(sensorEvent!!.values)
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {  }
}

@Composable
fun ScreenSetupMain(
    viewModel: AccelerometerViewModel
) {
    ScreenMain(
        accelX = viewModel.accelX,
        accelY = viewModel.accelY,
        accelZ = viewModel.accelZ,
    )
}

@Composable
fun ScreenMain(
    accelX : String,
    accelY : String,
    accelZ : String,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        AccelText(
            message = accelX
        )
        AccelText(
            message = accelY
        )
        AccelText(
            message = accelZ
        )
    }
}

@Composable
fun AccelText(message: String) {
    Text(
        style = MaterialTheme.typography.h3,
        text = message,
        modifier = Modifier.padding(10.dp)
    )
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    LevelGaugeComposeTheme {

    }
}