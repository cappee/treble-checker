package dev.cappee.treble.helper

import android.content.Context
import dev.cappee.treble.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.File

object RootHelper {

    suspend fun rootPermissions() : Int {
        return withContext(Dispatchers.Default) {
            val possiblePath = arrayOf(
                "/sbin/",
                "/system/bin/",
                "/system/xbin/",
                "/data/local/xbin/",
                "/data/local/bin/",
                "/system/sd/xbin/",
                "/system/bin/failsafe/",
                "/data/local/"
            )
            for (path in possiblePath) {
                if (File(path + "su").exists()) {
                    R.string.available
                }
            }
            R.string.not_available
        }
    }

    suspend fun rootPath(context: Context) : String {
        return withContext(Dispatchers.Default) {
            val possiblePath = arrayOf(
                "/sbin/",
                "/system/bin/",
                "/system/xbin/",
                "/data/local/xbin/",
                "/data/local/bin/",
                "/system/sd/xbin/",
                "/system/bin/failsafe/",
                "/data/local/"
            )
            for (path in possiblePath) {
                if (File(path + "su").exists()) {
                    path + "su"
                }
            }
            context.getString(R.string.no_root_path_found)
        }
    }

    suspend fun busyBoxInstalled(context: Context) : String {
        return withContext(Dispatchers.Default) {
            try {
                val line = Runtime.getRuntime().exec("busybox").inputStream.bufferedReader().use(BufferedReader::readLine)
                val version = line.split("\\s+".toRegex()).toTypedArray()[1]
                context.getString(R.string.present) + ", " + version.subSequence(1, 7)
            } catch (e: Exception) {
                context.getString(R.string.not_installed)
            }
        }
    }

    suspend fun busyBoxBuildDate(context: Context) : String {
        return withContext(Dispatchers.Default) {
            try {
                val line = Runtime.getRuntime().exec("busybox").inputStream.bufferedReader().use(BufferedReader::readLine)
                val date = line.substringAfter("(", "")
                date.subSequence(0, 10).toString()
            } catch (e: Exception) {
                context.getString(R.string.not_installed)
            }
        }
    }

}