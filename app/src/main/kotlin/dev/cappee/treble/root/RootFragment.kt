package dev.cappee.treble.root

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

class RootFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    companion object {
        private const val DATA = "DATA"

        fun newInstance(root: Root) = RootFragment().apply {
            val bundle = Bundle()
            bundle.putParcelable(DATA, root)
            arguments = bundle
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //Get main object from bundle
        val root: Root = arguments?.getParcelable(DATA)!!
        //Init RecyclerView
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            addItemDecoration(ItemDecoration(resources.getDimensionPixelSize(R.dimen.recycler_items_margin)))
            adapter = RecyclerViewAdapter(context,
                arrayOf(R.string.superuser, R.string.busybox),
                arrayOf(
                    arrayOf(R.string.root_permissions, R.string.root_path),
                    arrayOf(R.string.status, R.string.build_date)),
                arrayOf(
                    arrayOf(root.rootPermissions, root.rootPath),
                    arrayOf(root.busyBoxStatus, root.busyBoxBuildDate)),
                arrayOf(
                    Pair(R.string.superuser, R.string.superuser_description),
                    Pair(R.string.busybox, R.string.busybox_description)))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}