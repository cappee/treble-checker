package dev.cappee.treble.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import dev.cappee.treble.R
import dev.cappee.treble.adapter.RecyclerViewAdapter
import dev.cappee.treble.helper.TrebleHelper
import kotlinx.android.synthetic.main.fragment_treble.*
import kotlin.concurrent.thread

class TrebleFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_treble, container, false)

        val titles: Array<Int> = arrayOf(R.string.project_treble, R.string.a_b_partitioning, R.string.system_as_root)
        val buttons: Array<Array<Int>> = arrayOf(arrayOf(R.string.project_treble, R.string.project_treble_description),
            arrayOf(R.string.a_b_partitioning, R.string.a_b_partitioning_description),
            arrayOf(R.string.system_as_root, R.string.system_as_root_description))
        val subtitleProjectTreble: Array<Int> = arrayOf(R.string.status, R.string.treble_arch, R.string.vndk_version)
        val subtitleABPartitioning: Array<Int> = arrayOf(R.string.status, R.string.seamless_updates)
        val subtitleSystemAsRoot: Array<Int> = arrayOf(R.string.status, R.string.method)
        thread {
            val dataProjectTreble: Array<String> = arrayOf(getString(TrebleHelper.trebleStatus()),
                getString(TrebleHelper.trebleVersion()),
                TrebleHelper.vndkVersion(requireContext()))
            val dataABPartitioning: Array<String> = arrayOf(getString(TrebleHelper.partitionStatus()),
                getString(TrebleHelper.seamlessUpdate()))
            val dataSystemAsRoot: Array<String> = arrayOf(getString(TrebleHelper.systemMount()),
                getString(TrebleHelper.systemMountMethod()))
            runOnUiThread {
                recyclerViewTreble.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                recyclerViewTreble.adapter = RecyclerViewAdapter(context,
                    titles,
                    arrayOf(subtitleProjectTreble, subtitleABPartitioning, subtitleSystemAsRoot),
                    arrayOf(dataProjectTreble, dataABPartitioning, dataSystemAsRoot),
                    buttons
                )
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