package com.example.glamora.fragmentSignup.viewModel

import app.cash.turbine.test
import com.example.glamora.data.repository.RepositoryImpl
import com.example.glamora.util.State
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock


class SignUpViewModelTest {

    private lateinit var signUpViewModel: SignUpViewModel
    private lateinit var repository: RepositoryImpl

    @Before
    fun setUp() {
        repository = mock()
        signUpViewModel = SignUpViewModel(repository)
    }

    @Test
    fun `validateAndSignUp with invalid inputs returns error`() = runTest {
        signUpViewModel.signUpState.test {
            signUpViewModel.validateAndSignUp(
                "",
                "",
                "",
                "",
                ""
            )
            assertTrue(awaitItem() is State.Error)
        }
    }

    @Test
    fun `validateAndSignUp with valid inputs returns success`() = runTest {
        signUpViewModel.signUpState.test {
            signUpViewModel.validateAndSignUp(
                "John Doe",
                "test@gmail.com",
                "12345678",
                "12345678",
                "1234567890"
            )
            assertTrue(awaitItem() is State.Success)
        }
    }

}