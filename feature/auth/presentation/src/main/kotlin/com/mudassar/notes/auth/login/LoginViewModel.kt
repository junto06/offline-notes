package com.mudassar.notes.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mudassar.notes.models.DomainResult
import com.mudassar.notes.navigation.Navigator
import com.mudassar.notes.navigation.openDeeplink
import com.mudassar.notes.navigation.popBackStack
import com.mudassar.notes.usecases.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val navigator: Navigator,
) : ViewModel() {

    private val _state = MutableStateFlow(LoginUiState())
    val state: StateFlow<LoginUiState> = _state.asStateFlow()

    fun onEmailChanged(email: String) {
        _state.update { it.copy(email = email, error = null) }
    }

    fun onPasswordChanged(password: String) {
        _state.update { it.copy(password = password, error = null) }
    }

    fun onBackClicked() {
        navigator.popBackStack()
    }

    fun onLoginClicked() {
        val current = _state.value
        if (current.email.isBlank() || current.password.isBlank()) {
            _state.update { it.copy(error = ValidationError.EmptyFields) }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            when (loginUseCase(current.email, current.password)) {
                is DomainResult.Success -> navigator.openDeeplink("notes://list")
                is DomainResult.Error -> _state.update {
                    it.copy(
                        isLoading = false,
                        error = ValidationError.LoginFailed
                    )
                }
            }
        }
    }
}

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: ValidationError? = null,
)

sealed interface ValidationError {
    data object EmptyFields : ValidationError
    data object LoginFailed : ValidationError
}