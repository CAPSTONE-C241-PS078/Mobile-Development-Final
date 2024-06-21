package com.example.cataractnow

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import com.example.cataractnow.databinding.ActivityRegisterBinding
import com.example.cataractnow.retrofit.ApiConfig
import com.example.cataractnow.response.LoginResponse
import kotlinx.coroutines.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnlogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        binding.btnregister.setOnClickListener {
            val fullname = binding.edfullname.text.toString()
            val email = binding.edemail.text.toString()
            val password = binding.edpassword.text.toString()

            if (validateInputs(fullname, email, password)) {
                // Panggil fungsi register
                registerUser(fullname, email, password)
            }
        }
    }

    private fun validateInputs(fullname: String, email: String, password: String): Boolean {
        if (fullname.isEmpty()) {
            Toast.makeText(this, resources.getString(R.string.message_enter_fullname), Toast.LENGTH_SHORT).show()
            return false
        }

        if (email.isEmpty()) {
            Toast.makeText(this, resources.getString(R.string.message_enter_email), Toast.LENGTH_SHORT).show()
            return false
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, resources.getString(R.string.message_valid_email), Toast.LENGTH_SHORT).show()
            return false
        }

        if (password.isEmpty()) {
            Toast.makeText(this, resources.getString(R.string.message_enter_password), Toast.LENGTH_SHORT).show()
            return false
        }

        if (password.length < 8) {
            Toast.makeText(this, resources.getString(R.string.message_password_length), Toast.LENGTH_SHORT).show()
            return false
        }

        if (!password.any { it.isUpperCase() }) {
            Toast.makeText(this, resources.getString(R.string.message_password_uppercase), Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun registerUser(FullName: String, Email: String, Password: String) {
        // Membuat JSON object untuk request body
        val jsonObject = JSONObject()
        jsonObject.put("FullName", FullName)
        jsonObject.put("Email", Email)
        jsonObject.put("Password", Password)

        // Membuat RequestBody dari JSON object
        val requestBody = RequestBody.create(
            "application/json".toMediaTypeOrNull(),
            jsonObject.toString()
        )

        val apiService = ApiConfig.getApiService()
        val call = apiService.regist(requestBody)

        val dialogView = LayoutInflater.from(this).inflate(R.layout.loading, null)
        val progressDialog = ProgressDialog(this)
        progressDialog.setCancelable(false)
        progressDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        progressDialog.show()
        progressDialog.setContentView(dialogView)
        val messageTextView: TextView = dialogView.findViewById(R.id.textViewMessage)
        messageTextView.text = resources.getString(R.string.message_registering)

        call.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                progressDialog.dismiss()
                if (response.isSuccessful) {
                    val registerResponse = response.body()
                    if (registerResponse?.message != null) {
                        // Registrasi berhasil
                        Toast.makeText(this@RegisterActivity, resources.getString(R.string.message_registration_success), Toast.LENGTH_SHORT).show()
                        // Lanjutkan ke LoginActivity
                        startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                        finish()
                    } else {
                        // Respons kosong atau tidak valid
                        Toast.makeText(this@RegisterActivity, resources.getString(R.string.message_invalid_user_data), Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // Tanggapan tidak berhasil
                    val errorMessage = response.message()
                    Toast.makeText(this@RegisterActivity, "${resources.getString(R.string.message_registration_failed)} $errorMessage", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                progressDialog.dismiss()
                val errorMessage = t.message ?: resources.getString(R.string.message_unknown_error)
                Toast.makeText(this@RegisterActivity, "${resources.getString(R.string.message_registration_failed)} $errorMessage", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
