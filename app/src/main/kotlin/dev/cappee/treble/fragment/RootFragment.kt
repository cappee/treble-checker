package dev.cappee.treble.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.cappee.treble.R
import dev.cappee.treble.adapter.RecyclerViewAdapter
import dev.cappee.treble.helper.RootHelper
import kotlinx.android.synthetic.main.fragment_root.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.concurrent.thread

class RootFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_root, container, false)

        val titles: Array<Int> = arrayOf(R.string.superuser, R.string.busybox)
        val buttonSuperuser: Array<Int> = arrayOf(R.string.superuser, R.string.superuser_description)
        val buttonBusyBox: Array<Int> = arrayOf(R.string.busybox, R.string.busybox_description)
        val subtitlesSuperuser: Array<Int> = arrayOf(R.string.root_permissions, R.string.root_path)
        val subtitleBusyBox: Array<Int> = arrayOf(R.string.status, R.string.build_date)
        lifecycleScope.launch(Dispatchers.Main) {
            val dataSuperuser: Array<String> = arrayOf(getString(RootHelper.rootPermissions()),
                RootHelper.rootPath(context!!))
            val dataBusyBox: Array<String> = arrayOf(RootHelper.busyBoxInstalled(context!!),
                RootHelper.busyBoxBuildDate(context!!))
            recyclerViewRoot.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            recyclerViewRoot.adapter = RecyclerViewAdapter(context,
                titles,
                arrayOf(subtitlesSuperuser, subtitleBusyBox),
                arrayOf(dataSuperuser, dataBusyBox),
                arrayOf(buttonSuperuser, buttonBusyBox))
            progressBarRoot.visibility = ViewGroup.INVISIBLE
        }

        return view
    }

}