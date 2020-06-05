package dev.gabrielecappellaro.deviceinfo.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import dev.gabrielecappellaro.deviceinfo.R
import dev.gabrielecappellaro.deviceinfo.helper.TrebleHelper

class TrebleFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_treble, container, false)
        //Binding itemes
        val textViewProjectTrebleStatus: MaterialTextView = view.findViewById(R.id.textview_project_treble_status)
        val textViewTrebleVersion: MaterialTextView = view.findViewById(R.id.textview_treble_type)
        val textViewVNDKVersion: MaterialTextView = view.findViewById(R.id.textview_vndk_version)
        val buttonMoreInfoProjectTreble: MaterialButton = view.findViewById(R.id.button_more_info_project_treble)
        val textViewABPartitioningStatus: MaterialTextView = view.findViewById(R.id.textview_a_b_partitioning_status)
        val textViewSeamlessUpdate: MaterialTextView = view.findViewById(R.id.textview_seamless_updates)
        val buttonMoreInfoABPartitioning: MaterialButton = view.findViewById(R.id.button_more_info_a_b_partitioning)
        val textViewSystemAsRootStatus: MaterialTextView = view.findViewById(R.id.textview_system_as_root_status)
        val textViewSystemAsRootMethod: MaterialTextView = view.findViewById(R.id.textview_system_as_root_method)
        val buttonMoreInfoSystemAsRoot: MaterialButton = view.findViewById(R.id.button_more_info_system_as_root)

        //Setting helper values on textviews
        Thread {
            textViewProjectTrebleStatus.text = getString(TrebleHelper.trebleStatus())
            textViewTrebleVersion.text = getString(TrebleHelper.trebleVersion())
            textViewVNDKVersion.text = TrebleHelper.vndkVersion(requireContext())
            textViewABPartitioningStatus.text = getString(TrebleHelper.partitionStatus())
            textViewSeamlessUpdate.text = getString(TrebleHelper.seamlessUpdate())
            textViewSystemAsRootStatus.text = getString(TrebleHelper.systemMount())
            textViewSystemAsRootMethod.text = getString(TrebleHelper.systemMountMethod())
        }.run()

        buttonMoreInfoProjectTreble.setOnClickListener {
            MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                title(R.string.project_treble)
                message(R.string.project_treble_description)
            }
        }
        buttonMoreInfoABPartitioning.setOnClickListener {
            MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                title(R.string.a_b_partitioning)
            }
        }
        buttonMoreInfoSystemAsRoot.setOnClickListener {
            MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                title(R.string.system_as_root)
            }
        }

        return view
    }
}