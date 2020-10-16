package dev.cappee.treble.helper

import android.content.Context
import dev.cappee.treble.R
import java.io.File

class RootHelper {

    companion object {

        fun rootPermissions() : Int {
            val possiblePath = arrayOf("/sbin/", "/system/bin/", "/system/xbin/", "/data/local/xbin/", "/data/local/bin/", "/system/sd/xbin/", "/system/bin/failsafe/", "/data/local/")
            for (path in possiblePath) {
                if (File(path + "su").exists()) {
                    return R.string.available
                }
            }
            return R.string.not_available
        }

        fun rootPath(context: Context) : String {
            val possiblePath = arrayOf("/sbin/", "/system/bin/", "/system/xbin/", "/data/local/xbin/", "/data/local/bin/", "/system/sd/xbin/", "/system/bin/failsafe/", "/data/local/")
            for (path in possiblePath) {
                if (File(path + "su").exists()) {
                    return path + "su"
                }
            }
            return context.getString(R.string.no_root_path_found)
        }

        /*fun superuserApplication(context: Context) : String {

        }*/
    }

}