package net.ddns.muchserver.levelgaugecompose.composables


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import net.ddns.muchserver.levelgaugecompose.MainActivity
import net.ddns.muchserver.levelgaugecompose.repository.THEME_DARK
import net.ddns.muchserver.levelgaugecompose.repository.THEME_LIGHT
import net.ddns.muchserver.levelgaugecompose.viewmodels.PreferenceViewModel

@Composable
fun DynamicButton(
    preferenceViewModel: PreferenceViewModel,
    activity: MainActivity,
    modifier: Modifier
) {
    var theme by remember { mutableStateOf(THEME_LIGHT) }
    preferenceViewModel.readFromDataStore.observe(activity) { colors ->
        theme = colors
    }

    val darkTheme = listOf(Color(0xDDEF9A9A), Color(0xDDEF9A9A), Color(0xDDD32F2F))
    val lightTheme = listOf(Color(0xDD90CAF9), Color(0xDD90CAF9), Color(0xDD1976D2))
    val brush = Brush.verticalGradient(
        colors = if(theme == THEME_LIGHT) lightTheme else darkTheme
    )

    Button(
        onClick = {
            if(preferenceViewModel.readFromDataStore.value!! == THEME_LIGHT) {
                preferenceViewModel.saveToDataStore(THEME_DARK)
            }
            else {
                preferenceViewModel.saveToDataStore(THEME_LIGHT)
            }
        },
        colors = ButtonDefaults.buttonColors(
           Color.Transparent
        ),
        contentPadding = PaddingValues(),
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .background(brush = brush)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = theme,
                color = if (theme == THEME_LIGHT) Color.Black else Color.White
            )
        }
    }
}