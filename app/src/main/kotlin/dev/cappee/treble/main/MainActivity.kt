package dev.cappee.treble.main

import android.opengl.GLSurfaceView
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import dev.cappee.treble.main.viewpager.ViewPagerAdapter
import dev.cappee.treble.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class MainActivity : AppCompatActivity() {

    private var glSurfaceView: GLSurfaceView? = null
    private val bundle = Bundle()
    private val glRenderer = object : GLSurfaceView.Renderer {
        override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
            bundle.putString("GPU_INFO", gl?.glGetString(GL10.GL_VENDOR) + " " + gl?.glGetString(GL10.GL_RENDERER))
            runOnUiThread {
                glSurfaceView?.visibility = View.GONE
            }
        }
        override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {}
        override fun onDrawFrame(gl: GL10) {}
    }

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "Elleueleu"

        //Running GPU stuff
        lifecycleScope.launch(Dispatchers.Main) {
            glSurfaceView = GLSurfaceView(this@MainActivity)
            glSurfaceView?.apply {
                setEGLConfigChooser(8, 8, 8, 8, 16, 0)
                setRenderer(glRenderer)
            }
            binding.root.addView(glSurfaceView)
        }

        binding.viewPager.adapter = ViewPagerAdapter(this, bundle)

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab: TabLayout.Tab, position: Int ->
            when (position) {
                0 -> tab.text = "Treble"
                1 -> tab.text = "Root"
                2 -> tab.text = "Device"
            }
        }.attach()

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