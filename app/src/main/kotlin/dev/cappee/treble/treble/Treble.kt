package dev.cappee.treble.treble

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Treble(
    //Project Treble
    val trebleStatus: String,
    val trebleArch: String,
    val vndkVersion: String,
    //A/B Partitioning
    val abStatus: String,
    val seamlessUpdate: String,
    //System-as-root
    val sarStatus: String,
    val sarMethod: String
) : Parcelable
