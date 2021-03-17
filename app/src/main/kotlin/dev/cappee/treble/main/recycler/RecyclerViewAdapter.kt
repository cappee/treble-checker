package dev.cappee.treble.main.recycler

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.google.android.gms.ads.nativead.NativeAdView
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import dev.cappee.treble.databinding.LayoutRecyclerBinding
import dev.cappee.treble.model.Data

class RecyclerViewAdapter(
    private val context: Context?,
    private val dataSet: MutableList<Any>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    inner class ViewHolder(val binding: LayoutRecyclerBinding) : RecyclerView.ViewHolder(binding.root)

    inner class AdsViewHolder(val binding: LayoutRecyclerBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == 1) {
            AdsViewHolder(LayoutRecyclerBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        } else {
            ViewHolder(LayoutRecyclerBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is AdsViewHolder) { with(holder.binding) {
            val adView = dataSet[position] as NativeAdView

            if (root.childCount > 0) {
                root.removeAllViews()
            }
            if (adView.parent != null) {
                (adView.parent as ViewGroup).removeView(adView)
            }
            root.addView(adView)
        } } else if (holder is ViewHolder) { with(holder.binding) {
            val data = dataSet[position] as Data
            textViewTitle.text = context?.getString(data.title)

            when (data.values.size) {
                4 -> {
                    textViewDescription1.text = context?.getString(data.descriptions[0])
                    textView1.text = data.values[0]
                    textViewDescription2.text = context?.getString(data.descriptions[1])
                    textView2.text = data.values[1]
                    textViewDescription3.text = context?.getString(data.descriptions[2])
                    textView3.text = data.values[2]
                    textViewDescription4.text = context?.getString(data.descriptions[3])
                    textView4.text = data.values[3]
                }
                3 -> {
                    textViewDescription4.visibility = MaterialTextView.GONE
                    textView4.visibility = MaterialTextView.GONE
                    textViewDescription1.text = context?.getString(data.descriptions[0])
                    textView1.text = data.values[0]
                    textViewDescription2.text = context?.getString(data.descriptions[1])
                    textView2.text = data.values[1]
                    textViewDescription3.text = context?.getString(data.descriptions[2])
                    textView3.text = data.values[2]
                }
                2 -> {
                    textViewDescription4.visibility = MaterialTextView.GONE
                    textView4.visibility = MaterialTextView.GONE
                    textViewDescription3.visibility = MaterialTextView.GONE
                    textView3.visibility = MaterialTextView.GONE
                    textViewDescription1.text = context?.getString(data.descriptions[0])
                    textView1.text = data.values[0]
                    textViewDescription2.text = context?.getString(data.descriptions[1])
                    textView2.text = data.values[1]
                }
                1 -> {
                    textViewDescription4.visibility = MaterialTextView.GONE
                    textView4.visibility = MaterialTextView.GONE
                    textViewDescription3.visibility = MaterialTextView.GONE
                    textView3.visibility = MaterialTextView.GONE
                    textViewDescription2.visibility = MaterialTextView.GONE
                    textView2.visibility = MaterialTextView.GONE
                    textViewDescription1.text = context?.getString(data.descriptions[0])
                    textView1.text = data.values[0]
                }
            }
            if (data.button != null) {
                buttonMoreInfo.setOnClickListener {
                    if (context != null) {
                        MaterialDialog(context, BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                            title(data.button.first)
                            message(data.button.second)
                        }
                    }
                }
            } else {
                buttonMoreInfo.visibility = MaterialButton.GONE
            } }
        }
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (dataSet[position] is NativeAdView) {
            1
        } else {
            0
        }
    }

}