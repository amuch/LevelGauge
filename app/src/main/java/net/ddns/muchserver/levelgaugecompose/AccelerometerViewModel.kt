package net.ddns.muchserver.levelgaugecompose

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class AccelerometerViewModel : ViewModel() {
    var accelX by mutableStateOf(0.0f)
    var accelY by mutableStateOf(0.0f)
    var accelZ by mutableStateOf(0.0f)

    fun setAcceleration(accel: FloatArray?) {
        try {
            accelX = accel!![0]
            accelY = accel[1]
            accelZ = accel[2]
        }
        catch(e: Exception) {
            "Invalid"
        }

    }

}