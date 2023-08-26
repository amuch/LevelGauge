package net.ddns.muchserver.levelgaugecompose

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.view.WindowInsetsController
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.ddns.muchserver.levelgaugecompose.composables.DynamicButton
import net.ddns.muchserver.levelgaugecompose.composables.LevelGauge
import net.ddns.muchserver.levelgaugecompose.repository.THEME_LIGHT
import net.ddns.muchserver.levelgaugecompose.viewmodels.PreferenceViewModel

const val DELAY_UPDATE = 50L

class MainActivity : ComponentActivity(), SensorEventListener {
    private val coroutineScopeMain = CoroutineScope(Dispatchers.Main)
    private var sensorManager: SensorManager? = null
    private var accelerometer: Sensor? = null
    private lateinit var accelerometerViewModel: AccelerometerViewModel
    private lateinit var preferenceViewModel: PreferenceViewModel
    private var shouldUpdate = false
    private var accelerometerIsRegistered = false
    private lateinit var theme: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        theme = THEME_LIGHT
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        accelerometerViewModel = AccelerometerViewModel()
        preferenceViewModel = ViewModelProvider(this).get(PreferenceViewModel::class.java)
        preferenceViewModel.readFromDataStore.observe(this) { colors ->
            theme = colors
        }

        val activity = this

        setContent {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colors.background
            ) {
                Box {
                    LevelGauge(
                        accelerometerViewModel,
                        preferenceViewModel,
                        activity
                    )
                    DynamicButton(
                        preferenceViewModel,
                        activity,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(16.dp)
                            .background(Color.Transparent)
                    )
                }
            }
        }
        hideSystemUI()
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

    private fun hideSystemUI() {
        actionBar?.hide()

        WindowCompat.setDecorFitsSystemWindows(window, false)

        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
        else {
            window.insetsController?.apply{
                hide(WindowInsetsCompat.Type.systemBars())
                systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        }
    }
}