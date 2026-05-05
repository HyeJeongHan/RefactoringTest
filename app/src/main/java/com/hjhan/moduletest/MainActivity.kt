package com.hjhan.moduletest

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hjhan.moduletest.adapter.UserAdapter
import com.hjhan.moduletest.model.User
import com.hjhan.moduletest.repository.UserRepository
import com.hjhan.moduletest.util.Constants
import com.hjhan.moduletest.util.SharedPrefsManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject lateinit var sharedPrefs: SharedPrefsManager
    @Inject lateinit var userRepository: UserRepository

    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvEmpty: TextView
    private lateinit var tvError: TextView
    private lateinit var btnRetry: Button
    private lateinit var etSearch: EditText
    private lateinit var tvWelcome: TextView

    private lateinit var userAdapter: UserAdapter
    private val allUsers: MutableList<User> = mutableListOf()
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!sharedPrefs.isLoggedIn()) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        setContentView(R.layout.activity_main)
        initViews()
        setupRecyclerView()
        setupSearch()
        loadUsers()
    }

    private fun initViews() {
        recyclerView = findViewById(R.id.recycler_view)
        progressBar = findViewById(R.id.progress_bar)
        tvEmpty = findViewById(R.id.tv_empty)
        tvError = findViewById(R.id.tv_error)
        btnRetry = findViewById(R.id.btn_retry)
        etSearch = findViewById(R.id.et_search)
        tvWelcome = findViewById(R.id.tv_welcome)

        tvWelcome.text = "안녕하세요, ${sharedPrefs.getUsername()}님!"
        btnRetry.setOnClickListener { loadUsers() }
    }

    private fun setupRecyclerView() {
        userAdapter = UserAdapter(
            users = allUsers,
            onUserClick = { user ->
                Intent(this, UserDetailActivity::class.java).apply {
                    putExtra(Constants.EXTRA_USER_ID, user.id)
                    putExtra(Constants.EXTRA_USER_NAME, user.name)
                    putExtra(Constants.EXTRA_USER_EMAIL, user.email)
                }.also { startActivity(it) }
            },
            onFavoriteClick = { user, isFavorite ->
                userRepository.toggleFavorite(user.id, isFavorite)
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
                val query = s?.toString()?.trim() ?: ""
                if (query.isEmpty()) {
                    userAdapter.updateUsers(allUsers)
                } else {
                    val filtered = allUsers.filter {
                        it.name.contains(query, ignoreCase = true) ||
                        it.email.contains(query, ignoreCase = true) ||
                        (it.phone?.contains(query) == true)
                    }
                    userAdapter.updateUsers(filtered)
                }
            }
        })
    }

    private fun loadUsers() {
        showLoading()

        userRepository.fetchUsers(object : UserRepository.UserListCallback {
            override fun onSuccess(users: List<User>) {
                handler.post {
                    allUsers.clear()
                    allUsers.addAll(users)
                    userAdapter.updateUsers(allUsers)

                    if (users.isEmpty()) showEmpty() else showContent()
                }
            }

            override fun onError(error: String) {
                handler.post { showError(error) }
            }
        })
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
            R.id.action_refresh -> {
                userRepository.clearCache()
                loadUsers()
                true
            }
            R.id.action_favorites -> {
                showFavorites()
                true
            }
            R.id.action_logout -> {
                logout()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showFavorites() {
        val favorites = userRepository.getFavoriteUsers()
        if (favorites.isEmpty()) {
            Toast.makeText(this, "즐겨찾기한 사용자가 없습니다", Toast.LENGTH_SHORT).show()
        } else {
            userAdapter.updateUsers(favorites)
            etSearch.setText("")
        }
    }

    private fun logout() {
        sharedPrefs.clearAll()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}
