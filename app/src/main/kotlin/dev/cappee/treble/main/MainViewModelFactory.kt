package dev.cappee.treble.main

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.ads.AdLoader
import dev.cappee.treble.device.DeviceHelper
import dev.cappee.treble.root.RootHelper
import dev.cappee.treble.treble.TrebleHelper

class MainViewModelFactory(
    private val applicationContext: Context,
    private val trebleHelper: TrebleHelper,
    private val rootHelper: RootHelper,
    private val deviceHelper: DeviceHelper
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MainViewModel(applicationContext, trebleHelper, rootHelper, deviceHelper) as T
    }
}