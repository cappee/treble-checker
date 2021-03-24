package dev.cappee.treble.settings

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.input.InputCallback
import com.afollestad.materialdialogs.input.input
import com.afollestad.materialdialogs.list.SingleChoiceListener
import com.afollestad.materialdialogs.list.listItemsSingleChoice
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dev.cappee.treble.BuildConfig
import dev.cappee.treble.R
import dev.cappee.treble.device.DeviceHelper
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await

class SettingsFragment : PreferenceFragmentCompat() {

    private val database by lazy { Firebase.firestore }

    private lateinit var menuContactUs: MaterialDialog

    private lateinit var preferenceBattery: Preference
    private lateinit var preferenceIdentifier: Preference
    private lateinit var preferenceProcessor: Preference
    private lateinit var preferenceContactUs: Preference
    private lateinit var preferenceGithub: Preference
    private lateinit var preferenceDeveloper: Preference
    private lateinit var preferenceVersion: Preference
    private lateinit var preferenceLicenses: Preference

    private val viewModel: SettingsViewModel by viewModels {
        SettingsViewModelFactory(SettingsRepository(context?.dataStore!!))
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
        preferenceContactUs = findPreference("contact_us")!!
        preferenceGithub = findPreference("github")!!
        preferenceDeveloper = findPreference("developer")!!
        preferenceVersion = findPreference("version")!!
        preferenceLicenses = findPreference("licenses")!!

        lifecycleScope.launch {
            menuContactUs = MaterialDialog(context!!).apply {
                title(res = R.string.contact_us)
                message(res = R.string.contact_us_message)
                input(
                    hintRes = R.string.contact_us_hint,
                    allowEmpty = false,
                    maxLength = 150,
                    waitForPositiveButton = true,
                    callback = object : InputCallback {
                        override fun invoke(dialog: MaterialDialog, text: CharSequence) {
                            lifecycleScope.launch {
                                database.collection("reports")
                                    .add(
                                        hashMapOf(
                                            "message" to text.toString(),
                                            "timestamp" to FieldValue.serverTimestamp(),
                                            "android_version" to Build.VERSION.SDK_INT,
                                            "app_version" to "${BuildConfig.VERSION_CODE}/${BuildConfig.VERSION_NAME}"
                                        )
                                    )
                                    .addOnSuccessListener {
                                        if (this@SettingsFragment.view != null) {
                                            Snackbar
                                                .make(this@SettingsFragment.view!!, R.string.feedback_sent, Snackbar.LENGTH_SHORT)
                                                .show()
                                        }
                                    }
                                    .await()
                            }
                        }
                    })
            }
        }

        viewModel.liveDataBatteryMode.observeForever { value ->
            preferenceBattery.apply {
                summary = String.format(
                    activity?.getString(R.string.battery_summary) ?: "",
                    if (value) {
                        activity?.getString(R.string.experimental)?.toLowerCase()
                    } else {
                        activity?.getString(R.string.classic)?.toLowerCase()
                    }
                )
                onPreferenceClickListener = Preference.OnPreferenceClickListener {
                    MaterialDialog(context!!, BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                        title(R.string.battery)
                        listItemsSingleChoice(
                            res = R.array.battery_entries,
                            initialSelection = if (value) {
                                1
                            } else {
                                0
                            },
                            selection = object : SingleChoiceListener {
                                override fun invoke(
                                    dialog: MaterialDialog,
                                    index: Int,
                                    text: CharSequence
                                ) {
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
                summary = String.format(
                    activity?.getString(R.string.identifier_summary) ?: "",
                    DeviceHelper.possibleIdentifierOrder[value]
                )
                onPreferenceClickListener = Preference.OnPreferenceClickListener {
                    MaterialDialog(context!!, BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                        title(R.string.identifier)
                        listItemsSingleChoice(
                            items = viewModel.identifierEntries,
                            initialSelection = value,
                            selection = object : SingleChoiceListener {
                                override fun invoke(
                                    dialog: MaterialDialog,
                                    index: Int,
                                    text: CharSequence
                                ) {
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
                summary = String.format(activity?.getString(R.string.processor_summary) ?: "", runBlocking {
                    DeviceHelper.cpu(
                        value = value
                    )
                })
                onPreferenceClickListener = Preference.OnPreferenceClickListener {
                    MaterialDialog(context!!, BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                        title(R.string.processor)
                        val items = viewModel.cpuEntries.values.toMutableList()
                        if (Build.VERSION.SDK_INT >= 24)
                            items.removeIf { it.isEmpty() }
                        listItemsSingleChoice(
                            items = items,
                            initialSelection = DeviceHelper.getCpuIndexByString(value),
                            selection = object : SingleChoiceListener {
                                override fun invoke(
                                    dialog: MaterialDialog,
                                    index: Int,
                                    text: CharSequence
                                ) {
                                    dialog.dismiss()
                                    viewModel.setProcessorShownAs(
                                        viewModel.cpuEntries.keys.elementAt(
                                            viewModel.cpuEntries.values.indexOf(
                                                text
                                            )
                                        )
                                    )
                                }
                            }
                        )
                    }
                    return@OnPreferenceClickListener true
                }
            }
        }

        preferenceContactUs.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            if (this::menuContactUs.isInitialized)
                menuContactUs.show()
            return@OnPreferenceClickListener true
        }

        preferenceGithub.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://github.com/cappee/treble-checker")
                )
            )
            return@OnPreferenceClickListener true
        }

        preferenceVersion.summary = viewModel.appVersion

        preferenceLicenses.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            OssLicensesMenuActivity.setActivityTitle(activity?.getString(R.string.oss_license_title) ?: "")
            startActivity(Intent(context, OssLicensesMenuActivity::class.java))
            return@OnPreferenceClickListener true
        }
    }
}