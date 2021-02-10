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
import dev.cappee.treble.main.MainViewModel
import dev.cappee.treble.main.recycler.ItemDecoration
import dev.cappee.treble.main.recycler.RecyclerViewAdapter
import dev.cappee.treble.model.Data

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
                mutableListOf(
                    Data(
                        R.string.project_treble,
                        arrayOf(R.string.status, R.string.treble_arch, R.string.vndk_version),
                        arrayOf(it.trebleStatus, it.trebleArch, it.vndkVersion),
                        Pair(R.string.project_treble, R.string.project_treble_description)),
                    Data(
                        R.string.a_b_partitioning,
                        arrayOf(R.string.status, R.string.seamless_updates),
                        arrayOf(it.abStatus, it.seamlessUpdate),
                        Pair(R.string.a_b_partitioning, R.string.a_b_partitioning_description)),
                    Data(
                        R.string.system_as_root,
                        arrayOf(R.string.status, R.string.method),
                        arrayOf(it.sarStatus, it.sarMethod),
                        Pair(R.string.system_as_root, R.string.system_as_root_description))
                )
            )
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}