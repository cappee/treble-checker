package dev.cappee.treble.helper

import android.app.ActivityManager
import android.content.Context
import android.graphics.Point
import android.os.BatteryManager
import android.os.Build
import android.os.Environment
import android.os.StatFs
import android.util.DisplayMetrics
import android.view.Display
import android.view.WindowManager
import dev.cappee.treble.R
import java.io.File
import java.io.FileNotFoundException
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.pow
import kotlin.math.sqrt


class DeviceHelper {

    companion object {

        fun identification() : String {
            return Build.MANUFACTURER + " " + Build.MODEL + " (" + Build.DEVICE + ")"
        }

        fun batteryCapacity(context: Context) : String {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val batteryManager = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
                val chargeCounter = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER)
                val capacity = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
                return "~" + (((chargeCounter / 1000) * 100) / capacity).toString() + " mAh"
            }
            return context.getString(R.string.api_21_required)
        }

        //Ported method from DroidInfo (https://github.com/gabrielecappellaro/DroidInfo)
        fun cpu() : String {
            val map: MutableMap<String, String> = HashMap()
            try {
                val scanner = Scanner(File("/proc/cpuinfo"))
                while (scanner.hasNextLine()) {
                    val vals = scanner.nextLine().split(": ".toRegex()).toTypedArray()
                    if (vals.size > 1) map[vals[0].trim { it <= ' ' }] = vals[1].trim { it <= ' ' }
                }
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }
            return if (map["Hardware"] != null) {
                map["Hardware"].toString()
            } else {
                map["model name"].toString()
            }
        }

        fun cpuArch() : String {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Build.SUPPORTED_ABIS[0]
            } else {
                Build.CPU_ABI
            }
        }

        //Ported (and simplified) from DroidInfo (https://github.com/gabrielecappellaro/DroidInfo)
        fun totalRam(activityManager: ActivityManager) : String {
            val memoryInfo = ActivityManager.MemoryInfo()
            activityManager.getMemoryInfo(memoryInfo)
            val ram = memoryInfo.totalMem
            return if ((ram / 1073741824.0) > 1) {
                String.format("%.1f", ram / 1073741824.0) + " Gb"
            } else {
                String.format("%.1f", ram / 1048576.0) + " Mb"
            }
        }

        fun internalStorage(context: Context) : String {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                val root = Environment.getRootDirectory()
                val data = Environment.getDataDirectory()
                val sizeBytes = StatFs(root.path).totalBytes + StatFs(data.path).totalBytes
                String.format("%.1f", sizeBytes / 1073741824.0) + " Gb (" + String.format("%.1f", StatFs(root.path).totalBytes / 1073741824.0) + "gb " + context.getString(R.string.grammatical_particle_of) + " /root)"
            } else {
                context.getString(R.string.api_18_required)
            }
        }

        fun externalStorage(context: Context) : String {
            if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED || Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED_READ_ONLY) {
                when {
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP -> {
                        val externalDirs: Array<File> = context.getExternalFilesDirs(null)
                        val resultDirs = ArrayList<String>()
                        for (file in externalDirs) {
                            val path: String = file.path.split("/Android")[0]
                            if ( Environment.isExternalStorageRemovable(file)) {
                                resultDirs.add(path)
                            }
                        }
                        var storageDirectories = ""
                        for (i in 0 until resultDirs.size)
                            storageDirectories += resultDirs[i]
                        val ext = StatFs(storageDirectories)
                        return String.format("%.1f", ext.totalBytes / 1073741824.0) + " Gb"
                    }
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2 -> {
                        val ext = StatFs(Environment.getExternalStorageDirectory().path)
                        return String.format("%.1f", ext.totalBytes / 1073741824.0) + " Gb"
                    }
                    else -> {
                        return context.getString(R.string.api_18_required)
                    }
                }
            } else {
                return context.getString(R.string.not_mounted)
            }
        }

        //Ported method from DroidInfo (https://github.com/gabrielecappellaro/DroidInfo)
        fun displaySize(windowManager: WindowManager) : String {
            val display = windowManager.defaultDisplay
            val displayMetrics = DisplayMetrics()
            display.getMetrics(displayMetrics)
            val point = Point()
            Display::class.java.getMethod("getRealSize", Point::class.java).invoke(display, point)
            val x = (point.x / displayMetrics.xdpi).toDouble().pow(2.0)
            val y = (point.y / displayMetrics.ydpi).toDouble().pow(2.0)
            return String.format("%.1f", sqrt(x + y)) + "\""
        }

        //Ported method from DroidInfo (https://github.com/gabrielecappellaro/DroidInfo)
        fun displayResolution(windowManager: WindowManager) : String {
            /*val displayMetrics: DisplayMetrics = DisplayMetrics()
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                context?.display?.getRealMetrics(displayMetrics)
            } else {
                val windowManager: WindowManager = context?.getSystemService(Context.WINDOW_SERVICE) as WindowManager
                val display: Display? = windowManager.defaultDisplay
                display?.getRealMetrics(displayMetrics)
            }*/
            val display: Display = windowManager.defaultDisplay
            val point = Point()
            display.getRealSize(point)
            return "" + point.y + "x" + point.x
        }

        //Ported method from DroidInfo (https://github.com/gabrielecappellaro/DroidInfo)
        fun displayDPI(windowManager: WindowManager) : String {
            val display = windowManager.defaultDisplay
            val displayMetrics = DisplayMetrics()
            display.getMetrics(displayMetrics)
            return displayMetrics.densityDpi.toString()
        }

        //Ported method from DroidInfo (https://github.com/gabrielecappellaro/DroidInfo)
        fun displayRefreshRate(windowManager: WindowManager) : String {
            val display = windowManager.defaultDisplay
            return String.format("%.0f", display.refreshRate) + " Hz"
        }

        fun batteryCapacityExperimental(context: Context) : String {
            var powerProfile_: Any? = null
            val POWER_PROFILE_CLASS = "com.android.internal.os.PowerProfile"
            try {
                powerProfile_ = Class.forName(POWER_PROFILE_CLASS)
                    .getConstructor(Context::class.java).newInstance(context)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            var batteryCapacity = 0.0
            try {
                batteryCapacity = Class.forName(POWER_PROFILE_CLASS).getMethod("getAveragePower", String::class.java).invoke(powerProfile_, "battery.capacity") as Double
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return "$batteryCapacity mAh"
        }

    }

}