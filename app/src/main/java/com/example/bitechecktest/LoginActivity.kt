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

        binding.btnLogin.setOnClickListener { login() }
        binding.btnGoogle.setOnClickListener { login() }
        binding.btnFacebook.setOnClickListener { login() }
        binding.btnx.setOnClickListener { login() }
        binding.tvSignUp.setOnClickListener {
            // start the SignUpActivity, LoginActivity not finish
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
    }

    private fun login() {
        // intent to open main
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)

        // biar user gabisa pencet back abis login
        finish()
    }
}