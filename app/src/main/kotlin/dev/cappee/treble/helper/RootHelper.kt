package dev.cappee.treble.helper

import android.content.Context
import dev.cappee.treble.R
import java.io.BufferedReader
import java.io.File

class RootHelper {

    companion object {

        fun rootPermissions() : Int {
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
                    return R.string.available
                }
            }
            return R.string.not_available
        }

        fun rootPath(context: Context) : String {
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
                    return path + "su"
                }
            }
            return context.getString(R.string.no_root_path_found)
        }

        fun busyBoxInstalled(context: Context) : String {
            return try {
                val line = Runtime.getRuntime().exec("busybox").inputStream.bufferedReader().use(BufferedReader::readLine)
                val version = line.split("\\s+".toRegex()).toTypedArray()[1]
                return context.getString(R.string.present) + ", " + version.subSequence(1, 7)
            } catch (e: Exception) {
                context.getString(R.string.not_installed)
            }
        }

        fun busyBoxBuildDate(context: Context) : String {
            return try {
                val line = Runtime.getRuntime().exec("busybox").inputStream.bufferedReader().use(BufferedReader::readLine)
                val date = line.substringAfter("(", "")
                return date.subSequence(0, 10).toString()
            } catch (e: Exception) {
                context.getString(R.string.not_installed)
            }
        }
    }

}