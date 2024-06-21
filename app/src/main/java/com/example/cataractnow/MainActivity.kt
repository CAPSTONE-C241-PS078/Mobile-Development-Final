package com.example.cataractnow

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.example.cataractnow.databinding.ActivityMainBinding
import com.example.cataractnow.ui.News.NewsFragment
import com.example.cataractnow.ui.analyze.AnalyzeFragment
import com.example.cataractnow.ui.home.HomeFragment
import nl.joery.animatedbottombar.AnimatedBottomBar

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Setup BottomNavigationView
        binding.navView.setOnTabSelectListener(object : AnimatedBottomBar.OnTabSelectListener {
            override fun onTabSelected(
                lastIndex: Int,
                lastTab: AnimatedBottomBar.Tab?,
                newIndex: Int,
                newTab: AnimatedBottomBar.Tab
            ) {
                val fragment = when (newIndex) {
                    0 -> HomeFragment()
                    1 -> AnalyzeFragment()
                    2 -> NewsFragment()
                    else -> null
                }
                if (fragment != null) {
                    replaceFragment(fragment)
                }
            }
        })

        binding.btnlogout.setOnClickListener {
            logout()
        }

        binding.btnlanguage?.setOnClickListener {
            changeLanguage()
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment_activity_main2, fragment)
            .commitAllowingStateLoss() // Use commitAllowingStateLoss if necessary
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.btnlanguage -> {
                changeLanguage()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun changeLanguage() {
        startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
    }

    private fun logout() {
        clearLoginStatus()
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun clearLoginStatus() {
        // Membersihkan status login dari penyimpanan lokal
        val sharedPreferences = getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("isLoggedIn", false)
        editor.apply()
    }
}
