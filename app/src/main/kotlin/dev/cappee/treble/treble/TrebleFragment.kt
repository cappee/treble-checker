package dev.cappee.treble.treble

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import dev.cappee.treble.R
import dev.cappee.treble.databinding.FragmentMainBinding
import dev.cappee.treble.device.DeviceHelper
import dev.cappee.treble.main.MainViewModel
import dev.cappee.treble.main.MainViewModelFactory
import dev.cappee.treble.main.recycler.ItemDecoration
import dev.cappee.treble.main.recycler.RecyclerViewAdapter
import dev.cappee.treble.root.RootHelper

class TrebleFragment : Fragment() {

    private val viewModel: MainViewModel by activityViewModels()

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //Init RecyclerView
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            addItemDecoration(ItemDecoration(resources.getDimensionPixelSize(R.dimen.recycler_items_margin)))
        }

        //Observe LiveData and update adapter
        viewModel.liveDataTreble.observe(viewLifecycleOwner, {
            binding.progressBar.visibility = ProgressBar.INVISIBLE
            binding.recyclerView.adapter = RecyclerViewAdapter(
                context,
                arrayOf(
                    R.string.project_treble,
                    R.string.a_b_partitioning,
                    R.string.system_as_root
                ),
                arrayOf(
                    arrayOf(R.string.status, R.string.treble_arch, R.string.vndk_version),
                    arrayOf(R.string.status, R.string.seamless_updates),
                    arrayOf(R.string.status, R.string.method)
                ),
                arrayOf(
                    arrayOf(it.trebleStatus, it.trebleArch, it.vndkVersion),
                    arrayOf(it.abStatus, it.seamlessUpdate),
                    arrayOf(it.sarStatus, it.sarMethod)
                ),
                arrayOf(
                    Pair(R.string.project_treble, R.string.project_treble_description),
                    Pair(R.string.a_b_partitioning, R.string.a_b_partitioning_description),
                    Pair(R.string.system_as_root, R.string.system_as_root_description)
                )
            )
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}