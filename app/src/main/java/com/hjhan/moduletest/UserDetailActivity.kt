package com.hjhan.moduletest

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.hjhan.moduletest.model.User
import com.hjhan.moduletest.ui.detail.UserDetailViewModel
import com.hjhan.moduletest.util.Constants
import com.hjhan.moduletest.util.DateUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class UserDetailActivity : AppCompatActivity() {

    private val viewModel: UserDetailViewModel by viewModels()

    private var userId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_detail)

        userId = intent.getIntExtra(Constants.EXTRA_USER_ID, -1)
        val userName = intent.getStringExtra(Constants.EXTRA_USER_NAME)

        if (userId == -1) {
            Toast.makeText(this, "잘못된 접근입니다", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = userName ?: "사용자 정보"
        }

        observeViewModel()
        viewModel.onIntent(UserDetailViewModel.Intent.LoadUser(userId))
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is UserDetailViewModel.UiState.Loading -> showLoading(true)
                        is UserDetailViewModel.UiState.Success -> {
                            showLoading(false)
                            bindUserData(state.user)
                        }
                        is UserDetailViewModel.UiState.Error -> {
                            showLoading(false)
                            Toast.makeText(this@UserDetailActivity, "오류: ${state.message}", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
    }

    private fun bindUserData(user: User) {
        findViewById<TextView>(R.id.tv_detail_name).text = user.name
        findViewById<TextView>(R.id.tv_detail_email).text = user.email
        findViewById<TextView>(R.id.tv_detail_phone).text = user.phone ?: "-"
        findViewById<TextView>(R.id.tv_detail_website).text = user.website ?: "-"

        val addressText = user.address?.let {
            "${it.street}, ${it.suite}\n${it.city} ${it.zipcode}"
        } ?: "-"
        findViewById<TextView>(R.id.tv_detail_address).text = addressText

        val companyText = user.company?.let {
            "${it.name}\n\"${it.catchPhrase}\""
        } ?: "-"
        findViewById<TextView>(R.id.tv_detail_company).text = companyText

        findViewById<TextView>(R.id.tv_detail_last_updated).text =
            "마지막 업데이트: ${DateUtils.formatTimestamp(user.lastUpdated)}"

        val btnFavorite = findViewById<Button>(R.id.btn_favorite)
        updateFavoriteButton(btnFavorite, user.isFavorite)
        btnFavorite.setOnClickListener {
            val newState = !user.isFavorite
            viewModel.onIntent(UserDetailViewModel.Intent.ToggleFavorite(userId, newState))
            val msg = if (newState) "즐겨찾기에 추가했습니다" else "즐겨찾기에서 제거했습니다"
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateFavoriteButton(btn: Button, isFavorite: Boolean) {
        btn.text = if (isFavorite) "★ 즐겨찾기 해제" else "☆ 즐겨찾기 추가"
    }

    private fun showLoading(show: Boolean) {
        findViewById<ProgressBar>(R.id.progress_bar).visibility =
            if (show) View.VISIBLE else View.GONE
        findViewById<View>(R.id.scroll_content).visibility =
            if (show) View.GONE else View.VISIBLE
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
