package dev.cappee.treble.device

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
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dev.cappee.treble.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import java.io.File
import java.io.FileNotFoundException
import java.util.*
import kotlin.collections.HashMap
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sqrt

object DeviceHelper {

    suspend fun get(context: Context) = coroutineScope {
        initDisplay(context, context.getSystemService(Context.WINDOW_SERVICE) as WindowManager)
        async(Dispatchers.Default) { Device(
            identifier(),
            batteryCapacityExperimental(context),
            cpu(),
            gpu,
            cpuArch(),
            totalRam(context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager),
            internalStorage(context),
            externalStorage(context),
            displaySize(),
            displayResolution(),
            displayDPI(),
            displayRefreshRate()
        ) }
    }.await()

    private fun identifier() : String {
        println("THREAD DEVICE: ${Thread.currentThread()}")
        return Build.MANUFACTURER + " " + Build.DEVICE + " (" + Build.MODEL + ")"
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

    //Ported method from DroidInfo (https://github.com/cappee/DroidInfo)
    private fun cpu() : String {
        val map: MutableMap<String, String> = HashMap()
        try {
            val scanner = Scanner(File("/proc/cpuinfo"))
            while (scanner.hasNextLine()) {
                val split = scanner.nextLine().split(": ".toRegex()).toTypedArray()
                if (split.size > 1) map[split[0].trim { it <= ' ' }] = split[1].trim { it <= ' ' }
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

    @Suppress("DEPRECATION")
    private fun cpuArch() : String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Build.SUPPORTED_ABIS[0]
        } else {
            Build.CPU_ABI
        }
    }

    lateinit var gpu: String

    //Ported (and simplified) from DroidInfo (https://github.com/cappee/DroidInfo)
    private fun totalRam(activityManager: ActivityManager) : String {
        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memoryInfo)
        val ram = memoryInfo.totalMem
        return if ((ram / 1073741824.0) > 1) {
            String.format("%.1f", ram / 1073741824.0) + " Gb"
        } else {
            String.format("%.1f", ram / 1048576.0) + " Mb"
        }
    }

    private fun internalStorage(context: Context) : String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            val root = Environment.getRootDirectory()
            val data = Environment.getDataDirectory()
            val sizeBytes = StatFs(root.path).totalBytes + StatFs(data.path).totalBytes
            String.format("%.1f", sizeBytes / 1073741824.0) + " Gb (" + String.format("%.1f", StatFs(root.path).totalBytes / 1073741824.0) + "gb " + context.getString(R.string.grammatical_particle_of) + " /root)"
        } else {
            context.getString(R.string.api_18_required)
        }
    }

    @Suppress("DEPRECATION")
    private fun externalStorage(context: Context) : String {
        if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED || Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED_READ_ONLY) {
            when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP -> {
                    val externalDirs: Array<File> = context.getExternalFilesDirs(null)
                    var resultDirs: Array<String> = emptyArray()
                    for (file in externalDirs) {
                        val path: String = file.path.split("/Android")[0]
                        if (Environment.isExternalStorageRemovable(file)) {
                            resultDirs += path
                        }
                    }
                    var storageDirectories = ""
                    for (element in resultDirs)
                        storageDirectories += element
                    FirebaseCrashlytics.getInstance().log(storageDirectories)
                    val ext: StatFs
                    try {
                        ext = StatFs(storageDirectories)
                    } catch (e: Exception) {
                        return context.getString(R.string.not_mounted)
                    }
                    return String.format("%.1f", ext.totalBytes / 1073741824.0) + " Gb"
                }
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2 -> {
                    val ext: StatFs
                    try {
                        ext = StatFs(Environment.getExternalStorageDirectory().path)
                    } catch (e: Exception) {
                        return context.getString(R.string.not_mounted)
                    }
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

    private lateinit var display: Display
    private val displayMetrics: DisplayMetrics = DisplayMetrics()
    private val displayPoint: Point = Point()

    @Suppress("DEPRECATION")
    private fun initDisplay(context: Context, windowManager: WindowManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            display = context.display ?: windowManager.defaultDisplay
            context.display?.getRealMetrics(displayMetrics)
            context.display?.getRealSize(displayPoint)
        } else {
            display = windowManager.defaultDisplay
            windowManager.defaultDisplay.getRealMetrics(displayMetrics)
            windowManager.defaultDisplay.getRealSize(displayPoint)
        }
    }

    //Ported method from DroidInfo (https://github.com/cappee/DroidInfo)
    private fun displaySize() : String {
        val x = (displayPoint.x / displayMetrics.xdpi).toDouble().pow(2.0)
        val y = (displayPoint.y / displayMetrics.ydpi).toDouble().pow(2.0)
        return String.format("%.1f", sqrt(x + y)) + "\""
    }

    //Ported method from DroidInfo (https://github.com/cappee/DroidInfo)
    private fun displayResolution(): String {
        val resolution = "${displayMetrics.heightPixels}x${displayMetrics.widthPixels}"
        return if (display.isHdr) {
            when {
                display.hdrCapabilities.supportedHdrTypes.contains(Display.HdrCapabilities.HDR_TYPE_HDR10_PLUS) -> {
                    "$resolution (HDR10+)"
                }
                display.hdrCapabilities.supportedHdrTypes.contains(Display.HdrCapabilities.HDR_TYPE_HDR10) -> {
                    "$resolution (HDR10)"
                }
                else -> {
                    "$resolution (HDR)"
                }
            }
        } else {
            resolution
        }
    }

    //Ported method from DroidInfo (https://github.com/cappee/DroidInfo)
    private fun displayDPI(): String {
        return displayMetrics.densityDpi.toString()
    }

    //Ported method from DroidInfo (https://github.com/cappee/DroidInfo)
    private fun displayRefreshRate() : String {
        var refreshRate: Float = display.supportedModes[0].refreshRate
        for (mode in display.supportedModes) {
            if (mode.refreshRate > refreshRate) {
                refreshRate = mode.refreshRate
            }
        }
        return "${refreshRate.roundToInt()} Hz"
    }

    private fun batteryCapacityExperimental(context: Context) : String {
        val powerProfile: Any?
        try {
            powerProfile = Class.forName("com.android.internal.os.PowerProfile")
                .getConstructor(Context::class.java).newInstance(context)
        } catch (e: Exception) {
            return context.getString(R.string.not_found)
        }
        val batteryCapacity: Double
        try {
            batteryCapacity = Class.forName("com.android.internal.os.PowerProfile").getMethod("getAveragePower", String::class.java).invoke(powerProfile, "battery.capacity") as Double
        } catch (e: Exception) {
            return context.getString(R.string.not_found)
        }
        return "$batteryCapacity mAh"
    }

}