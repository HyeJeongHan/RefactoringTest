package com.hjhan.moduletest.ui.main

import com.hjhan.moduletest.domain.repository.AuthRepository
import com.hjhan.moduletest.domain.usecase.GetFavoriteUsersUseCase
import com.hjhan.moduletest.domain.usecase.GetUsersUseCase
import com.hjhan.moduletest.domain.usecase.LogoutUseCase
import com.hjhan.moduletest.domain.usecase.RefreshUsersUseCase
import com.hjhan.moduletest.domain.usecase.SearchUsersUseCase
import com.hjhan.moduletest.domain.usecase.ToggleFavoriteUseCase
import com.hjhan.moduletest.model.User
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
class MainViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var getUsersUseCase: GetUsersUseCase
    private lateinit var authRepository: AuthRepository
    private lateinit var viewModel: MainViewModel

    private val testUsers = listOf(
        User(id = 1, name = "Alice Smith", username = "alice", email = "alice@example.com", phone = "010-1111-1111", website = null, address = null, company = null),
        User(id = 2, name = "Bob Jones", username = "bob", email = "bob@example.com", phone = "010-2222-2222", website = null, address = null, company = null),
        User(id = 3, name = "Charlie Brown", username = "charlie", email = "charlie@test.com", phone = null, website = null, address = null, company = null),
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        getUsersUseCase = mockk()
        authRepository = mockk(relaxed = true)
        every { authRepository.getUsername() } returns "testuser"
        every { authRepository.isLoggedIn() } returns true
        coEvery { getUsersUseCase() } returns testUsers
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(): MainViewModel {
        return MainViewModel(
            getUsersUseCase = getUsersUseCase,
            refreshUsersUseCase = mockk(relaxed = true),
            searchUsersUseCase = SearchUsersUseCase(),
            getFavoriteUsersUseCase = mockk(relaxed = true),
            toggleFavoriteUseCase = mockk(relaxed = true),
            logoutUseCase = mockk(relaxed = true),
            authRepository = authRepository
        )
    }

    @Test
    fun `search by name filters correctly`() = runTest {
        viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onIntent(MainViewModel.Intent.Search("Alice"))

        val state = viewModel.uiState.value as MainViewModel.UiState.Success
        assertEquals(1, state.users.size)
        assertEquals("Alice Smith", state.users[0].name)
    }

    @Test
    fun `search by email filters correctly`() = runTest {
        viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onIntent(MainViewModel.Intent.Search("example.com"))

        val state = viewModel.uiState.value as MainViewModel.UiState.Success
        assertEquals(2, state.users.size)
    }

    @Test
    fun `search by phone filters correctly`() = runTest {
        viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onIntent(MainViewModel.Intent.Search("010-1111"))

        val state = viewModel.uiState.value as MainViewModel.UiState.Success
        assertEquals(1, state.users.size)
        assertEquals("Alice Smith", state.users[0].name)
    }

    @Test
    fun `search is case insensitive`() = runTest {
        viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onIntent(MainViewModel.Intent.Search("alice"))

        val state = viewModel.uiState.value as MainViewModel.UiState.Success
        assertEquals(1, state.users.size)
    }

    @Test
    fun `empty search restores all users`() = runTest {
        viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onIntent(MainViewModel.Intent.Search("Alice"))
        viewModel.onIntent(MainViewModel.Intent.Search(""))

        val state = viewModel.uiState.value as MainViewModel.UiState.Success
        assertEquals(testUsers.size, state.users.size)
    }

    @Test
    fun `search with no match returns empty state`() = runTest {
        viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onIntent(MainViewModel.Intent.Search("zzz_no_match"))

        assertTrue(viewModel.uiState.value is MainViewModel.UiState.Empty)
    }

    @Test
    fun `load users failure shows error state`() = runTest {
        coEvery { getUsersUseCase() } throws RuntimeException("네트워크 오류")
        viewModel = createViewModel()
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value is MainViewModel.UiState.Error)
    }
}
