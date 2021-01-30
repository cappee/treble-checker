package dev.cappee.treble.root

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Root(
    //Superuser
    val rootPermissions: String,
    val rootPath: String,
    //BusyBox
    val busyBoxStatus: String,
    val busyBoxBuildDate: String
) : Parcelable
