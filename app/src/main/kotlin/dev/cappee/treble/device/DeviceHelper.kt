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
import dev.cappee.treble.settings.SettingsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.io.File
import java.io.FileNotFoundException
import java.util.*
import kotlin.collections.HashMap
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sqrt

object DeviceHelper {

    suspend fun get() = coroutineScope {
        settingsRepository = SettingsRepository(applicationContext)
        async(Dispatchers.Default) { Device(
            identifier(),
            batteryCapacity(),
            cpu() as String,
            gpu ?: applicationContext.getString(R.string.error_report_this_please),
            cpuArch(),
            totalRam(),
            internalStorage(),
            externalStorage(),
            displaySize(),
            displayResolution(),
            displayDPI(),
            displayRefreshRate()
        ) }
    }.await()

    @Suppress("DEPRECATION")
    fun init(context: Context) : DeviceHelper {
        applicationContext = context.applicationContext
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            display = context.display ?: (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
            context.display?.getRealMetrics(displayMetrics)
            context.display?.getRealSize(displayPoint)
        } else {
            val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            display = windowManager.defaultDisplay
            windowManager.defaultDisplay.getRealMetrics(displayMetrics)
            windowManager.defaultDisplay.getRealSize(displayPoint)
        }
        return this
    }

    private lateinit var applicationContext: Context
    private lateinit var settingsRepository: SettingsRepository

    val possibleIdentifierOrder: List<CharSequence> = listOf(
        "${Build.MANUFACTURER} ${Build.MODEL} (${Build.DEVICE})",
        "${Build.MANUFACTURER} ${Build.DEVICE} (${Build.MODEL})",
        "${Build.DEVICE} ${Build.MODEL} (${Build.MANUFACTURER})",
        "${Build.MODEL} ${Build.DEVICE} (${Build.MANUFACTURER})",
        "${Build.DEVICE} ${Build.MANUFACTURER} (${Build.MODEL})",
        "${Build.MODEL} ${Build.MANUFACTURER} (${Build.DEVICE})"
    )

    private suspend fun identifier() : String {
        return possibleIdentifierOrder[settingsRepository.getIdentifierOrder().first()] as String
    }

