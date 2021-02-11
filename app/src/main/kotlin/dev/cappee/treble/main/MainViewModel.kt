package dev.cappee.treble.main

import android.content.Context
import androidx.lifecycle.*
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.nativead.NativeAd
import dev.cappee.treble.device.Device
import dev.cappee.treble.device.DeviceHelper
import dev.cappee.treble.root.Root
import dev.cappee.treble.root.RootHelper
import dev.cappee.treble.treble.Treble
import dev.cappee.treble.treble.TrebleHelper
import kotlinx.coroutines.launch

class MainViewModel(
    private val applicationContext: Context,
    private val trebleHelper: TrebleHelper,
    private val rootHelper: RootHelper,
    private val deviceHelper: DeviceHelper,
) : ViewModel() {

    private val mutableLiveDataTreble = MutableLiveData<Treble>()
    private val mutableLiveDataRoot = MutableLiveData<Root>()
    private val mutableLiveDataDevice = MutableLiveData<Device>()

    val liveDataTreble: LiveData<Treble> = liveData {
        emitSource(mutableLiveDataTreble)
    }

    val liveDataRoot: LiveData<Root> = liveData {
        emitSource(mutableLiveDataRoot)
    }

    val liveDataDevice: LiveData<Device> = liveData {
        emitSource(mutableLiveDataDevice)
    }

    fun updateValues() {
        viewModelScope.launch {
            mutableLiveDataTreble.postValue(trebleHelper.get())
            mutableLiveDataRoot.postValue(rootHelper.get())
            mutableLiveDataDevice.postValue(deviceHelper.get())
        }
    }

    private val mutableLiveDataNativeAdTreble = MutableLiveData<NativeAd>()
    private val mutableLiveDataNativeAdRoot = MutableLiveData<NativeAd>()
    private val mutableLiveDataNativeAdDevice = MutableLiveData<NativeAd>()

    val liveDataAdTreble: LiveData<NativeAd> = liveData {
        emitSource(mutableLiveDataNativeAdTreble)
    }

    val liveDataAdRoot: LiveData<NativeAd> = liveData {
        emitSource(mutableLiveDataNativeAdRoot)
    }

    val liveDataAdDevice: LiveData<NativeAd> = liveData {
        emitSource(mutableLiveDataNativeAdDevice)
    }

    fun getNativeAds() {
        var index = 0
        AdLoader.Builder(applicationContext, "ca-app-pub-2954582391475229/6394775935")
            .forNativeAd {
                when(index) {
                    0 -> {
                        viewModelScope.launch {
                            mutableLiveDataNativeAdTreble.postValue(it)
                            index += 1
                        }
                    }
                    1 -> {
                        viewModelScope.launch {
                            mutableLiveDataNativeAdRoot.postValue(it)
                            index += 1
                        }
                    }
                    2 -> {
                        viewModelScope.launch {
                            mutableLiveDataNativeAdDevice.postValue(it)
                            index += 1
                        }
                    }
                }
            }
            .build()
            .loadAds(AdRequest.Builder().build(), 3)
    }

}