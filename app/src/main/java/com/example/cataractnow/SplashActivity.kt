package com.example.cataractnow

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler

class SplashActivity : AppCompatActivity() {
    private val splashTime: Long = 3000 // Durasi splash screen (dalam milidetik)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Memeriksa status login
        if (isLoggedIn()) {
            // Jika pengguna sudah login, langsung arahkan ke MainActivity
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        } else {
            // Jika pengguna belum login, tampilkan splash screen
            setContentView(R.layout.activity_splash)

            // Delay untuk splash screen
            Handler().postDelayed({
                // Setelah splash screen selesai, arahkan ke LoginActivity
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }, splashTime)
        }
    }

    // Fungsi untuk memeriksa status login dari penyimpanan lokal
    private fun isLoggedIn(): Boolean {
        val sharedPreferences = getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean("isLoggedIn", false)
    }
}
