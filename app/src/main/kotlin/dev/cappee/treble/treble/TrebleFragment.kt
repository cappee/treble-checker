package dev.cappee.treble.treble

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import dev.cappee.treble.R
import dev.cappee.treble.main.recycler.RecyclerViewAdapter
import dev.cappee.treble.databinding.FragmentMainBinding
import dev.cappee.treble.main.recycler.ItemDecoration

class TrebleFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    companion object {
        private const val DATA = "DATA"

        fun newInstance(treble: Treble) = TrebleFragment().apply {
            val bundle = Bundle()
            bundle.putParcelable(DATA, treble)
            arguments = bundle
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //Get main object from bundle
        val treble: Treble = arguments?.getParcelable(DATA)!!
        //Init RecyclerView
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            addItemDecoration(ItemDecoration(resources.getDimensionPixelSize(R.dimen.recycler_items_margin)))
            adapter = RecyclerViewAdapter(context,
                arrayOf(R.string.project_treble, R.string.a_b_partitioning, R.string.system_as_root),
                arrayOf(
                    arrayOf(R.string.status, R.string.treble_arch, R.string.vndk_version),
                    arrayOf(R.string.status, R.string.seamless_updates),
                    arrayOf(R.string.status, R.string.method)),
                arrayOf(
                    arrayOf(treble.trebleStatus, treble.trebleArch, treble.vndkVersion),
                    arrayOf(treble.abStatus, treble.seamlessUpdate),
                    arrayOf(treble.sarStatus, treble.sarMethod)),
                arrayOf(
                    Pair(R.string.project_treble, R.string.project_treble_description),
                    Pair(R.string.a_b_partitioning, R.string.a_b_partitioning_description),
                    Pair(R.string.system_as_root, R.string.system_as_root_description)))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}