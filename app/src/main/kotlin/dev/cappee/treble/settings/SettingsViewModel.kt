package dev.cappee.treble.settings

import androidx.lifecycle.*
import dev.cappee.treble.BuildConfig
import dev.cappee.treble.device.DeviceHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SettingsViewModel(private val settingsRepository: SettingsRepository) : ViewModel() {

    val identifierEntries = DeviceHelper.possibleIdentifierOrder

    lateinit var cpu: String
    lateinit var cpuEntries: MutableList<CharSequence>
    suspend fun updateCpuValues() {
        cpu = withContext(Dispatchers.Default) {
            DeviceHelper.cpu() as String
        }
        @Suppress("UNCHECKED_CAST")
        cpuEntries = withContext(Dispatchers.Default) {
            val entries = DeviceHelper.cpu(true) as MutableList<CharSequence?>
            entries.removeIf { it.isNullOrEmpty() || it == "0" }
            return@withContext entries as MutableList<CharSequence>
        }
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

    fun setProcessorShownAs(value: Int) {
        viewModelScope.launch {
            settingsRepository.setProcessorShownAs(value)
        }
    }

    val appVersion = "${BuildConfig.VERSION_NAME} (${BuildConfig.BUILD_TYPE})"

}