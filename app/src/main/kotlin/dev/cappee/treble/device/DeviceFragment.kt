package dev.cappee.treble.device

import android.app.ActivityManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import dev.cappee.treble.R
import dev.cappee.treble.adapter.RecyclerViewAdapter
import dev.cappee.treble.databinding.FragmentDeviceBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DeviceFragment : Fragment() {

    private var _binding: FragmentDeviceBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDeviceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val titles: Array<Int> = arrayOf(R.string.general, R.string.chipset, R.string.memory, R.string.display)
        val subtitleGeneral: Array<Int> = arrayOf(R.string.identification, R.string.battery)
        val subtitleChipset: Array<Int> = arrayOf(R.string.processor, R.string.graphic_card, R.string.architecture)
        val subtitleMemory: Array<Int> = arrayOf(R.string.ram, R.string.intenal_memory, R.string.external_memory)
        val subtitleDisplay: Array<Int> = arrayOf(R.string.dimensions, R.string.display_resolution, R.string.dpi, R.string.refresh_rate)
        lifecycleScope.launch(Dispatchers.Main) {
            val dataGeneral: Array<String> = arrayOf(
                DeviceHelper.identification(),
                DeviceHelper.batteryCapacityExperimental(context!!))
            val dataChipset: Array<String> = arrayOf(
                DeviceHelper.cpu(),
                arguments?.getString("GPU_INFO").toString(), DeviceHelper.cpuArch())
            val dataMemory: Array<String> = arrayOf(
                DeviceHelper.totalRam(context?.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager),
                DeviceHelper.internalStorage(context!!),
                DeviceHelper.externalStorage(context!!))
            DeviceHelper.initDisplay(context!!)
            val dataDisplay: Array<String> = arrayOf(
                DeviceHelper.displaySize(),
                DeviceHelper.displayResolution(),
                DeviceHelper.displayDPI(),
                DeviceHelper.displayRefreshRate(context?.getSystemService(Context.WINDOW_SERVICE) as WindowManager))
            binding.recyclerViewDevice.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                adapter = RecyclerViewAdapter(context,
                    titles,
                    arrayOf(subtitleGeneral, subtitleChipset, subtitleMemory, subtitleDisplay),
                    arrayOf(dataGeneral, dataChipset, dataMemory, dataDisplay),
                    emptyArray()
                )
            }
            binding.progressBarDevice.visibility = ViewGroup.INVISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}