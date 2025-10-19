package com.example.bitechecktest

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.bitechecktest.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set listeners for all login buttons
        binding.btnLogin.setOnClickListener { login() }
        binding.btnGoogle.setOnClickListener { login() }
        binding.btnFacebook.setOnClickListener { login() }
        binding.btnx.setOnClickListener { login() }
    }

    private fun login() {
        // Create an intent to open the main part of the app
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)

        // This prevents the user from pressing the back button to return here after logging in.
        finish()
    }
}