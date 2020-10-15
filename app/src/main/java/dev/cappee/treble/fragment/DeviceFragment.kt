package dev.cappee.treble.fragment

import android.app.ActivityManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import dev.cappee.treble.R
import dev.cappee.treble.adapter.RecyclerViewAdapter
import dev.cappee.treble.helper.DeviceHelper
import kotlinx.android.synthetic.main.fragment_device.*
import kotlin.concurrent.thread

class DeviceFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_device, container, false)

        val titles: Array<Int> = arrayOf(R.string.general, R.string.chipset, R.string.memory, R.string.display)
        val subtitleGeneral: Array<Int> = arrayOf(R.string.identification, R.string.battery)
        val subtitleChipset: Array<Int> = arrayOf(R.string.processor, R.string.graphic_card, R.string.architecture)
        val subtitleMemory: Array<Int> = arrayOf(R.string.ram, R.string.intenal_memory, R.string.external_memory)
        val subtitleDisplay: Array<Int> = arrayOf(R.string.dimensions, R.string.display_resolution, R.string.dpi, R.string.refresh_rate)
        thread {
            val dataGeneral: Array<String> = arrayOf(DeviceHelper.identification(),
                DeviceHelper.batteryCapacityExperimental(context!!))
            val dataChipset: Array<String> = arrayOf(DeviceHelper.cpu(),
                arguments?.getString("GPU_INFO").toString(), DeviceHelper.cpuArch())
            val dataMemory: Array<String> = arrayOf(DeviceHelper.totalRam(context?.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager),
                DeviceHelper.internalStorage(context!!),
                DeviceHelper.externalStorage(context!!))
            val dataDisplay: Array<String> = arrayOf(DeviceHelper.displaySize(context?.getSystemService(Context.WINDOW_SERVICE) as WindowManager),
                DeviceHelper.displayResolution(context?.getSystemService(Context.WINDOW_SERVICE) as WindowManager),
                DeviceHelper.displayDPI(context?.getSystemService(Context.WINDOW_SERVICE) as WindowManager),
                DeviceHelper.displayRefreshRate(context?.getSystemService(Context.WINDOW_SERVICE) as WindowManager))
            runOnUiThread {
                recyclerViewDevice.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                recyclerViewDevice.adapter = RecyclerViewAdapter(context,
                    titles,
                    arrayOf(subtitleGeneral, subtitleChipset, subtitleMemory, subtitleDisplay),
                    arrayOf(dataGeneral, dataChipset, dataMemory, dataDisplay),
                    null
                )
            }
        }

        return view
    }

    private fun Fragment?.runOnUiThread(action: () -> Unit) {
        this ?: return
        if (!isAdded) return
        activity?.runOnUiThread(action)
    }

}