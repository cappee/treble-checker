package dev.cappee.treble.settings

import androidx.lifecycle.*
import dev.cappee.treble.BuildConfig
import dev.cappee.treble.device.DeviceHelper
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class SettingsViewModel(private val settingsRepository: SettingsRepository) : ViewModel() {

    val identifierEntries = DeviceHelper.possibleIdentifierOrder

    @Suppress("UNCHECKED_CAST")
    val cpuEntries: MutableMap<String, CharSequence> = runBlocking {
        DeviceHelper.cpu(true) as MutableMap<String, CharSequence>
    }

    val liveDataBatteryMode = settingsRepository.getBatteryFetchModeExperimental().asLiveData()

    fun setBatteryModeExperimental(value: Int) {
        viewModelScope.launch {
            settingsRepository.setBatteryFetchModeExperimental(value)
        }
    }

    val liveDataIdentifierOrder = settingsRepository.getIdentifierOrder().asLiveData()

    fun setIdentifierOrder(value: Int) {
        viewModelScope.launch {
            settingsRepository.setIdentifierOrder(value)
        }
    }

    val liveDataProcessorShownAs = settingsRepository.getProcessorShownAs().asLiveData()

    fun setProcessorShownAs(value: String) {
        viewModelScope.launch {
            settingsRepository.setProcessorShownAs(value)
        }
    }

    val appVersion = "${BuildConfig.VERSION_NAME} (${BuildConfig.BUILD_TYPE})"

}