package net.ddns.muchserver.levelgaugecompose

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class AccelerometerViewModel : ViewModel() {
    var accelX by mutableStateOf("")
    var accelY by mutableStateOf("")
    var accelZ by mutableStateOf("")

    fun setAcceleration(accel: FloatArray?) {
        try {
            val x = "%.3f".format(accel!![0])
            accelX = "Accel X: $x"
            val y = "%.3f".format(accel[1])
            accelY = "Accel Y: $y"
            val z = "%.3f".format(accel[2])
            accelZ = "Accel Z: $z"
        }
        catch(e: Exception) {
            "Invalid"
        }

    }

}