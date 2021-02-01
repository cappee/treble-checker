package dev.cappee.treble.main.viewpager

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import dev.cappee.treble.device.Device
import dev.cappee.treble.device.DeviceFragment
import dev.cappee.treble.root.Root
import dev.cappee.treble.root.RootFragment
import dev.cappee.treble.treble.Treble
import dev.cappee.treble.treble.TrebleFragment

class ViewPagerAdapter(fragmentActivity: FragmentActivity,
                       private val treble: Treble,
                       private val root: Root,
                       private val device: Device) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> TrebleFragment.newInstance(treble)
            1 -> RootFragment.newInstance(root)
            2 -> DeviceFragment.newInstance(device)
            else -> TrebleFragment()
        }
    }
}