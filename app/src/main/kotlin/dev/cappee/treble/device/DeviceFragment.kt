package dev.cappee.treble.device

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import dev.cappee.treble.R
import dev.cappee.treble.main.recycler.RecyclerViewAdapter
import dev.cappee.treble.databinding.FragmentMainBinding
import dev.cappee.treble.main.recycler.ItemDecoration
import dev.cappee.treble.treble.Treble
import dev.cappee.treble.treble.TrebleFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DeviceFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    companion object {
        private const val DATA = "DATA"

        fun newInstance(device: Device) = DeviceFragment().apply {
            val bundle = Bundle()
            bundle.putParcelable(DATA, device)
            arguments = bundle
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val titles: Array<Int> = arrayOf(R.string.general, R.string.chipset, R.string.memory, R.string.display)
        val subtitleGeneral: Array<Int> = arrayOf(R.string.identification, R.string.battery)
        val subtitleChipset: Array<Int> = arrayOf(R.string.processor, R.string.graphic_card, R.string.architecture)
        val subtitleMemory: Array<Int> = arrayOf(R.string.ram, R.string.intenal_memory, R.string.external_memory)
        val subtitleDisplay: Array<Int> = arrayOf(R.string.dimensions, R.string.display_resolution, R.string.dpi, R.string.refresh_rate)
        val device: Device = arguments?.getParcelable(DATA)!!
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            addItemDecoration(ItemDecoration(resources.getDimensionPixelSize(R.dimen.recycler_items_margin)))
            adapter = RecyclerViewAdapter(context,
                titles,
                arrayOf(subtitleGeneral, subtitleChipset, subtitleMemory, subtitleDisplay),
                arrayOf(
                    arrayOf(device.identifier, device.battery),
                    arrayOf(device.cpu, device.gpu, device.arch),
                    arrayOf(device.ram, device.internalMemory, device.externalMemory),
                    arrayOf(device.screenSize, device.screenResolution, device.dpi, device.refreshRate)),
                emptyArray())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}