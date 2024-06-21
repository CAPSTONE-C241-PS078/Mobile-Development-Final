package com.example.cataractnow

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cataractnow.databinding.ActivityLoginBinding
import com.example.cataractnow.retrofit.ApiConfig
import com.example.cataractnow.response.LoginResponse
import kotlinx.coroutines.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnregister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        binding.btnlogin.setOnClickListener {
            val email = binding.edemail.text.toString()
            val password = binding.edpassword.text.toString()
            if (validateInputs(email, password)) {
                loginUser(email, password)
            }
        }

        if (isLoggedIn()) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun validateInputs(email: String, password: String): Boolean {
        if (email.isEmpty()) {
            Toast.makeText(this, R.string.message_enter_email, Toast.LENGTH_SHORT).show()
            return false
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, R.string.message_valid_email, Toast.LENGTH_SHORT).show()
            return false
        }

        if (password.isEmpty()) {
            Toast.makeText(this, R.string.message_enter_password, Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun loginUser(Email: String, Password: String) {
        // Membuat JSON object untuk request body
        val jsonObject = JSONObject()
        jsonObject.put("Email", Email)
        jsonObject.put("Password", Password)

        // Membuat RequestBody dari JSON object
        val requestBody = RequestBody.create(
            "application/json".toMediaTypeOrNull(),
            jsonObject.toString()
        )

        val apiService = ApiConfig.getApiService()
        val call = apiService.login(requestBody)

        val dialogView = LayoutInflater.from(this).inflate(R.layout.loading, null)
        val progressDialog = ProgressDialog(this)
        progressDialog.setCancelable(false)
        progressDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        progressDialog.show()
        progressDialog.setContentView(dialogView)
        val messageTextView: TextView = dialogView.findViewById(R.id.textViewMessage)
        messageTextView.text = getString(R.string.message_logging_in)
        call.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                progressDialog.dismiss()
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    if (loginResponse?.message != null) {
                        saveLoginStatus(true)
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this@LoginActivity, R.string.message_invalid_user_data, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    val errorMessage = response.message()
                    Toast.makeText(this@LoginActivity, getString(R.string.message_login_failed) + errorMessage, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                progressDialog.dismiss()
                val errorMessage = t.message ?: getString(R.string.message_unknown_error)
                Toast.makeText(this@LoginActivity, getString(R.string.message_login_failed) + errorMessage, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun isLoggedIn(): Boolean {
        val sharedPreferences = getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean("isLoggedIn", false)
    }

    private fun saveLoginStatus(isLoggedIn: Boolean) {
        val sharedPreferences = getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("isLoggedIn", isLoggedIn)
        editor.apply()
    }
}
