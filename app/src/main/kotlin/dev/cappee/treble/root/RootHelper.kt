package dev.cappee.treble.root

import android.content.Context
import dev.cappee.treble.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import java.io.BufferedReader
import java.io.File

object RootHelper {

    suspend fun get() = coroutineScope {
        async(Dispatchers.Default) { Root(
            rootPermissions(),
            rootPath(),
            busyBoxInstalled(),
            busyBoxBuildDate()
        ) }
    }.await()

    fun init(context: Context) {
        applicationContext = context.applicationContext
    }

    private lateinit var applicationContext: Context

    /*
     Hey you who are reading this code, update this array if you think that some are missing
     */
    private val possiblePath = arrayOf(
        "/sbin/",
        "/system/bin/",
        "/system/xbin/",
        "/data/local/xbin/",
        "/data/local/bin/",
        "/system/sd/xbin/",
        "/system/bin/failsafe/",
        "/data/local/"
    )

    private fun rootPermissions() : String {
        for (path in possiblePath) {
            if (File(path + "su").exists()) {
                return applicationContext.getString(R.string.available)
            }
        }
        return applicationContext.getString(R.string.not_available)
    }

    private fun rootPath() : String {
        for (path in possiblePath) {
            if (File(path + "su").exists()) {
                return path + "su"
            }
        }
        return applicationContext.getString(R.string.no_root_path_found)
    }

    private fun busyBoxInstalled() : String {
        return try {
            val line = Runtime.getRuntime().exec("busybox").inputStream.bufferedReader().use(BufferedReader::readLine)
            val version = line.split("\\s+".toRegex()).toTypedArray()[1]
            applicationContext.getString(R.string.present) + ", " + version.subSequence(1, 7)
        } catch (e: Exception) {
            applicationContext.getString(R.string.not_installed)
        }
    }

    private fun busyBoxBuildDate() : String {
        return try {
            val line = Runtime.getRuntime().exec("busybox").inputStream.bufferedReader().use(BufferedReader::readLine)
            val date = line.substringAfter("(", "")
            date.subSequence(0, 10).toString()
        } catch (e: Exception) {
            applicationContext.getString(R.string.not_installed)
        }
    }

}