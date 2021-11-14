package com.example.cool_app

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.login_page.*

class LoginPage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_page)

        buttonLogin.setOnClickListener {
            val email = editTextTextEmail.text.toString()
            val password = editTextTextPassword.text.toString()

            if (email == "mda200@mail.ru" && password == "1") {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }

        }
    }

    private fun shortToast(string: String) {
        Toast.makeText(this, string, Toast.LENGTH_SHORT).show()
    }
}