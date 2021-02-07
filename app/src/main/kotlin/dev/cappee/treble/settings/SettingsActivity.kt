package dev.cappee.treble.settings

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import dev.cappee.treble.R
import dev.cappee.treble.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        //Init preference fragment
        supportFragmentManager
            .beginTransaction()
            .replace(binding.frameLayout.id, SettingsFragment())
            .commit()

        //Setup back button toolbar
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }



    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}