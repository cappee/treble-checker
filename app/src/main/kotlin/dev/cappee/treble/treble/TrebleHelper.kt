package dev.cappee.treble.treble

import android.content.Context
import dev.cappee.treble.R
import dev.cappee.treble.model.Mount
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import java.io.BufferedReader
import java.io.File
import java.io.FileReader

object TrebleHelper {

    suspend fun get(context: Context) = coroutineScope {
        async(Dispatchers.IO) { Treble(
            trebleStatus(context),
            trebleVersion(context),
            vndkVersion(context),
            partitionStatus(context),
            seamlessUpdate(context),
            systemMount(context),
            systemMountMethod(context)
        ) }
    }.await()

    fun trebleStatus(context: Context) : String {
        println("THREAD TREBLE: ${Thread.currentThread()}")
        val processTreble = Runtime.getRuntime().exec("getprop ro.treble.enabled")
        var treble: String = processTreble.inputStream.bufferedReader().use(BufferedReader::readText)
        if (treble.isNotBlank())
            treble = treble.substring(0, treble.length - 1)
        return if (treble == "true") {
            context.getString(R.string.compatible)
        } else if (treble == "false" && (File("/vendor/etc/vintf/manifest.xml").exists() || File("/vendor/manifest.xml").exists())) {
            context.getString(R.string.compatible_but_hidden)
        } else {
            context.getString(R.string.not_compatible)
        }
    }

    //Ported and adapted from treble (https://github.com/kevintresuelo/treble)
    fun trebleVersion(context: Context) : String {
        return when {
            File("/vendor/etc/vintf/manifest.xml").exists() -> context.getString(R.string.latest_typology)
            File("/vendor/manifest.xml").exists() -> context.getString(R.string.legacy)
            else -> context.getString(R.string.not_supported)
        }
    }

    //Ported and adapted from treble (https://github.com/kevintresuelo/treble)
    fun vndkVersion(context: Context) : String {
        val processLite = Runtime.getRuntime().exec("getprop ro.vndk.lite").inputStream.bufferedReader().use(BufferedReader::readLine)
        var vndkLite = ""
        if (processLite == "true")
            vndkLite = "(" + context.getString(R.string.lite) + ")"
        val processVersion = Runtime.getRuntime().exec("getprop ro.vndk.version").inputStream.bufferedReader().use(BufferedReader::readText)
        return if (processVersion.substring(0, processVersion.length - 1).isNotEmpty()) {
            processVersion.substring(0, processVersion.length - 1) + " " + vndkLite
        } else {
            context.getString(R.string.not_supported)
        }
    }

    //Ported and adapted from treble (https://github.com/kevintresuelo/treble)
    fun partitionStatus(context: Context) : String {
        val processVirtualABEnabled = Runtime.getRuntime().exec("getprop ro.virtual_ab.enabled").inputStream.bufferedReader().use(BufferedReader::readText)
        val processVirtualABRetrofit = Runtime.getRuntime().exec("getprop ro.virtual_ab.retrofit").inputStream.bufferedReader().use(BufferedReader::readText)
        val processBootSlotSuffix = Runtime.getRuntime().exec("getprop ro.boot.slot_suffix").inputStream.bufferedReader().use(BufferedReader::readText)
        val processBuildABEnabled = Runtime.getRuntime().exec("getprop ro.build.enabled").inputStream.bufferedReader().use(BufferedReader::readText)

        val virtualABEnabled = processBootSlotSuffix.substring(0, processVirtualABEnabled.length - 1)
        val virtualABRetrofit = processBootSlotSuffix.substring(0, processVirtualABRetrofit.length - 1)
        val bootSlotSuffix = processBootSlotSuffix.substring(0, processBootSlotSuffix.length - 1)
        val buildABEnabled = processBootSlotSuffix.substring(0, processBuildABEnabled.length - 1)

        if (virtualABEnabled == "true" && virtualABRetrofit == "false") {
            return context.getString(R.string.virtual_a_b_partitioning)
        }
        if (bootSlotSuffix.isNotEmpty() || buildABEnabled == "true") {
            return context.getString(R.string.legacy_a_b_partitioning)
        }
        return context.getString(R.string.not_supported)
    }

    fun seamlessUpdate(context: Context) : String {
        return when (partitionStatus(context)) {
            context.getString(R.string.virtual_a_b_partitioning) -> context.getString(R.string.supported)
            context.getString(R.string.legacy_a_b_partitioning) -> context.getString(R.string.supported)
            else -> context.getString(R.string.not_supported)
        }
    }

    //Ported and adapted from treble (https://github.com/kevintresuelo/treble)
    private fun mountPoints() : ArrayList<Mount> {
        val mountsPoints = ArrayList<Mount>()
        val br = BufferedReader(FileReader("/proc/mounts"))
        var line: String?
        while (br.readLine().also { line = it } != null) {
            line?.let {
                val mountDetails = it.split(" ").toTypedArray()
                if (mountDetails.size == 6) {
                    val mountPoint = Mount(mountDetails[0], mountDetails[1], mountDetails[2], mountDetails[3], mountDetails[4], mountDetails[5])
                    mountsPoints.add(mountPoint)
                }
            }
        }
        return mountsPoints
    }

    //Ported and adapted from treble (https://github.com/kevintresuelo/treble)
    fun systemMount(context: Context) : String {
        val mountsPoints = mountPoints()
        val systemOnBlock = mountsPoints.none { it.device != "none" && it.mountPoint == "/system" && it.fileSystem != "tmpfs"}
        val deviceMountedOnRoot = mountsPoints.any { it.device == "/dev/root" && it.mountPoint == "/" }
        val systemOnRoot = mountsPoints.any { it.mountPoint == "/system_root" && it.fileSystem != "tmpfs" }
        return if (systemOnBlock || deviceMountedOnRoot || systemOnRoot) {
            context.getString(R.string.supported)
        } else {
            context.getString(R.string.not_supported)
        }
    }

    fun systemMountMethod(context: Context) : String {
        val mountsPoints = mountPoints()
        val systemOnBlock = mountsPoints.none { it.device != "none" && it.mountPoint == "/system" && it.fileSystem != "tmpfs"}
        val deviceMountedOnRoot = mountsPoints.any { it.device == "/dev/root" && it.mountPoint == "/" }
        val systemOnRoot = mountsPoints.any { it.mountPoint == "/system_root" && it.fileSystem != "tmpfs" }
        return when {
            systemOnRoot -> { context.getString(R.string.mounted_like_system_as_root) }
            deviceMountedOnRoot -> { context.getString(R.string.mounted_like_device_as_root) }
            systemOnBlock -> { context.getString(R.string.mounted_like_system_on_block) }
            else -> { context.getString(R.string.not_supported) }
        }
    }

}