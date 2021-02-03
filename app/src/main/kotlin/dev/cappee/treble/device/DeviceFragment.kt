package dev.cappee.treble.device

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import dev.cappee.treble.R
import dev.cappee.treble.main.recycler.RecyclerViewAdapter
import dev.cappee.treble.databinding.FragmentMainBinding
import dev.cappee.treble.main.MainViewModel
import dev.cappee.treble.main.recycler.ItemDecoration

class DeviceFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //Init ViewModel
        val viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        //Init RecyclerView
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            addItemDecoration(ItemDecoration(resources.getDimensionPixelSize(R.dimen.recycler_items_margin)))
        }

        //Observe LiveData and update adapter
        viewModel.liveDataDevice.observe(viewLifecycleOwner, {
            binding.recyclerView.adapter = RecyclerViewAdapter(context,
                arrayOf(R.string.general, R.string.chipset, R.string.memory, R.string.display),
                arrayOf(
                    arrayOf(R.string.identification, R.string.battery),
                    arrayOf(R.string.processor, R.string.graphic_card, R.string.architecture),
                    arrayOf(R.string.ram, R.string.intenal_memory, R.string.external_memory),
                    arrayOf(R.string.dimensions, R.string.display_resolution, R.string.dpi, R.string.refresh_rate)),
                arrayOf(
                    arrayOf(it.identifier, it.battery),
                    arrayOf(it.cpu, it.gpu, it.arch),
                    arrayOf(it.ram, it.internalMemory, it.externalMemory),
                    arrayOf(it.screenSize, it.screenResolution, it.dpi, it.refreshRate)),
                emptyArray())
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}