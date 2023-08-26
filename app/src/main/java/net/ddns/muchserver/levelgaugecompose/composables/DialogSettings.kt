package net.ddns.muchserver.levelgaugecompose.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.window.DialogProperties
import net.ddns.muchserver.levelgaugecompose.MainActivity
import net.ddns.muchserver.levelgaugecompose.repository.HEX_MINIMUM
import net.ddns.muchserver.levelgaugecompose.repository.THEME_DARK
import net.ddns.muchserver.levelgaugecompose.repository.THEME_LIGHT
import net.ddns.muchserver.levelgaugecompose.viewmodels.PreferenceViewModel
import java.lang.Float


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun DialogSettings(
    showDialog: MutableState<Boolean>,
    preferenceViewModel: PreferenceViewModel,
    activity: MainActivity,
    onClose: () -> Unit
) {
    var theme by remember { mutableStateOf(THEME_LIGHT) }
    preferenceViewModel.readFromDataStore.observe(activity) { colors ->
        theme = colors
    }

    val colorBackground = if(theme == THEME_LIGHT) Color.White else Color.Black
    val colorText = if(theme == THEME_LIGHT) Color.Black else Color.White
    val colorButton = if(theme == THEME_LIGHT) Color.Red else Color.Blue

    var sliderValue by remember { mutableStateOf(HEX_MINIMUM) }
    preferenceViewModel.readHexFromDataStore.observe(activity) { hex ->
        sliderValue = hex
    }

    if(showDialog.value) {
//        Surface(
//            modifier = Modifier.fillMaxSize(),
//            elevation = 8.dp
//        ) {
            Dialog(
                onDismissRequest = { showDialog.value = false },
//                properties = DialogProperties(usePlatformDefaultWidth = false)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp)
                        .clip(RoundedCornerShape(15.dp))
                        .background(color = colorBackground)
                        .border(2.dp, colorText, RoundedCornerShape(15.dp))

                ) {
                    Button(
                        onClick = {
                            showDialog.value = false
                        },
                        colors = ButtonDefaults.textButtonColors(
                            backgroundColor = colorButton,
                            contentColor = colorText
                        ),
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(10.dp)
                    ) {
                        Text(text = "Close")
                    }
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                    ) {
                        Slider(
                            value = sliderValue,
                            onValueChange = { sliderVal ->
                                sliderValue = sliderVal
                            },
                            onValueChangeFinished = {
                                preferenceViewModel.saveHexToDataStore(sliderValue)
                            },
                            valueRange = 0f..1.0f,
                            modifier = Modifier
                                .fillMaxWidth(0.9f),
                            colors = SliderDefaults.colors(
                                activeTrackColor = colorButton,
                                thumbColor = colorButton
                            )
                        )
                        Text(
                            text = Math.round(255 * sliderValue).toString(),
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally),
                            color = colorText
                        )
                    }
                    Button(
                        onClick = {
                            if(preferenceViewModel.readFromDataStore.value!! == THEME_LIGHT) {
                                preferenceViewModel.saveToDataStore(THEME_DARK)
                            }
                            else {
                                preferenceViewModel.saveToDataStore(THEME_LIGHT)
                            }
                        },
                        colors = ButtonDefaults.textButtonColors(
                            backgroundColor = colorButton,
                            contentColor = colorText
                        ),
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(10.dp)
                    ) {
                        Text(text = theme)
                    }
                }
            }
        }
//    }
}
