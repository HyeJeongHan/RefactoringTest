package com.hjhan.moduletest.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.hjhan.moduletest.ui.main.MainActivity
import com.hjhan.moduletest.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private val viewModel: LoginViewModel by viewModels()

    private lateinit var etUsername: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (viewModel.isAlreadyLoggedIn) {
            navigateToMain()
            return
        }

        setContentView(R.layout.activity_login)
        supportActionBar?.hide()
        initViews()
        observeViewModel()
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

        viewModel.onIntent(LoginViewModel.Intent.Login(username, password))
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is LoginViewModel.UiState.Idle -> {
                            progressBar.visibility = View.GONE
                            btnLogin.isEnabled = true
                        }
                        is LoginViewModel.UiState.Loading -> {
                            progressBar.visibility = View.VISIBLE
                            btnLogin.isEnabled = false
                        }
                        is LoginViewModel.UiState.Success -> {
                            progressBar.visibility = View.GONE
                            Toast.makeText(this@LoginActivity, "로그인 성공!", Toast.LENGTH_SHORT).show()
                            navigateToMain()
                        }
                        is LoginViewModel.UiState.Error -> {
                            progressBar.visibility = View.GONE
                            btnLogin.isEnabled = true
                            Toast.makeText(this@LoginActivity, state.message, Toast.LENGTH_LONG).show()
                            viewModel.onIntent(LoginViewModel.Intent.ResetState)
                        }
                    }
                }
            }
        }
    }

    private fun navigateToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}