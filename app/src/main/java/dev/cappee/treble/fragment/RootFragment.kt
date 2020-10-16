package dev.cappee.treble.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.cappee.treble.R
import dev.cappee.treble.adapter.RecyclerViewAdapter
import dev.cappee.treble.helper.RootHelper
import kotlinx.android.synthetic.main.fragment_root.*
import kotlin.concurrent.thread

class RootFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_root, container, false)

        val titles: Array<Int> = arrayOf(R.string.superuser, R.string.busybox)
        val buttonSuperuser: Array<Int> = arrayOf(R.string.superuser, R.string.superuser_description)
        val buttonBusyBox: Array<Int> = arrayOf(R.string.busybox, R.string.busybox_description)
        val subtitlesSuperuser: Array<Int> = arrayOf(R.string.root_permissions, R.string.root_path, R.string.superuser_app)
        val subtitleBusyBox: Array<String> = arrayOf("Installed", "Version")
        thread {
            val dataSuperuser: Array<String> = arrayOf(getString(RootHelper.rootPermissions()),
                RootHelper.rootPath(context!!),
                "Magisk")
            val dataBusyBox: Array<String> = arrayOf("No", "Not installed")
            runOnUiThread {
                recyclerViewRoot.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                recyclerViewRoot.adapter = RecyclerViewAdapter(context, titles, arrayOf(subtitlesSuperuser), arrayOf(dataSuperuser), arrayOf(buttonSuperuser))
            }
        }


        return view
    }

    private fun Fragment?.runOnUiThread(action: () -> Unit) {
        this ?: return
        if (!isAdded) return
        activity?.runOnUiThread(action)
    }

}