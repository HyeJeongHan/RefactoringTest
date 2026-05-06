package com.hjhan.moduletest.ui.login

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.hjhan.moduletest.ui.main.MainActivity
import com.hjhan.moduletest.ui.theme.ModuleTestTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (viewModel.isAlreadyLoggedIn) {
            navigateToMain()
            return
        }

        supportActionBar?.hide()

        setContent {
            ModuleTestTheme {
                LoginScreen(
                    viewModel = viewModel,
                    onLoginSuccess = { navigateToMain() }
                )
            }
        }
    }

    private fun navigateToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}