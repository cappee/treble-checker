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

    suspend fun get() = coroutineScope {
        async(Dispatchers.Default) { Treble(
            trebleStatus(),
            trebleVersion(),
            vndkVersion(),
            partitionStatus(),
            seamlessUpdate(),
            systemMount(),
            systemMountMethod()
        ) }
    }.await()

    fun init(context: Context) : TrebleHelper {
        applicationContext = context.applicationContext
        return this
    }

    private lateinit var applicationContext: Context

    private fun trebleStatus() : String {
        println("THREAD TREBLE: ${Thread.currentThread()}")
        val processTreble = Runtime.getRuntime().exec("getprop ro.treble.enabled")
        var treble: String = processTreble.inputStream.bufferedReader().use(BufferedReader::readText)
        if (treble.isNotBlank())
            treble = treble.substring(0, treble.length - 1)
        return if (treble == "true") {
            applicationContext.getString(R.string.compatible)
        } else if (treble == "false" && (File("/vendor/etc/vintf/manifest.xml").exists() || File("/vendor/manifest.xml").exists())) {
            applicationContext.getString(R.string.compatible_but_hidden)
        } else {
            applicationContext.getString(R.string.not_compatible)
        }
    }

    //Ported and adapted from treble (https://github.com/kevintresuelo/treble)
    private fun trebleVersion() : String {
        return when {
            File("/vendor/etc/vintf/manifest.xml").exists() -> applicationContext.getString(R.string.latest_typology)
            File("/vendor/manifest.xml").exists() -> applicationContext.getString(R.string.legacy)
            else -> applicationContext.getString(R.string.not_supported)
        }
    }

    //Ported and adapted from treble (https://github.com/kevintresuelo/treble)
    private fun vndkVersion() : String {
        val processLite = Runtime.getRuntime().exec("getprop ro.vndk.lite").inputStream.bufferedReader().use(BufferedReader::readLine)
        var vndkLite = ""
        if (processLite == "true")
            vndkLite = "(" + applicationContext.getString(R.string.lite) + ")"
        val processVersion = Runtime.getRuntime().exec("getprop ro.vndk.version").inputStream.bufferedReader().use(BufferedReader::readText)
        return if (processVersion.substring(0, processVersion.length - 1).isNotEmpty()) {
            processVersion.substring(0, processVersion.length - 1) + " " + vndkLite
        } else {
            applicationContext.getString(R.string.not_supported)
        }
    }

    /*
     1 -> Virtual
     2 -> Classic
     */
    private var partitionStatusInt: Int = 0

    //Ported and adapted from treble (https://github.com/kevintresuelo/treble)
    private fun partitionStatus() : String {
        val processVirtualABEnabled = Runtime.getRuntime().exec("getprop ro.virtual_ab.enabled").inputStream.bufferedReader().use(BufferedReader::readText)
        val processVirtualABRetrofit = Runtime.getRuntime().exec("getprop ro.virtual_ab.retrofit").inputStream.bufferedReader().use(BufferedReader::readText)
        val processBootSlotSuffix = Runtime.getRuntime().exec("getprop ro.boot.slot_suffix").inputStream.bufferedReader().use(BufferedReader::readText)
        val processBuildABEnabled = Runtime.getRuntime().exec("getprop ro.build.enabled").inputStream.bufferedReader().use(BufferedReader::readText)

        val virtualABEnabled = processBootSlotSuffix.substring(0, processVirtualABEnabled.length - 1)
        val virtualABRetrofit = processBootSlotSuffix.substring(0, processVirtualABRetrofit.length - 1)
        val bootSlotSuffix = processBootSlotSuffix.substring(0, processBootSlotSuffix.length - 1)
        val buildABEnabled = processBootSlotSuffix.substring(0, processBuildABEnabled.length - 1)

        if (virtualABEnabled == "true" && virtualABRetrofit == "false") {
            partitionStatusInt = 1
            return applicationContext.getString(R.string.virtual_a_b_partitioning)
        }
        if (bootSlotSuffix.isNotEmpty() || buildABEnabled == "true") {
            partitionStatusInt = 2
            return applicationContext.getString(R.string.legacy_a_b_partitioning)
        }
        return applicationContext.getString(R.string.not_supported)
    }

    private fun seamlessUpdate() : String {
        return when (partitionStatusInt) {
            1,2 -> applicationContext.getString(R.string.supported)
            else -> applicationContext.getString(R.string.not_supported)
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
    private fun systemMount() : String {
        val mountsPoints = mountPoints()
        val systemOnBlock = mountsPoints.none { it.device != "none" && it.mountPoint == "/system" && it.fileSystem != "tmpfs"}
        val deviceMountedOnRoot = mountsPoints.any { it.device == "/dev/root" && it.mountPoint == "/" }
        val systemOnRoot = mountsPoints.any { it.mountPoint == "/system_root" && it.fileSystem != "tmpfs" }
        return if (systemOnBlock || deviceMountedOnRoot || systemOnRoot) {
            applicationContext.getString(R.string.supported)
        } else {
            applicationContext.getString(R.string.not_supported)
        }
    }

    private fun systemMountMethod() : String {
        val mountsPoints = mountPoints()
        val systemOnBlock = mountsPoints.none { it.device != "none" && it.mountPoint == "/system" && it.fileSystem != "tmpfs"}
        val deviceMountedOnRoot = mountsPoints.any { it.device == "/dev/root" && it.mountPoint == "/" }
        val systemOnRoot = mountsPoints.any { it.mountPoint == "/system_root" && it.fileSystem != "tmpfs" }
        return when {
            systemOnRoot -> { applicationContext.getString(R.string.mounted_like_system_as_root) }
            deviceMountedOnRoot -> { applicationContext.getString(R.string.mounted_like_device_as_root) }
            systemOnBlock -> { applicationContext.getString(R.string.mounted_like_system_on_block) }
            else -> { applicationContext.getString(R.string.not_supported) }
        }
    }

}