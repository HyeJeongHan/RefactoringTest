package com.hjhan.moduletest.ui.login

import com.hjhan.moduletest.domain.usecase.IsLoggedInUseCase
import com.hjhan.moduletest.domain.usecase.LoginUseCase
import com.hjhan.moduletest.util.Constants
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var loginUseCase: LoginUseCase
    private lateinit var isLoggedInUseCase: IsLoggedInUseCase
    private lateinit var viewModel: LoginViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        loginUseCase = mockk()
        isLoggedInUseCase = mockk()
        every { isLoggedInUseCase() } returns false
        viewModel = LoginViewModel(loginUseCase, isLoggedInUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `login success with correct credentials`() = runTest {
        coEvery { loginUseCase(Constants.TEST_USERNAME, Constants.TEST_PASSWORD) } returns true

        viewModel.onIntent(LoginViewModel.Intent.Login(Constants.TEST_USERNAME, Constants.TEST_PASSWORD))
        advanceUntilIdle()

        assertEquals(LoginViewModel.UiState.Success, viewModel.uiState.value)
    }

    @Test
    fun `login failure with wrong password`() = runTest {
        coEvery { loginUseCase(any(), any()) } returns false

        viewModel.onIntent(LoginViewModel.Intent.Login(Constants.TEST_USERNAME, "wrongpassword"))
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value is LoginViewModel.UiState.Error)
    }

    @Test
    fun `login failure with wrong username`() = runTest {
        coEvery { loginUseCase(any(), any()) } returns false

        viewModel.onIntent(LoginViewModel.Intent.Login("wronguser", Constants.TEST_PASSWORD))
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value is LoginViewModel.UiState.Error)
    }

    @Test
    fun `login ignored when username is blank`() = runTest {
        viewModel.onIntent(LoginViewModel.Intent.Login("", Constants.TEST_PASSWORD))
        advanceUntilIdle()

        assertEquals(LoginViewModel.UiState.Idle, viewModel.uiState.value)
    }

    @Test
    fun `login ignored when password too short`() = runTest {
        viewModel.onIntent(LoginViewModel.Intent.Login(Constants.TEST_USERNAME, "123"))
        advanceUntilIdle()

        assertEquals(LoginViewModel.UiState.Idle, viewModel.uiState.value)
    }

    @Test
    fun `reset state returns to idle`() = runTest {
        coEvery { loginUseCase(any(), any()) } returns false

        viewModel.onIntent(LoginViewModel.Intent.Login(Constants.TEST_USERNAME, "wrongpassword"))
        advanceUntilIdle()

        viewModel.onIntent(LoginViewModel.Intent.ResetState)

        assertEquals(LoginViewModel.UiState.Idle, viewModel.uiState.value)
    }
}
