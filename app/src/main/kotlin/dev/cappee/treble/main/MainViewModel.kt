package dev.cappee.treble.main

import androidx.lifecycle.*
import dev.cappee.treble.device.Device
import dev.cappee.treble.device.DeviceHelper
import dev.cappee.treble.root.Root
import dev.cappee.treble.root.RootHelper
import dev.cappee.treble.treble.Treble
import dev.cappee.treble.treble.TrebleHelper
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    val liveDataTreble: LiveData<Treble> = liveData {
        emit(TrebleHelper.get())
    }

    val liveDataRoot: LiveData<Root> = liveData {
        emit(RootHelper.get())
    }

    val liveDataDevice: LiveData<Device> = liveData {
        emit(DeviceHelper.get())
    }

}