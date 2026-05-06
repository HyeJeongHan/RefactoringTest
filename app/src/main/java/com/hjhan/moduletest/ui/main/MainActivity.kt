package com.hjhan.moduletest.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.hjhan.moduletest.ui.detail.UserDetailActivity
import com.hjhan.moduletest.ui.login.LoginActivity
import com.hjhan.moduletest.ui.theme.ModuleTestTheme
import com.hjhan.moduletest.util.Constants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!viewModel.isLoggedIn) {
            navigateToLogin()
            return
        }

        supportActionBar?.hide()

        setContent {
            ModuleTestTheme {
                MainScreen(
                    viewModel = viewModel,
                    onNavigateToDetail = { userId, userName, userEmail ->
                        Intent(this, UserDetailActivity::class.java).apply {
                            putExtra(Constants.EXTRA_USER_ID, userId)
                            putExtra(Constants.EXTRA_USER_NAME, userName)
                            putExtra(Constants.EXTRA_USER_EMAIL, userEmail)
                        }.also { startActivity(it) }
                    },
                    onNavigateToLogin = { navigateToLogin() }
                )
            }
        }
    }

    private fun navigateToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}
