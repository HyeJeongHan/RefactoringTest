package com.hjhan.moduletest

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.hjhan.moduletest.util.Constants
import com.hjhan.moduletest.util.SharedPrefsManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    @Inject lateinit var sharedPrefsManager: SharedPrefsManager

    private lateinit var etUsername: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (sharedPrefsManager.isLoggedIn()) {
            navigateToMain()
            return
        }

        setContentView(R.layout.activity_login)
        supportActionBar?.hide()
        initViews()
    }

    private fun initViews() {
        etUsername = findViewById(R.id.et_username)
        etPassword = findViewById(R.id.et_password)
        btnLogin = findViewById(R.id.btn_login)
        progressBar = findViewById(R.id.progress_bar)

        btnLogin.setOnClickListener { handleLoginClick() }
    }

    private fun handleLoginClick() {
        val username = etUsername.text.toString().trim()
        val password = etPassword.text.toString().trim()

        if (username.isEmpty()) {
            etUsername.error = "아이디를 입력해주세요"
            etUsername.requestFocus()
            return
        }
        if (password.isEmpty()) {
            etPassword.error = "비밀번호를 입력해주세요"
            etPassword.requestFocus()
            return
        }
        if (password.length < 4) {
            etPassword.error = "비밀번호는 4자 이상이어야 합니다"
            return
        }

        performLogin(username, password)
    }

    private fun performLogin(username: String, password: String) {
        progressBar.visibility = View.VISIBLE
        btnLogin.isEnabled = false

        Handler(Looper.getMainLooper()).postDelayed({
            if (username == Constants.TEST_USERNAME && password == Constants.TEST_PASSWORD) {
                sharedPrefsManager.setLoggedIn(true)
                sharedPrefsManager.saveUsername(username)
                sharedPrefsManager.saveUserToken("token_${System.currentTimeMillis()}")

                progressBar.visibility = View.GONE
                Toast.makeText(this, "로그인 성공!", Toast.LENGTH_SHORT).show()
                navigateToMain()
            } else {
                progressBar.visibility = View.GONE
                btnLogin.isEnabled = true
                Toast.makeText(this, "아이디 또는 비밀번호가 올바르지 않습니다", Toast.LENGTH_LONG).show()
            }
        }, 1200)
    }

    private fun navigateToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
