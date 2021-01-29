package dev.cappee.treble.root

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import dev.cappee.treble.R
import dev.cappee.treble.adapter.RecyclerViewAdapter
import dev.cappee.treble.databinding.FragmentRootBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RootFragment : Fragment() {

    private var _binding: FragmentRootBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentRootBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val titles: Array<Int> = arrayOf(R.string.superuser, R.string.busybox)
        val buttonSuperuser: Array<Int> = arrayOf(R.string.superuser, R.string.superuser_description)
        val buttonBusyBox: Array<Int> = arrayOf(R.string.busybox, R.string.busybox_description)
        val subtitlesSuperuser: Array<Int> = arrayOf(R.string.root_permissions, R.string.root_path)
        val subtitleBusyBox: Array<Int> = arrayOf(R.string.status, R.string.build_date)
        lifecycleScope.launch(Dispatchers.Main) {
            val dataSuperuser: Array<String> = arrayOf(getString(RootHelper.rootPermissions()),
                RootHelper.rootPath(context!!))
            val dataBusyBox: Array<String> = arrayOf(
                RootHelper.busyBoxInstalled(context!!),
                RootHelper.busyBoxBuildDate(context!!))
            binding.recyclerViewRoot.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                adapter = RecyclerViewAdapter(context,
                    titles,
                    arrayOf(subtitlesSuperuser, subtitleBusyBox),
                    arrayOf(dataSuperuser, dataBusyBox),
                    arrayOf(buttonSuperuser, buttonBusyBox))
            }
            binding.progressBarRoot.visibility = ViewGroup.INVISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}