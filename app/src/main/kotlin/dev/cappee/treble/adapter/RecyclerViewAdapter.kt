package dev.cappee.treble.adapter

import android.content.Context
import android.graphics.Point
import android.hardware.display.DisplayManager
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.*
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textview.MaterialTextView
import dev.cappee.treble.R
import kotlin.math.roundToInt

class RecyclerViewAdapter(private val context: Context?, private val titles: Array<Int>, private val allSubtitles: Array<Array<Int>>, private val allData: Array<Array<String>>, private val allButtons: Array<Array<Int>>?) : RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val card: MaterialCardView = itemView.findViewById(R.id.cardViewPlaceholder)
        val title: MaterialTextView = itemView.findViewById(R.id.textViewPlaceholderTitle)
        val subtitleFirst: MaterialTextView = itemView.findViewById(R.id.textViewPlaceholderSubtitleFirst)
        val first: MaterialTextView = itemView.findViewById(R.id.textViewPlaceholderFirst)
        val subtitleSecond: MaterialTextView = itemView.findViewById(R.id.textViewPlaceholderSubtitleSecond)
        val second: MaterialTextView = itemView.findViewById(R.id.textViewPlaceholderSecond)
        val subtitleThird: MaterialTextView = itemView.findViewById(R.id.textViewPlaceholderSubtitleThird)
        val third: MaterialTextView = itemView.findViewById(R.id.textViewPlaceholderThird)
        val subtitleFourth: MaterialTextView = itemView.findViewById(R.id.textViewPlaceholderSubtitleFourth)
        val fourth: MaterialTextView = itemView.findViewById(R.id.textViewPlaceholderFourth)
        val button: MaterialButton = itemView.findViewById(R.id.buttonMoreInfo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val cardView = LayoutInflater.from(parent.context).inflate(R.layout.layout_cardview, parent, false)
        return ViewHolder(cardView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position == itemCount - 1) {
            val params: ViewGroup.MarginLayoutParams = holder.card.layoutParams as ViewGroup.MarginLayoutParams
            params.bottomMargin = params.topMargin
        }
        holder.title.text = context?.getString(titles[position])
        val subtitles : Array<Int> = allSubtitles[position]
        val data : Array<String> = allData[position]
        var buttons: Array<Int> = emptyArray()
        if (!allButtons.isNullOrEmpty())
            buttons = allButtons[position]
        when (subtitles.size) {
            4 -> {
                holder.subtitleFirst.text = context?.getString(subtitles[0])
                holder.first.text = data[0]
                holder.subtitleSecond.text = context?.getString(subtitles[1])
                holder.second.text = data[1]
                holder.subtitleThird.text = context?.getString(subtitles[2])
                holder.third.text = data[2]
                holder.subtitleFourth.text = context?.getString(subtitles[3])
                holder.fourth.text = data[3]
            }
            3 -> {
                holder.subtitleFourth.visibility = MaterialTextView.GONE
                holder.fourth.visibility = MaterialTextView.GONE
                holder.subtitleFirst.text = context?.getString(subtitles[0])
                holder.first.text = data[0]
                holder.subtitleSecond.text = context?.getString(subtitles[1])
                holder.second.text = data[1]
                holder.subtitleThird.text = context?.getString(subtitles[2])
                holder.third.text = data[2]
            }
            2 -> {
                holder.subtitleFourth.visibility = MaterialTextView.GONE
                holder.fourth.visibility = MaterialTextView.GONE
                holder.subtitleThird.visibility = MaterialTextView.GONE
                holder.third.visibility = MaterialTextView.GONE
                holder.subtitleFirst.text = context?.getString(subtitles[0])
                holder.first.text = data[0]
                holder.subtitleSecond.text = context?.getString(subtitles[1])
                holder.second.text = data[1]
            }
            1 -> {
                holder.subtitleFourth.visibility = MaterialTextView.GONE
                holder.fourth.visibility = MaterialTextView.GONE
                holder.subtitleThird.visibility = MaterialTextView.GONE
                holder.third.visibility = MaterialTextView.GONE
                holder.subtitleSecond.visibility = MaterialTextView.GONE
                holder.second.visibility = MaterialTextView.GONE
                holder.subtitleFirst.text = context?.getString(subtitles[0])
                holder.first.text = data[0]
            }
        }
        if (buttons.isNullOrEmpty()) {
            holder.button.visibility = MaterialButton.GONE
        } else {
            holder.button.setOnClickListener {
                if (context != null) {
                    MaterialDialog(context, BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                        title(buttons[0])
                        message(buttons[1])
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return allSubtitles.size
    }

}