package net.ddns.muchserver.levelgaugecompose.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.ddns.muchserver.levelgaugecompose.repository.DataStoreRepository

class PreferenceViewModel(application: Application): AndroidViewModel(application) {

    private val repository = DataStoreRepository(application)

    val readFromDataStore = repository.readFromDataStore.asLiveData()

    fun saveToDataStore(colors: String) = viewModelScope.launch(Dispatchers.IO) {
        repository.saveToDataStore(colors)
    }

    val readHexFromDataStore = repository.readHexFromDataStore.asLiveData()

    fun saveHexToDataStore(hex: Float) = viewModelScope.launch(Dispatchers.IO) {
        repository.saveHexToDataStore(hex)
    }
}