package dev.cappee.treble.treble

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.cappee.treble.R
import dev.cappee.treble.main.recycler.RecyclerViewAdapter
import dev.cappee.treble.databinding.FragmentMainBinding
import dev.cappee.treble.main.recycler.ItemDecoration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class TrebleFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val titles: Array<Int> = arrayOf(R.string.project_treble, R.string.a_b_partitioning, R.string.system_as_root)
        val buttons: Array<Array<Int>> = arrayOf(arrayOf(R.string.project_treble, R.string.project_treble_description),
            arrayOf(R.string.a_b_partitioning, R.string.a_b_partitioning_description),
            arrayOf(R.string.system_as_root, R.string.system_as_root_description))
        val subtitleProjectTreble: Array<Int> = arrayOf(R.string.status, R.string.treble_arch, R.string.vndk_version)
        val subtitleABPartitioning: Array<Int> = arrayOf(R.string.status, R.string.seamless_updates)
        val subtitleSystemAsRoot: Array<Int> = arrayOf(R.string.status, R.string.method)
        lifecycleScope.launch(Dispatchers.Main) {
            val dataProjectTreble = async(Dispatchers.Default) {
                arrayOf(getString(TrebleHelper.trebleStatus()),
                    getString(TrebleHelper.trebleVersion()),
                    TrebleHelper.vndkVersion(requireContext()))
            }
            val dataABPartitioning = async(Dispatchers.Default) {
                arrayOf(getString(TrebleHelper.partitionStatus()),
                    getString(TrebleHelper.seamlessUpdate()))
            }
            val dataSystemAsRoot = async(Dispatchers.Default) {
                arrayOf(getString(TrebleHelper.systemMount()),
                    getString(TrebleHelper.systemMountMethod()))
            }
            binding.recyclerView.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                addItemDecoration(ItemDecoration(resources.getDimensionPixelSize(R.dimen.recycler_items_margin)))
                adapter = RecyclerViewAdapter(context,
                    titles,
                    arrayOf(subtitleProjectTreble, subtitleABPartitioning, subtitleSystemAsRoot),
                    arrayOf(dataProjectTreble.await(), dataABPartitioning.await(), dataSystemAsRoot.await()),
                    buttons
                )
            }
            binding.progressBar.visibility = ViewGroup.INVISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}