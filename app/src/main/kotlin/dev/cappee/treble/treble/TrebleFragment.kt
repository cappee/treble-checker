package dev.cappee.treble.treble

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.nativead.NativeAdOptions
import dev.cappee.treble.R
import dev.cappee.treble.databinding.FragmentMainBinding
import dev.cappee.treble.databinding.LayoutNativeAdBinding
import dev.cappee.treble.main.MainViewModel
import dev.cappee.treble.main.recycler.ItemDecoration
import dev.cappee.treble.main.recycler.RecyclerViewAdapter
import dev.cappee.treble.model.Data
import kotlin.random.Random

class TrebleFragment : Fragment() {

    private lateinit var data: MutableList<Any>

    private val viewModel: MainViewModel by activityViewModels()

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //Init RecyclerView
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            addItemDecoration(ItemDecoration(resources.getDimensionPixelSize(R.dimen.recycler_items_margin)))
        }

        //Observe LiveData and update adapter
        viewModel.liveDataTreble.observe(viewLifecycleOwner, { treble ->
            data = mutableListOf(
                Data(
                    R.string.project_treble,
                    arrayOf(R.string.status, R.string.treble_arch, R.string.vndk_version),
                    arrayOf(treble.trebleStatus, treble.trebleArch, treble.vndkVersion),
                    Pair(R.string.project_treble, R.string.project_treble_description)),
                Data(
                    R.string.a_b_partitioning,
                    arrayOf(R.string.status, R.string.seamless_updates),
                    arrayOf(treble.abStatus, treble.seamlessUpdate),
                    Pair(R.string.a_b_partitioning, R.string.a_b_partitioning_description)),
                Data(
                    R.string.system_as_root,
                    arrayOf(R.string.status, R.string.method),
                    arrayOf(treble.sarStatus, treble.sarMethod),
                    Pair(R.string.system_as_root, R.string.system_as_root_description)))
            binding.recyclerView.adapter = RecyclerViewAdapter(context, data)
            binding.progressBar.visibility = ProgressBar.INVISIBLE
        })

        viewModel.liveDataAdTreble.observe(viewLifecycleOwner, {
            if (isDetached) {
                it.destroy()
                return@observe
            }
            val layout = LayoutNativeAdBinding.inflate(layoutInflater)
            val nativeAdView = layout.root
            with(layout) {
                adMediaView.setImageScaleType(ImageView.ScaleType.CENTER)
                adMediaView.setMediaContent(it.mediaContent)
                adHeadline.text = it.headline
                adBody.text = it.body
                if (it.icon != null)
                    adIcon.setImageDrawable(it.icon.drawable)
                adClick.text = it.callToAction
                if (it.store.isNullOrEmpty()) {
                    adStorePrice.visibility = View.GONE
                    adRating.visibility = View.GONE
                } else {
                    adStorePrice.text = "${it.store} | ${it.price}"
                    adRating.rating = it.starRating?.toFloat() ?: 0f
                }
                nativeAdView.callToActionView = adClick
                nativeAdView.setNativeAd(it)
            }
            data.add(Random.nextInt(1,2), nativeAdView)
            binding.recyclerView.adapter?.notifyItemInserted(1)
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}