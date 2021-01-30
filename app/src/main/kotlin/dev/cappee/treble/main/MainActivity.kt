package dev.cappee.treble.main

import android.opengl.GLSurfaceView
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.list.ItemListener
import com.afollestad.materialdialogs.list.listItems
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import dev.cappee.treble.R
import dev.cappee.treble.main.viewpager.ViewPagerAdapter
import dev.cappee.treble.databinding.ActivityMainBinding
import dev.cappee.treble.device.DeviceHelper
import dev.cappee.treble.root.Root
import dev.cappee.treble.root.RootHelper
import dev.cappee.treble.treble.TrebleHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class MainActivity : AppCompatActivity() {

    private val trebleBundle = Bundle()
    private val rootBundle = Bundle()
    private val deviceBundle = Bundle()

    private var glSurfaceView: GLSurfaceView? = null
    private var glGpu = ""
    private val glRenderer = object : GLSurfaceView.Renderer {
        override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
            glGpu = gl?.glGetString(GL10.GL_VENDOR) + " " + gl?.glGetString(GL10.GL_RENDERER)
            runOnUiThread {
                glSurfaceView?.visibility = View.GONE
            }
        }
        override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {}
        override fun onDrawFrame(gl: GL10) {}
    }
    private lateinit var menuDialog: MaterialDialog

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        lifecycleScope.launch(Dispatchers.Main) {
            println("THREAD MAIN: ${Thread.currentThread()}")
            //Running GPU stuff
            withContext(Dispatchers.Main) {
                glSurfaceView = GLSurfaceView(this@MainActivity)
                glSurfaceView?.apply {
                    setEGLConfigChooser(8, 8, 8, 8, 16, 0)
                    setRenderer(glRenderer)
                }
            }
            binding.root.addView(glSurfaceView)

            //Get info from helper class
            withContext(Dispatchers.Default) {
                trebleBundle.putParcelable("info", TrebleHelper.get(this@MainActivity))
                rootBundle.putParcelable("info", RootHelper.get(this@MainActivity))
                deviceBundle.putParcelable("info", DeviceHelper.get(this@MainActivity, glGpu))
            }

            //Init ViewPager
            binding.viewPager.adapter = withContext(Dispatchers.Default) {
                ViewPagerAdapter(this@MainActivity, trebleBundle, rootBundle, deviceBundle)
            }

            //Init TabLayout
            TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab: TabLayout.Tab, position: Int ->
                when (position) {
                    0 -> tab.text = "Treble"
                    1 -> tab.text = "Root"
                    2 -> tab.text = "Device"
                }
            }.attach()

            //Hide progressbar
            binding.progressBar.visibility = View.INVISIBLE
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        lifecycleScope.launch(Dispatchers.Main) {
            menuDialog = MaterialDialog(this@MainActivity, BottomSheet(LayoutMode.WRAP_CONTENT))
            menuDialog.apply {
                title(R.string.menu)
                listItems(null,
                    listOf(getText(R.string.tools), getText(R.string.about_us), getText(R.string.settings)),
                    null,
                    false,
                    object : ItemListener {
                        override fun invoke(dialog: MaterialDialog, index: Int, text: CharSequence) {
                            when (index) {
                                0 -> {
                                    //TODO: Open tools activity
                                    dismiss()
                                }
                                1 -> {
                                    //TODO: Open about us activity
                                    dismiss()
                                }
                                2 -> {
                                    //TODO: Open settings activity
                                    dismiss()
                                }
                            }
                        }
                    }
                )
            }
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu -> {
                menuDialog.show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        glSurfaceView?.onResume()
    }

    override fun onPause() {
        super.onPause()
        glSurfaceView?.onPause()
    }

}