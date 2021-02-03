package dev.cappee.treble.main.recycler

import android.content.Context
import android.view.*
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import dev.cappee.treble.R
import dev.cappee.treble.databinding.LayoutRecyclerBinding

class RecyclerViewAdapter(private val context: Context?, private val titles: Array<Int>, private val allSubtitles: Array<Array<Int>>, private val allData: Array<Array<String>>, private val allButtons: Array<Pair<Int, Int>>?) : RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: LayoutRecyclerBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutRecyclerBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            binding.textViewTitle.text = context?.getString(titles[position])
            val subtitles : Array<Int> = allSubtitles[position]
            val data : Array<String> = allData[position]

            when (subtitles.size) {
                4 -> {
                    binding.textViewDescriptionFirst.text = context?.getString(subtitles[0])
                    binding.textViewFirst.text = data[0]
                    binding.textViewDescriptionSecond.text = context?.getString(subtitles[1])
                    binding.textViewSecond.text = data[1]
                    binding.textViewDescriptionThird.text = context?.getString(subtitles[2])
                    binding.textViewThird.text = data[2]
                    binding.textViewDescriptionFourth.text = context?.getString(subtitles[3])
                    binding.textViewFourth.text = data[3]
                }
                3 -> {
                    binding.textViewDescriptionFourth.visibility = MaterialTextView.GONE
                    binding.textViewFourth.visibility = MaterialTextView.GONE
                    binding.textViewDescriptionFirst.text = context?.getString(subtitles[0])
                    binding.textViewFirst.text = data[0]
                    binding.textViewDescriptionSecond.text = context?.getString(subtitles[1])
                    binding.textViewSecond.text = data[1]
                    binding.textViewDescriptionThird.text = context?.getString(subtitles[2])
                    binding.textViewThird.text = data[2]
                }
                2 -> {
                    binding.textViewDescriptionFourth.visibility = MaterialTextView.GONE
                    binding.textViewFourth.visibility = MaterialTextView.GONE
                    binding.textViewDescriptionThird.visibility = MaterialTextView.GONE
                    binding.textViewThird.visibility = MaterialTextView.GONE
                    binding.textViewDescriptionFirst.text = context?.getString(subtitles[0])
                    binding.textViewFirst.text = data[0]
                    binding.textViewDescriptionSecond.text = context?.getString(subtitles[1])
                    binding.textViewSecond.text = data[1]
                }
                1 -> {
                    binding.textViewDescriptionFourth.visibility = MaterialTextView.GONE
                    binding.textViewFourth.visibility = MaterialTextView.GONE
                    binding.textViewDescriptionThird.visibility = MaterialTextView.GONE
                    binding.textViewThird.visibility = MaterialTextView.GONE
                    binding.textViewDescriptionSecond.visibility = MaterialTextView.GONE
                    binding.textViewSecond.visibility = MaterialTextView.GONE
                    binding.textViewDescriptionFirst.text = context?.getString(subtitles[0])
                    binding.textViewFirst.text = data[0]
                }
            }
            if (!allButtons.isNullOrEmpty()) {
                with(allButtons[position]) {
                    binding.buttonMoreInfo.setOnClickListener {
                        if (context != null) {
                            MaterialDialog(context, BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                                title(first)
                                message(second)
                            }
                        }
                    }
                }
            } else {
                binding.buttonMoreInfo.visibility = MaterialButton.GONE
            }
        }
    }

    override fun getItemCount(): Int {
        return allSubtitles.size
    }

}