    private suspend fun batteryCapacity() : String {
        return if (!settingsRepository.getBatteryFetchModeExperimental().first()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val batteryManager = applicationContext.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
                val chargeCounter = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER)
                val capacity = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
                "~" + (((chargeCounter / 1000) * 100) / capacity).toString() + " mAh"
            } else {
                applicationContext.getString(R.string.api_21_required)
            }
        } else {
            batteryCapacityExperimental()
        }
    }

    fun getCpuIndexByString(value: String) : Int {
        return when (value) {
            "Hardware" -> 0
            "Processor" -> 1
            "model name" -> 2
            else -> 0
        }
    }

    //Ported method from DroidInfo (https://github.com/cappee/DroidInfo)
    fun cpu(list: Boolean? = null ?: false, value: String? = null) : Any {
        settingsRepository = SettingsRepository(applicationContext)
        val map: MutableMap<String, String> = HashMap()
        try {
            val scanner = Scanner(File("/proc/cpuinfo"))
            while (scanner.hasNextLine()) {
                val split = scanner.nextLine().split(": ".toRegex()).toTypedArray()
                if (split.size > 1) map[split[0].trim { it <= ' ' }] = split[1].trim { it <= ' ' }
            }
        } catch (e: FileNotFoundException) {
            throw e
        }
        val possibleCpuEntries: MutableMap<String, CharSequence> = mutableMapOf(
            Pair("Hardware", map["Hardware"] ?: ""),
            Pair("Processor", map["Processor"] ?: ""),
            Pair("model name", map["model name"] ?: "")
        )
        return when {
            list!! -> {
                possibleCpuEntries
            }
            value != null -> {
                possibleCpuEntries[value] ?: ""
            }
            else -> {
                possibleCpuEntries[runBlocking { settingsRepository.getProcessorShownAs().first() }] ?: applicationContext.getString(R.string.error_report_this_please)
            }
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

    var gpu: String? = null

    //Ported (and simplified) from DroidInfo (https://github.com/cappee/DroidInfo)
    private fun totalRam() : String {
        val memoryInfo = ActivityManager.MemoryInfo()
        (applicationContext.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager).getMemoryInfo(memoryInfo)
        val ram = memoryInfo.totalMem
        return if ((ram / 1073741824.0) > 1) {
            String.format("%.1f", ram / 1073741824.0) + " Gb"
        } else {
            String.format("%.1f", ram / 1048576.0) + " Mb"
        }
    }

    private fun internalStorage() : String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            val root = Environment.getRootDirectory()
            val data = Environment.getDataDirectory()
            val sizeBytes = StatFs(root.path).totalBytes + StatFs(data.path).totalBytes
            String.format("%.1f", sizeBytes / 1073741824.0) + " Gb (" + String.format("%.1f", StatFs(root.path).totalBytes / 1073741824.0) + "gb " + applicationContext.getString(R.string.grammatical_particle_of) + " /root)"
        } else {
            applicationContext.getString(R.string.api_18_required)
        }
    }

    @Suppress("DEPRECATION")
    private fun externalStorage() : String {
        if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED || Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED_READ_ONLY) {
            when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP -> {
                    val externalDirs: Array<File> = applicationContext.getExternalFilesDirs(null)
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
                        return applicationContext.getString(R.string.not_mounted)
                    }
                    return String.format("%.1f", ext.totalBytes / 1073741824.0) + " Gb"
                }
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2 -> {
                    val ext: StatFs
                    try {
                        ext = StatFs(Environment.getExternalStorageDirectory().path)
                    } catch (e: Exception) {
                        return applicationContext.getString(R.string.not_mounted)
                    }
                    return String.format("%.1f", ext.totalBytes / 1073741824.0) + " Gb"
                }
                else -> {
                    return applicationContext.getString(R.string.api_18_required)
                }
            }
        } else {
            return applicationContext.getString(R.string.not_mounted)
        }
    }

    private lateinit var display: Display
    private val displayMetrics: DisplayMetrics = DisplayMetrics()
    private val displayPoint: Point = Point()

    //Ported method from DroidInfo (https://github.com/cappee/DroidInfo)
    private fun displaySize() : String {
        val x = (displayPoint.x / displayMetrics.xdpi).toDouble().pow(2.0)
        val y = (displayPoint.y / displayMetrics.ydpi).toDouble().pow(2.0)
        return String.format("%.1f", sqrt(x + y)) + "\""
    }

    //Ported method from DroidInfo (https://github.com/cappee/DroidInfo)
    private fun displayResolution(): String {
        val resolution = "${displayMetrics.heightPixels}x${displayMetrics.widthPixels}"
        return if (Build.VERSION.SDK_INT >= 24) {
            if (display.isHdr) {
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
        return if (Build.VERSION.SDK_INT >= 23) {
            var refreshRate: Float = display.supportedModes[0].refreshRate
            for (mode in display.supportedModes) {
                if (mode.refreshRate > refreshRate) {
                    refreshRate = mode.refreshRate
                }
            }
            "${refreshRate.roundToInt()} Hz"
        } else {
            "${display.refreshRate.roundToInt()} Hz"
        }
    }

    private fun batteryCapacityExperimental() : String {
        val powerProfile: Any?
        try {
            powerProfile = Class.forName("com.android.internal.os.PowerProfile")
                .getConstructor(Context::class.java).newInstance(applicationContext)
        } catch (e: Exception) {
            return applicationContext.getString(R.string.not_found)
        }
        val batteryCapacity: Double
        return try {
            batteryCapacity = Class.forName("com.android.internal.os.PowerProfile")
                .getMethod("getAveragePower", String::class.java)
                .invoke(powerProfile, "battery.capacity") as Double
            "${batteryCapacity.roundToInt()} mAh"
        } catch (e: Exception) {
            applicationContext.getString(R.string.not_found)
        }
    }

}