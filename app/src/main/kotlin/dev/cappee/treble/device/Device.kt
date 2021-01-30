package dev.cappee.treble.device

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Device(
    //General
    val identifier: String,
    val battery: String,
    //Chipset
    val cpu: String,
    val gpu: String,
    val arch: String,
    //Memory
    val ram: String,
    val internalMemory: String,
    val externalMemory: String,
    //Display
    val screenSize: String,
    val screenResolution: String,
    val dpi: String,
    val refreshRate: String
) : Parcelable
