package com.hjhan.moduletest

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hjhan.moduletest.adapter.UserAdapter
import com.hjhan.moduletest.ui.main.MainViewModel
import com.hjhan.moduletest.util.Constants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()

    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvEmpty: TextView
    private lateinit var tvError: TextView
    private lateinit var btnRetry: Button
    private lateinit var etSearch: EditText
    private lateinit var tvWelcome: TextView
    private lateinit var userAdapter: UserAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!isLoggedIn()) {
            navigateToLogin()
            return
        }

        setContentView(R.layout.activity_main)
        initViews()
        setupRecyclerView()
        setupSearch()
        observeViewModel()
    }

    private fun isLoggedIn(): Boolean = viewModel.isLoggedIn

    private fun initViews() {
        recyclerView = findViewById(R.id.recycler_view)
        progressBar = findViewById(R.id.progress_bar)
        tvEmpty = findViewById(R.id.tv_empty)
        tvError = findViewById(R.id.tv_error)
        btnRetry = findViewById(R.id.btn_retry)
        etSearch = findViewById(R.id.et_search)
        tvWelcome = findViewById(R.id.tv_welcome)

        tvWelcome.text = "안녕하세요, ${viewModel.username}님!"
        btnRetry.setOnClickListener { viewModel.onIntent(MainViewModel.Intent.LoadUsers) }
    }

    private fun setupRecyclerView() {
        userAdapter = UserAdapter(
            users = mutableListOf(),
            onUserClick = { user ->
                Intent(this, UserDetailActivity::class.java).apply {
                    putExtra(Constants.EXTRA_USER_ID, user.id)
                    putExtra(Constants.EXTRA_USER_NAME, user.name)
                    putExtra(Constants.EXTRA_USER_EMAIL, user.email)
                }.also { startActivity(it) }
            },
            onFavoriteClick = { user, isFavorite ->
                viewModel.onIntent(MainViewModel.Intent.ToggleFavorite(user.id, isFavorite))
            }
        )
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = userAdapter
    }

    private fun setupSearch() {
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                viewModel.onIntent(MainViewModel.Intent.Search(s?.toString()?.trim() ?: ""))
            }
        })
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is MainViewModel.UiState.Loading -> showLoading()
                        is MainViewModel.UiState.Success -> {
                            userAdapter.updateUsers(state.users)
                            showContent()
                        }
                        is MainViewModel.UiState.Empty -> showEmpty()
                        is MainViewModel.UiState.Error -> showError(state.message)
                    }
                }
            }
        }
    }

    private fun showLoading() {
        progressBar.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
        tvEmpty.visibility = View.GONE
        tvError.visibility = View.GONE
        btnRetry.visibility = View.GONE
    }

    private fun showContent() {
        progressBar.visibility = View.GONE
        recyclerView.visibility = View.VISIBLE
        tvEmpty.visibility = View.GONE
        tvError.visibility = View.GONE
        btnRetry.visibility = View.GONE
    }

    private fun showEmpty() {
        progressBar.visibility = View.GONE
        recyclerView.visibility = View.GONE
        tvEmpty.visibility = View.VISIBLE
        tvError.visibility = View.GONE
        btnRetry.visibility = View.GONE
    }

    private fun showError(message: String) {
        progressBar.visibility = View.GONE
        recyclerView.visibility = View.GONE
        tvEmpty.visibility = View.GONE
        tvError.visibility = View.VISIBLE
        tvError.text = "오류: $message"
        btnRetry.visibility = View.VISIBLE
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_refresh -> { viewModel.onIntent(MainViewModel.Intent.Refresh); true }
            R.id.action_favorites -> { viewModel.onIntent(MainViewModel.Intent.ShowFavorites); true }
            R.id.action_logout -> { logout(); true }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun logout() {
        viewModel.onIntent(MainViewModel.Intent.Logout)
        navigateToLogin()
    }

    private fun navigateToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}
