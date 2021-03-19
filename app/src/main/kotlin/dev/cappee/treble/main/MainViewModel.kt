package dev.cappee.treble.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.cappee.treble.device.Device
import dev.cappee.treble.device.DeviceHelper
import dev.cappee.treble.root.Root
import dev.cappee.treble.root.RootHelper
import dev.cappee.treble.treble.Treble
import dev.cappee.treble.treble.TrebleHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(
    private val trebleHelper: TrebleHelper,
    private val rootHelper: RootHelper,
    private val deviceHelper: DeviceHelper,
) : ViewModel() {

    val liveDataTreble = MutableLiveData<Treble>()
    val liveDataRoot = MutableLiveData<Root>()
    val liveDataDevice = MutableLiveData<Device>()

    fun updateValues() {
        viewModelScope.launch(Dispatchers.Default) {
            liveDataTreble.postValue(trebleHelper.get())
            liveDataRoot.postValue(rootHelper.get())
            liveDataDevice.postValue(deviceHelper.get())
        }
    }

}