package dev.cappee.treble.root

import android.content.Context
import dev.cappee.treble.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import java.io.BufferedReader
import java.io.File

object RootHelper {

    suspend fun get(context: Context) = coroutineScope {
        async(Dispatchers.Default) { Root(
            rootPermissions(context),
            rootPath(context),
            busyBoxInstalled(context),
            busyBoxBuildDate(context)
        ) }
    }.await()

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

    private fun rootPermissions(context: Context) : String {
        println("THREAD ROOT: ${Thread.currentThread()}")
        for (path in possiblePath) {
            if (File(path + "su").exists()) {
                return context.getString(R.string.available)
            }
        }
        return context.getString(R.string.not_available)
    }

    private fun rootPath(context: Context) : String {
        for (path in possiblePath) {
            if (File(path + "su").exists()) {
                return path + "su"
            }
        }
        return context.getString(R.string.no_root_path_found)
    }

    private fun busyBoxInstalled(context: Context) : String {
        return try {
            val line = Runtime.getRuntime().exec("busybox").inputStream.bufferedReader().use(BufferedReader::readLine)
            val version = line.split("\\s+".toRegex()).toTypedArray()[1]
            context.getString(R.string.present) + ", " + version.subSequence(1, 7)
        } catch (e: Exception) {
            context.getString(R.string.not_installed)
        }
    }

    private fun busyBoxBuildDate(context: Context) : String {
        return try {
            val line = Runtime.getRuntime().exec("busybox").inputStream.bufferedReader().use(BufferedReader::readLine)
            val date = line.substringAfter("(", "")
            date.subSequence(0, 10).toString()
        } catch (e: Exception) {
            context.getString(R.string.not_installed)
        }
    }

}