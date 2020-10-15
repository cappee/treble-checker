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
import kotlinx.android.synthetic.main.fragment_root.*

class RootFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_root, container, false)

        val titles: Array<Int> = arrayOf(R.string.superuser, R.string.busybox)
        val subtitlesSuperuser: Array<String> = arrayOf("Rooted", "Root path", "Superuser app")
        val subtitleBusyBox: Array<String> = arrayOf("Installed", "Version")
        val dataSuperuser: Array<String> = arrayOf("Yes", "/bin/su", "Magisk")
        val dataBusyBox: Array<String> = arrayOf("No", "Not installed")
        val buttonSuperuser: Array<Int> = arrayOf(R.string.superuser, R.string.superuser_description)
        val buttonBusyBox: Array<Int> = arrayOf(R.string.busybox, R.string.busybox_description)

        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerViewRoot)
        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        //recyclerView.adapter = RecyclerViewAdapter(context, titles, arrayOf(subtitlesSuperuser, subtitleBusyBox), arrayOf(dataSuperuser, dataBusyBox), arrayOf(buttonSuperuser, buttonBusyBox))
        return view
    }

    private fun Fragment?.runOnUiThread(action: () -> Unit) {
        this ?: return
        if (!isAdded) return
        activity?.runOnUiThread(action)
    }

}