package dev.cappee.treble.main

import android.content.Context
import androidx.lifecycle.*
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
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
        viewModelScope.launch {
            AdLoader.Builder(applicationContext, "ca-app-pub-2954582391475229/4977892104")
                .forNativeAd {
                    when(index) {
                        0 -> {
                            mutableLiveDataNativeAdTreble.postValue(it)
                            index += 1
                        }
                        1 -> {
                            mutableLiveDataNativeAdRoot.postValue(it)
                            index += 1
                        }
                        2 -> {
                            mutableLiveDataNativeAdDevice.postValue(it)
                            index += 1
                        }
                    }
                }
                .withAdListener(object : AdListener() {
                    override fun onAdFailedToLoad(loadAdError: LoadAdError?) {
                        super.onAdFailedToLoad(loadAdError)
                        println("ADS ERROR cause: ${loadAdError?.cause}")
                        println("ADS ERROR message: ${loadAdError?.message}")
                        println("ADS ERROR domain: ${loadAdError?.domain}")
                    }
                })
                .build()
                .loadAds(AdRequest.Builder().build(), 3)
        }
    }

}