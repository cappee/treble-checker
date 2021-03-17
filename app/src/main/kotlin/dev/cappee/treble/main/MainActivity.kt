package dev.cappee.treble.main

import android.content.Intent
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.callbacks.onDismiss
import com.afollestad.materialdialogs.list.ItemListener
import com.afollestad.materialdialogs.list.listItems
import com.google.android.gms.ads.*
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import dev.cappee.treble.R
import dev.cappee.treble.databinding.ActivityMainBinding
import dev.cappee.treble.device.DeviceHelper
import dev.cappee.treble.main.viewpager.ViewPagerAdapter
import dev.cappee.treble.root.RootHelper
import dev.cappee.treble.settings.SettingsActivity
import dev.cappee.treble.treble.TrebleHelper
import kotlinx.coroutines.*
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class MainActivity : AppCompatActivity() {

    private lateinit var menuDialog: MaterialDialog

    private val viewModel: MainViewModel by viewModels {
        MainViewModelFactory(applicationContext, TrebleHelper.init(this), RootHelper.init(this), DeviceHelper.init(this))
    }

    private val coroutine = CoroutineScope(Dispatchers.Main + Job())
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        coroutine.launch {
            //Init AdMob SDK
            launch(Dispatchers.Default) {
                MobileAds.initialize(this@MainActivity)
                viewModel.getNativeAds()
            }

            //Running GPU stuff
            val glSurfaceView = GLSurfaceView(this@MainActivity)
            glSurfaceView.apply {
                setEGLConfigChooser(8, 8, 8, 8, 16, 0)
                setRenderer(object : GLSurfaceView.Renderer {
                    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
                        DeviceHelper.gpu = "${gl?.glGetString(GL10.GL_VENDOR)} ${gl?.glGetString(GL10.GL_RENDERER)}"
                        runOnUiThread {
                            binding.root.removeView(glSurfaceView)
                        }
                    }
                    override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {}
                    override fun onDrawFrame(gl: GL10) {}
                })
            }
            binding.root.addView(glSurfaceView)
        }

        //Init ViewPager
        binding.viewPager.apply {
            (getChildAt(0) as RecyclerView).overScrollMode = RecyclerView.OVER_SCROLL_NEVER
            adapter = ViewPagerAdapter(this@MainActivity)
        }

        //Init TabLayout
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab: TabLayout.Tab, position: Int ->
            when (position) {
                0 -> tab.text = "Treble"
                1 -> tab.text = "Root"
                2 -> tab.text = "Device"
            }
        }.attach()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        coroutine.launch(Dispatchers.Main) {
            menuDialog = MaterialDialog(this@MainActivity, BottomSheet(LayoutMode.WRAP_CONTENT))
            menuDialog.apply {
                title(R.string.menu)
                var selection = -1
                listItems(items = listOf(/*getText(R.string.tools), */getText(R.string.settings)),
                    selection = object : ItemListener {
                        override fun invoke(dialog: MaterialDialog, index: Int, text: CharSequence) {
                            selection = index
                            dismiss()
                        }
                    }
                )
                onDismiss {
                    when (selection) {
                        /*0 -> {
                            //startActivity(Intent(this@MainActivity, ToolsActivity::class.java))
                        }*/
                        0 -> {
                            startActivity(Intent(this@MainActivity, SettingsActivity::class.java))
                        }
                    }
                }
            }
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onResume() {
        super.onResume()
        viewModel.updateValues()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu -> {
                if (::menuDialog.isInitialized)
                    menuDialog.show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

}