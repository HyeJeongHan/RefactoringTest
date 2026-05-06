package com.hjhan.moduletest.ui.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hjhan.moduletest.R
import com.hjhan.moduletest.domain.model.User
import com.hjhan.moduletest.ui.theme.ModuleTestTheme
import com.hjhan.moduletest.util.DateUtils

@Composable
fun MainScreen(
    viewModel: MainViewModel,
    onNavigateToDetail: (Int, String, String) -> Unit,
    onNavigateToLogin: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    MainContent(
        uiState = uiState,
        username = viewModel.username,
        onIntent = viewModel::onIntent,
        onUserClick = { user ->
            onNavigateToDetail(user.id, user.name, user.email)
        },
        onLogout = {
            viewModel.onIntent(MainViewModel.Intent.Logout)
            onNavigateToLogin()
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainContent(
    uiState: MainViewModel.UiState,
    username: String,
    onIntent: (MainViewModel.Intent) -> Unit,
    onUserClick: (User) -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var menuExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                actions = {
                    Box {
                        IconButton(onClick = { menuExpanded = true }) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = stringResource(R.string.label_more_menu)
                            )
                        }
                        DropdownMenu(
                            expanded = menuExpanded,
                            onDismissRequest = { menuExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.menu_refresh)) },
                                onClick = {
                                    onIntent(MainViewModel.Intent.Refresh)
                                    menuExpanded = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.menu_favorites)) },
                                onClick = {
                                    onIntent(MainViewModel.Intent.ShowFavorites)
                                    menuExpanded = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.menu_logout)) },
                                onClick = {
                                    menuExpanded = false
                                    onLogout()
                                }
                            )
                        }
                    }
                }
            )
        },
        modifier = modifier
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Text(
                text = stringResource(R.string.welcome_message, username),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            )

            OutlinedTextField(
                value = searchQuery,
                onValueChange = {
                    searchQuery = it
                    onIntent(MainViewModel.Intent.Search(it.trim()))
                },
                placeholder = { Text(stringResource(R.string.search_hint)) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )

            when (uiState) {
                is MainViewModel.UiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                is MainViewModel.UiState.Success -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(vertical = 4.dp)
                    ) {
                        items(uiState.users, key = { it.id }) { user ->
                            UserItem(
                                user = user,
                                onUserClick = onUserClick,
                                onFavoriteClick = { u, isFav ->
                                    onIntent(MainViewModel.Intent.ToggleFavorite(u.id, isFav))
                                }
                            )
                        }
                    }
                }
                is MainViewModel.UiState.Empty -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.text_empty),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                is MainViewModel.UiState.Error -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = stringResource(R.string.error_format, uiState.message),
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { onIntent(MainViewModel.Intent.LoadUsers) }) {
                            Text(stringResource(R.string.btn_retry))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun UserItem(
    user: User,
    onUserClick: (User) -> Unit,
    onFavoriteClick: (User, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var isFavorite by remember(user.id, user.isFavorite) { mutableStateOf(user.isFavorite) }

    Card(
        onClick = { onUserClick(user) },
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = user.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = user.email,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = user.phone ?: stringResource(R.string.text_no_phone),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
                Text(
                    text = DateUtils.getRelativeTimeString(user.lastUpdated),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            IconButton(
                onClick = {
                    isFavorite = !isFavorite
                    onFavoriteClick(user, isFavorite)
                }
            ) {
                Text(
                    text = if (isFavorite) "★" else "☆",
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MainContentSuccessPreview() {
    ModuleTestTheme {
        MainContent(
            uiState = MainViewModel.UiState.Success(
                listOf(
                    User(id = 1, name = "홍길동", username = "hong", email = "hong@example.com", phone = "010-1234-5678", website = null, address = null, company = null),
                    User(id = 2, name = "김철수", username = "kim", email = "kim@example.com", phone = null, website = null, address = null, company = null, isFavorite = true)
                )
            ),
            username = "admin",
            onIntent = {},
            onUserClick = {},
            onLogout = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun MainContentLoadingPreview() {
    ModuleTestTheme {
        MainContent(
            uiState = MainViewModel.UiState.Loading,
            username = "admin",
            onIntent = {},
            onUserClick = {},
            onLogout = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun MainContentErrorPreview() {
    ModuleTestTheme {
        MainContent(
            uiState = MainViewModel.UiState.Error("네트워크 연결을 확인해주세요"),
            username = "admin",
            onIntent = {},
            onUserClick = {},
            onLogout = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun UserItemPreview() {
    ModuleTestTheme {
        UserItem(
            user = User(id = 1, name = "홍길동", username = "hong", email = "hong@example.com", phone = "010-1234-5678", website = null, address = null, company = null),
            onUserClick = {},
            onFavoriteClick = { _, _ -> }
        )
    }
}
