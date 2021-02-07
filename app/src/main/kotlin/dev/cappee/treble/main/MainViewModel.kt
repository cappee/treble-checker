package dev.cappee.treble.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import dev.cappee.treble.device.Device
import dev.cappee.treble.device.DeviceHelper
import dev.cappee.treble.root.Root
import dev.cappee.treble.root.RootHelper
import dev.cappee.treble.treble.Treble
import dev.cappee.treble.treble.TrebleHelper

class MainViewModel(
    private val trebleHelper: TrebleHelper,
    private val rootHelper: RootHelper,
    private val deviceHelper: DeviceHelper
) : ViewModel() {

    val liveDataTreble: LiveData<Treble> = liveData {
        val data = trebleHelper.get()
        emit(data)
    }

    val liveDataRoot: LiveData<Root> = liveData {
        val data = rootHelper.get()
        emit(data)
    }

    val liveDataDevice: LiveData<Device> = liveData {
        val data = deviceHelper.get()
        emit(data)
    }

    var test = "GIGI"

}