package dev.cappee.treble.main

import androidx.lifecycle.*
import dev.cappee.treble.device.Device
import dev.cappee.treble.device.DeviceHelper
import dev.cappee.treble.root.Root
import dev.cappee.treble.root.RootHelper
import dev.cappee.treble.treble.Treble
import dev.cappee.treble.treble.TrebleHelper
import kotlinx.coroutines.launch

class MainViewModel(
    private val trebleHelper: TrebleHelper,
    private val rootHelper: RootHelper,
    private val deviceHelper: DeviceHelper
) : ViewModel() {

    private val mutableLiveDataTreble = MutableLiveData<Treble>()
    private val mutableLiveDataRoot = MutableLiveData<Root>()
    private val mutableLiveDataDevice = MutableLiveData<Device>()

    val liveDataTreble: LiveData<Treble> = liveData {
        val data = trebleHelper.get()
        emit(data)
        emitSource(mutableLiveDataTreble)
    }

    val liveDataRoot: LiveData<Root> = liveData {
        val data = rootHelper.get()
        emit(data)
        emitSource(mutableLiveDataRoot)
    }

    val liveDataDevice: LiveData<Device> = liveData {
        val data = deviceHelper.get()
        emit(data)
        emitSource(mutableLiveDataDevice)
    }

    fun updateValues() {
        viewModelScope.launch {
            mutableLiveDataTreble.postValue(trebleHelper.get())
            mutableLiveDataRoot.postValue(rootHelper.get())
            mutableLiveDataDevice.postValue(deviceHelper.get())
        }
    }

}