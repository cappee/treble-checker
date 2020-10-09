package dev.cappee.treble.fragment

import android.app.ActivityManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.Fragment
import com.google.android.material.textview.MaterialTextView
import dev.cappee.treble.R
import dev.cappee.treble.helper.DeviceHelper


class DeviceFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_device, container, false)

        //Binding elements
        val textViewBrandModelCodename: MaterialTextView = view.findViewById(R.id.textview_brand_model_codename)
        val textViewBattery: MaterialTextView = view.findViewById(R.id.textview_battery)
        val textViewProcessor: MaterialTextView = view.findViewById(R.id.textview_processor)
        val textViewVideoCard: MaterialTextView = view.findViewById(R.id.textview_video_card)
        val textViewArchitecture: MaterialTextView = view.findViewById(R.id.textview_architecture)
        val textViewRAM: MaterialTextView = view.findViewById(R.id.textview_ram)
        val textViewInternalMemory: MaterialTextView = view.findViewById(R.id.textview_internal_memory)
        val textViewExternalMemory: MaterialTextView = view.findViewById(R.id.textview_external_memory)
        val textViewDisplayDimension: MaterialTextView = view.findViewById(R.id.textview_display_dimensions)
        val textViewDisplayResolution: MaterialTextView = view.findViewById(R.id.textview_display_resolution)
        val textViewDisplayDPI: MaterialTextView = view.findViewById(R.id.textview_display_dpi)
        val textViewRefreshRate: MaterialTextView = view.findViewById(R.id.textview_refresh_rate)

        //Setting helper values on textviews
        Thread {
            textViewBrandModelCodename.text = DeviceHelper.identification()
            textViewBattery.text = DeviceHelper.batteryCapacityExperimental(requireContext())
            textViewProcessor.text = DeviceHelper.cpu()
            textViewArchitecture.text = DeviceHelper.cpuArch()
            textViewRAM.text = DeviceHelper.totalRam(requireContext().getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager)
            textViewInternalMemory.text = DeviceHelper.internalStorage(requireContext())
            textViewExternalMemory.text = DeviceHelper.externalStorage(requireContext())
            textViewDisplayDimension.text = DeviceHelper.displaySize(requireContext().getSystemService(Context.WINDOW_SERVICE) as WindowManager)
            textViewDisplayResolution.text = DeviceHelper.displayResolution(requireContext().getSystemService(Context.WINDOW_SERVICE) as WindowManager)
            textViewDisplayDPI.text = DeviceHelper.displayDPI(requireContext().getSystemService(Context.WINDOW_SERVICE) as WindowManager)
            textViewRefreshRate.text = DeviceHelper.displayRefreshRate(requireContext().getSystemService(Context.WINDOW_SERVICE) as WindowManager)
        }.run()
        textViewVideoCard.text = arguments?.getString("GPU_INFO")

        return view
    }

}