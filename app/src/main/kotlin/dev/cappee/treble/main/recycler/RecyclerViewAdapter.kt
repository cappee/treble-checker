package dev.cappee.treble.main.recycler

import android.content.Context
import android.view.*
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import dev.cappee.treble.databinding.LayoutAdsBinding
import dev.cappee.treble.databinding.LayoutRecyclerBinding
import dev.cappee.treble.model.Data

class RecyclerViewAdapter(
    private val context: Context?,
    private val dataSets: MutableList<Any>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    inner class ViewHolder(val binding: LayoutRecyclerBinding) : RecyclerView.ViewHolder(binding.root)

    inner class AdsViewHolder(val binding: LayoutAdsBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == 1) {
            AdsViewHolder(LayoutAdsBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        } else {
            ViewHolder(LayoutRecyclerBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is AdsViewHolder) { with(holder) {
            //binding
        } } else if (holder is ViewHolder) { with(holder.binding) {
            val data = dataSets[position] as Data
            textViewTitle.text = context?.getString(data.title)

            when (data.values.size) {
                4 -> {
                    textViewDescriptionFirst.text = context?.getString(data.descriptions[0])
                    textViewFirst.text = data.values[0]
                    textViewDescriptionSecond.text = context?.getString(data.descriptions[1])
                    textViewSecond.text = data.values[1]
                    textViewDescriptionThird.text = context?.getString(data.descriptions[2])
                    textViewThird.text = data.values[2]
                    textViewDescriptionFourth.text = context?.getString(data.descriptions[3])
                    textViewFourth.text = data.values[3]
                }
                3 -> {
                    textViewDescriptionFourth.visibility = MaterialTextView.GONE
                    textViewFourth.visibility = MaterialTextView.GONE
                    textViewDescriptionFirst.text = context?.getString(data.descriptions[0])
                    textViewFirst.text = data.values[0]
                    textViewDescriptionSecond.text = context?.getString(data.descriptions[1])
                    textViewSecond.text = data.values[1]
                    textViewDescriptionThird.text = context?.getString(data.descriptions[2])
                    textViewThird.text = data.values[2]
                }
                2 -> {
                    textViewDescriptionFourth.visibility = MaterialTextView.GONE
                    textViewFourth.visibility = MaterialTextView.GONE
                    textViewDescriptionThird.visibility = MaterialTextView.GONE
                    textViewThird.visibility = MaterialTextView.GONE
                    textViewDescriptionFirst.text = context?.getString(data.descriptions[0])
                    textViewFirst.text = data.values[0]
                    textViewDescriptionSecond.text = context?.getString(data.descriptions[1])
                    textViewSecond.text = data.values[1]
                }
                1 -> {
                    textViewDescriptionFourth.visibility = MaterialTextView.GONE
                    textViewFourth.visibility = MaterialTextView.GONE
                    textViewDescriptionThird.visibility = MaterialTextView.GONE
                    textViewThird.visibility = MaterialTextView.GONE
                    textViewDescriptionSecond.visibility = MaterialTextView.GONE
                    textViewSecond.visibility = MaterialTextView.GONE
                    textViewDescriptionFirst.text = context?.getString(data.descriptions[0])
                    textViewFirst.text = data.values[0]
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
        return dataSets.size
    }

    override fun getItemViewType(position: Int): Int {
        /*return if (position == 1) {
            1
        } else {
            0
        }*/
        return 0
    }

}