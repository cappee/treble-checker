package dev.cappee.treble.root

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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class RootFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val titles: Array<Int> = arrayOf(R.string.superuser, R.string.busybox)
        val buttonSuperuser: Array<Int> = arrayOf(R.string.superuser, R.string.superuser_description)
        val buttonBusyBox: Array<Int> = arrayOf(R.string.busybox, R.string.busybox_description)
        val subtitlesSuperuser: Array<Int> = arrayOf(R.string.root_permissions, R.string.root_path)
        val subtitleBusyBox: Array<Int> = arrayOf(R.string.status, R.string.build_date)
        lifecycleScope.launch(Dispatchers.Main) {
            val dataSuperuser = async(Dispatchers.Default) {
                arrayOf(getString(RootHelper.rootPermissions()),
                    RootHelper.rootPath(context!!))
            }
            val dataBusyBox = async(Dispatchers.Default) {
                arrayOf(RootHelper.busyBoxInstalled(context!!),
                    RootHelper.busyBoxBuildDate(context!!))
            }
            binding.recyclerView.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                addItemDecoration(ItemDecoration(resources.getDimensionPixelSize(R.dimen.recycler_items_margin)))
                adapter = RecyclerViewAdapter(context,
                    titles,
                    arrayOf(subtitlesSuperuser, subtitleBusyBox),
                    arrayOf(dataSuperuser.await(), dataBusyBox.await()),
                    arrayOf(buttonSuperuser, buttonBusyBox))
            }
            binding.progressBar.visibility = ViewGroup.INVISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}