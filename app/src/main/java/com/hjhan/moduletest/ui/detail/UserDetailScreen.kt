package com.hjhan.moduletest.ui.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hjhan.moduletest.R
import com.hjhan.moduletest.domain.model.Address
import com.hjhan.moduletest.domain.model.Company
import com.hjhan.moduletest.domain.model.User
import com.hjhan.moduletest.ui.theme.ModuleTestTheme
import com.hjhan.moduletest.util.DateUtils

@Composable
fun UserDetailScreen(
    viewModel: UserDetailViewModel,
    userName: String?,
    onNavigateUp: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    UserDetailContent(
        uiState = uiState,
        userName = userName,
        onToggleFavorite = { userId, isFavorite ->
            viewModel.onIntent(UserDetailViewModel.Intent.ToggleFavorite(userId, isFavorite))
        },
        onNavigateUp = onNavigateUp
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserDetailContent(
    uiState: UserDetailViewModel.UiState,
    userName: String?,
    onToggleFavorite: (Int, Boolean) -> Unit,
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(userName ?: stringResource(R.string.user_info)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.navigate_up)
                        )
                    }
                }
            )
        },
        modifier = modifier
    ) { padding ->
        when (uiState) {
            is UserDetailViewModel.UiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is UserDetailViewModel.UiState.Success -> {
                UserDetailBody(
                    user = uiState.user,
                    onToggleFavorite = onToggleFavorite,
                    modifier = Modifier.padding(padding)
                )
            }
            is UserDetailViewModel.UiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.error_format, uiState.message),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun UserDetailBody(
    user: User,
    onToggleFavorite: (Int, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val addressText = user.address?.let {
        "${it.street}, ${it.suite}\n${it.city} ${it.zipcode}"
    } ?: "-"

    val companyText = user.company?.let {
        "${it.name}\n\"${it.catchPhrase}\""
    } ?: "-"

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        DetailRow(label = stringResource(R.string.label_name), value = user.name)
        DetailRow(label = stringResource(R.string.label_email), value = user.email)
        DetailRow(label = stringResource(R.string.label_phone), value = user.phone ?: "-")
        DetailRow(label = stringResource(R.string.label_website), value = user.website ?: "-")
        DetailRow(label = stringResource(R.string.label_address), value = addressText)
        DetailRow(label = stringResource(R.string.label_company), value = companyText)
        DetailRow(
            label = stringResource(R.string.label_last_updated),
            value = DateUtils.formatTimestamp(user.lastUpdated)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { onToggleFavorite(user.id, !user.isFavorite) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(
                    if (user.isFavorite) R.string.favorite_remove else R.string.favorite_add
                )
            )
        }
    }
}

@Composable
private fun DetailRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(top = 2.dp)
        )
        HorizontalDivider(modifier = Modifier.padding(top = 8.dp))
    }
}

@Preview(showBackground = true)
@Composable
private fun UserDetailContentPreview() {
    ModuleTestTheme {
        UserDetailContent(
            uiState = UserDetailViewModel.UiState.Success(
                User(
                    id = 1,
                    name = "Leanne Graham",
                    username = "Bret",
                    email = "leanne@example.com",
                    phone = "010-1234-5678",
                    website = "hildegard.org",
                    address = Address(
                        street = "Kulas Light",
                        suite = "Apt. 556",
                        city = "Gwenborough",
                        zipcode = "92998-3874"
                    ),
                    company = Company(
                        name = "Romaguera-Crona",
                        catchPhrase = "Multi-layered client-server neural-net"
                    ),
                    lastUpdated = 1_700_000_000_000L,
                    isFavorite = false
                )
            ),
            userName = "Leanne Graham",
            onToggleFavorite = { _, _ -> },
            onNavigateUp = {}
        )
    }
}
