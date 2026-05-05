package com.hjhan.moduletest.ui.login

import com.hjhan.moduletest.util.Constants
import com.hjhan.moduletest.util.SharedPrefsManager
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
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
    private lateinit var sharedPrefs: SharedPrefsManager
    private lateinit var viewModel: LoginViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        sharedPrefs = mockk(relaxed = true)
        every { sharedPrefs.isLoggedIn() } returns false
        viewModel = LoginViewModel(sharedPrefs)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `login success with correct credentials`() = runTest {
        viewModel.onIntent(LoginViewModel.Intent.Login(Constants.TEST_USERNAME, Constants.TEST_PASSWORD))
        advanceUntilIdle()

        assertEquals(LoginViewModel.UiState.Success, viewModel.uiState.value)
        verify { sharedPrefs.setLoggedIn(true) }
        verify { sharedPrefs.saveUsername(Constants.TEST_USERNAME) }
    }

    @Test
    fun `login failure with wrong password`() = runTest {
        viewModel.onIntent(LoginViewModel.Intent.Login(Constants.TEST_USERNAME, "wrongpassword"))
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value is LoginViewModel.UiState.Error)
    }

    @Test
    fun `login failure with wrong username`() = runTest {
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
        viewModel.onIntent(LoginViewModel.Intent.Login(Constants.TEST_USERNAME, "wrongpassword"))
        advanceUntilIdle()

        viewModel.onIntent(LoginViewModel.Intent.ResetState)

        assertEquals(LoginViewModel.UiState.Idle, viewModel.uiState.value)
    }
}
