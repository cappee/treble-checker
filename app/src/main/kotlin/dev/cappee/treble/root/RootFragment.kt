package dev.cappee.treble.root

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import dev.cappee.treble.R
import dev.cappee.treble.main.recycler.RecyclerViewAdapter
import dev.cappee.treble.databinding.FragmentMainBinding
import dev.cappee.treble.main.MainViewModel
import dev.cappee.treble.main.recycler.ItemDecoration

class RootFragment : Fragment() {

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
        viewModel.liveDataRoot.observe(viewLifecycleOwner, {
            binding.progressBar.visibility = ProgressBar.INVISIBLE
            binding.recyclerView.adapter = RecyclerViewAdapter(context,
                arrayOf(R.string.superuser, R.string.busybox),
                arrayOf(
                    arrayOf(R.string.root_permissions, R.string.root_path),
                    arrayOf(R.string.status, R.string.build_date)),
                arrayOf(
                    arrayOf(it.rootPermissions, it.rootPath),
                    arrayOf(it.busyBoxStatus, it.busyBoxBuildDate)),
                arrayOf(
                    Pair(R.string.superuser, R.string.superuser_description),
                    Pair(R.string.busybox, R.string.busybox_description)))
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}