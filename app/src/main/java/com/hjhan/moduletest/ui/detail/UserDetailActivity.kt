package com.hjhan.moduletest.ui.detail

import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.hjhan.moduletest.R
import com.hjhan.moduletest.ui.theme.ModuleTestTheme
import com.hjhan.moduletest.util.Constants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserDetailActivity : AppCompatActivity() {

    private val viewModel: UserDetailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val userId = intent.getIntExtra(Constants.EXTRA_USER_ID, -1)
        val userName = intent.getStringExtra(Constants.EXTRA_USER_NAME)

        if (userId == -1) {
            Toast.makeText(this, getString(R.string.invalid_access), Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        supportActionBar?.hide()
        viewModel.onIntent(UserDetailViewModel.Intent.LoadUser(userId))

        setContent {
            ModuleTestTheme {
                UserDetailScreen(
                    viewModel = viewModel,
                    userName = userName,
                    onNavigateUp = { onBackPressedDispatcher.onBackPressed() }
                )
            }
        }
    }
}
