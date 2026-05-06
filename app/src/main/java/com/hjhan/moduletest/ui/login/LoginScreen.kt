package com.hjhan.moduletest.ui.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hjhan.moduletest.R
import com.hjhan.moduletest.ui.theme.ModuleTestTheme

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onLoginSuccess: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState) {
        if (uiState is LoginViewModel.UiState.Success) {
            onLoginSuccess()
        }
    }

    LoginContent(
        uiState = uiState,
        onLogin = { username, password ->
            viewModel.onIntent(LoginViewModel.Intent.Login(username, password))
        },
        onResetError = {
            viewModel.onIntent(LoginViewModel.Intent.ResetState)
        }
    )
}

@Composable
fun LoginContent(
    uiState: LoginViewModel.UiState,
    onLogin: (String, String) -> Unit,
    onResetError: () -> Unit,
    modifier: Modifier = Modifier
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var usernameError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }

    val isLoading = uiState is LoginViewModel.UiState.Loading
    val serverError = (uiState as? LoginViewModel.UiState.Error)?.message

    val errorUsernameEmpty = stringResource(R.string.error_username_empty)
    val errorPasswordEmpty = stringResource(R.string.error_password_empty)
    val errorPasswordLength = stringResource(R.string.error_password_length)

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = username,
                onValueChange = {
                    username = it
                    usernameError = null
                    if (uiState is LoginViewModel.UiState.Error) onResetError()
                },
                label = { Text(stringResource(R.string.hint_username)) },
                isError = usernameError != null,
                supportingText = usernameError?.let { msg -> { Text(msg) } },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                singleLine = true,
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    passwordError = null
                    if (uiState is LoginViewModel.UiState.Error) onResetError()
                },
                label = { Text(stringResource(R.string.hint_password)) },
                isError = passwordError != null,
                supportingText = passwordError?.let { msg -> { Text(msg) } },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                singleLine = true,
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth()
            )

            if (serverError != null) {
                Text(
                    text = serverError,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Button(
                onClick = {
                    var valid = true
                    if (username.isBlank()) {
                        usernameError = errorUsernameEmpty
                        valid = false
                    }
                    if (password.isBlank()) {
                        passwordError = errorPasswordEmpty
                        valid = false
                    } else if (password.length < 4) {
                        passwordError = errorPasswordLength
                        valid = false
                    }
                    if (valid) onLogin(username, password)
                },
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(stringResource(R.string.btn_login))
                }
            }

            Text(
                text = stringResource(R.string.login_hint),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LoginContentPreview() {
    ModuleTestTheme {
        LoginContent(
            uiState = LoginViewModel.UiState.Idle,
            onLogin = { _, _ -> },
            onResetError = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LoginContentLoadingPreview() {
    ModuleTestTheme {
        LoginContent(
            uiState = LoginViewModel.UiState.Loading,
            onLogin = { _, _ -> },
            onResetError = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LoginContentErrorPreview() {
    ModuleTestTheme {
        LoginContent(
            uiState = LoginViewModel.UiState.Error("아이디 또는 비밀번호가 올바르지 않습니다"),
            onLogin = { _, _ -> },
            onResetError = {}
        )
    }
}