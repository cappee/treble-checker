package dev.cappee.treble.settings

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.list.SingleChoiceListener
import com.afollestad.materialdialogs.list.listItemsSingleChoice
import dev.cappee.treble.R
import dev.cappee.treble.device.DeviceHelper
import kotlinx.coroutines.runBlocking

class SettingsFragment : PreferenceFragmentCompat() {

    private lateinit var preferenceBattery: Preference
    private lateinit var preferenceIdentifier: Preference
    private lateinit var preferenceProcessor: Preference
    private lateinit var preferenceGithub: Preference
    private lateinit var preferenceDeveloper: Preference
    private lateinit var preferenceVersion: Preference

    private val viewModel: SettingsViewModel by viewModels {
        SettingsViewModelFactory(SettingsRepository(context!!))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listView.overScrollMode = RecyclerView.OVER_SCROLL_NEVER
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings, rootKey)

        //Init all preferences
        preferenceBattery = findPreference("battery")!!
        preferenceIdentifier = findPreference("identifier")!!
        preferenceProcessor = findPreference("processor")!!
        preferenceGithub = findPreference("github")!!
        preferenceDeveloper = findPreference("developer")!!
        preferenceVersion = findPreference("version")!!

        viewModel.liveDataBatteryMode.observeForever { value ->
            preferenceBattery.apply {
                summary = String.format(getString(R.string.battery_summary),
                    if (value) { getString(R.string.experimental).toLowerCase() } else { getString(R.string.classic).toLowerCase() })
                onPreferenceClickListener = Preference.OnPreferenceClickListener {
                    MaterialDialog(context!!, BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                        title(R.string.battery)
                        listItemsSingleChoice(
                            res = R.array.battery_entries,
                            initialSelection = if (value) { 1 } else { 0 },
                            selection = object : SingleChoiceListener {
                                override fun invoke(dialog: MaterialDialog, index: Int, text: CharSequence) {
                                    dialog.dismiss()
                                    viewModel.setBatteryModeExperimental(index)
                                }
                            }
                        )
                    }
                    return@OnPreferenceClickListener true
                }
            }
        }

        viewModel.liveDataIdentifierOrder.observeForever { value ->
            preferenceIdentifier.apply {
                summary = String.format(getString(R.string.identifier_summary), DeviceHelper.possibleIdentifierOrder[value])
                onPreferenceClickListener = Preference.OnPreferenceClickListener {
                    MaterialDialog(context!!, BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                        title(R.string.identifier)
                        listItemsSingleChoice(
                            items = viewModel.identifierEntries,
                            initialSelection = value,
                            selection = object : SingleChoiceListener {
                                override fun invoke(dialog: MaterialDialog, index: Int, text: CharSequence) {
                                    dialog.dismiss()
                                    viewModel.setIdentifierOrder(index)
                                }
                            }
                        )
                    }
                    return@OnPreferenceClickListener true
                }
            }
        }

        viewModel.liveDataProcessorShownAs.observeForever { value ->
            preferenceProcessor.apply {
                summary = String.format(getString(R.string.processor_summary), runBlocking { DeviceHelper.cpu(value = value) })
                onPreferenceClickListener = Preference.OnPreferenceClickListener {
                    MaterialDialog(context!!, BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                        title(R.string.processor)
                        val items = viewModel.cpuEntries.values.toMutableList()
                        items.removeIf { it.isEmpty() || it == "0" }
                        listItemsSingleChoice(
                            items = items,
                            initialSelection = DeviceHelper.getCpuIndexByString(value),
                            selection = object : SingleChoiceListener {
                                override fun invoke(dialog: MaterialDialog, index: Int, text: CharSequence) {
                                    dialog.dismiss()
                                    viewModel.setProcessorShownAs(viewModel.cpuEntries.keys.elementAt(viewModel.cpuEntries.values.indexOf(text)))
                                }
                            }
                        )
                    }
                    return@OnPreferenceClickListener true
                }
            }
        }

        preferenceVersion.summary = viewModel.appVersion
    }
}