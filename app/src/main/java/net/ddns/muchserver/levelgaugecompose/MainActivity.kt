package net.ddns.muchserver.levelgaugecompose

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.Window
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.ddns.muchserver.levelgaugecompose.composables.DialogSettings
import net.ddns.muchserver.levelgaugecompose.composables.LevelGauge
import net.ddns.muchserver.levelgaugecompose.repository.THEME_LIGHT
import net.ddns.muchserver.levelgaugecompose.ui.theme.LevelGaugeComposeTheme
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
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        accelerometerViewModel = AccelerometerViewModel()
        preferenceViewModel = ViewModelProvider(this).get(PreferenceViewModel::class.java)
        preferenceViewModel.readFromDataStore.observe(this) { colors ->
            theme = colors
        }

        val activity = this

        setContent {
            var darkTheme by remember { mutableStateOf(false) }
            val colorButtonBackground = if(theme == THEME_LIGHT) Color.Red else Color.Blue
            val colorButtonText = if(theme == THEME_LIGHT) Color.White else Color.Black
            LevelGaugeComposeTheme(darkTheme = darkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    val showDialogSettings = remember { mutableStateOf(false) }
                    Box {
                        LevelGauge(accelerometerViewModel, preferenceViewModel, activity)
                        Button(
                            onClick = {
                                showDialogSettings.value = true
                                      println("${showDialogSettings.value}")},
                            colors = ButtonDefaults.textButtonColors(
                                backgroundColor = colorButtonBackground,
                                contentColor = colorButtonText
                            ),
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(10.dp)
//                                .border(5.dp, Color.White, CircleShape)
                        ) {
                            Text(text = "Settings")
                        }
                    }

                    DialogSettings(
                        showDialog = showDialogSettings,
                        preferenceViewModel = preferenceViewModel,
                        activity = activity
                    ) { }

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
/*
@Composable
fun ThemeSwitcher(
    darkTheme: Boolean = false,
    size: Dp = 150.dp,
    iconSize: Dp = size / 3,
    padding: Dp = 10.dp,
    borderWidth: Dp = 1.dp,
    parentShape: Shape = CircleShape,
    toggleShape: Shape = CircleShape,
    animationSpec: AnimationSpec<Dp> = tween(durationMillis = 300),
    onClick: () -> Unit
) {
    val offset by animateDpAsState(
        targetValue = if(darkTheme) 0.dp else size,
        animationSpec = animationSpec
    )

    Box(modifier = Modifier
        .width(size * 2)
        .height(size)
        .clip(shape = parentShape)
        .clickable { onClick() }
        .background(MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Box(modifier = Modifier
            .size(size)
            .offset(x = offset)
            .padding(all = padding)
            .clip(shape = parentShape)
            .background(MaterialTheme.colorScheme.primary)
        ) {}
        Row(
            modifier = Modifier
                .border(
                    border = BorderStroke(
                        width = borderWidth,
                        color = MaterialTheme.colorScheme.primary
                    ) ,
                    shape = parentShape
                )
        ) {
            Box(
                modifier = Modifier.size(size),
                contentAlignment = Alignment.Center

            ) {
                Icon (
                    modifier = Modifier.size(iconSize),
                    imageVector = Icons.Default.NightLight,
                    contentDescription = "Theme Icon",
                    tint =  if(darkTheme)
                                MaterialTheme.colorScheme.secondaryContainer
                            else
                                MaterialTheme.colorScheme.primary
                )
            }
            Box(
                modifier = Modifier.size(size),
                contentAlignment = Alignment.Center

            ){}
        }
    }
}

**/

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    LevelGaugeComposeTheme {

    }
}