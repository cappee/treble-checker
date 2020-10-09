package dev.cappee.treble.adapter

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import dev.cappee.treble.fragment.DeviceFragment
import dev.cappee.treble.fragment.RootFragment
import dev.cappee.treble.fragment.TrebleFragment

class ViewPagerAdaper(fragmentActivity: FragmentActivity, private val bundle: Bundle) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> TrebleFragment()
            1 -> RootFragment()
            2 -> {
                val deviceFragment = DeviceFragment()
                deviceFragment.arguments = bundle
                deviceFragment
            }
            else -> TrebleFragment()
        }
    }
}