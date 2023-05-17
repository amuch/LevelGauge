package net.ddns.muchserver.levelgaugecompose.repository

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.preferencesKey
import androidx.datastore.preferences.createDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

const val PREFERENCE_COLOR = "color_preference"
const val NAME_COLOR = "color_name"
const val NAME_HEX = "hex_name"
const val TAG_DATASTORE = "DataStore"

const val THEME_LIGHT = "Light"
const val THEME_DARK = "Dark"
const val HEX_MINIMUM = 0.0f

class DataStoreRepository(context: Context) {
    private object PreferenceKeys {
        val colors = preferencesKey<String>(NAME_COLOR)
        val hex = preferencesKey<Float>(NAME_HEX)
    }
    private val dataStore: DataStore<Preferences>  = context.createDataStore(
        name = PREFERENCE_COLOR
    )

    suspend fun saveHexToDataStore(hex: Float) {
        dataStore.edit { preference ->
            preference[PreferenceKeys.hex] = hex
        }
    }

    suspend fun saveToDataStore(colors: String) {
        dataStore.edit {preference ->
            preference[PreferenceKeys.colors] = colors
        }
    }

    val readFromDataStore: Flow<String> = dataStore.data
        .catch { exception ->
            if(exception is IOException) {
                Log.d(TAG_DATASTORE, exception.message.toString())
                emit(emptyPreferences())
            }
            else {
                throw exception
            }
        }
        .map { preference ->
            val color = preference[PreferenceKeys.colors] ?: THEME_LIGHT
            color
        }

    val readHexFromDataStore: Flow<Float> = dataStore.data
        .catch { exception ->
            if(exception is IOException) {
                Log.d(TAG_DATASTORE, exception.message.toString())
                emit(emptyPreferences())
            }
            else {
                throw exception
            }
        }
        .map { preference ->
            val hex = preference[PreferenceKeys.hex] ?: HEX_MINIMUM
            hex
        }
}