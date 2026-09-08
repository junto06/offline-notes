package com.mudassar.notes.auth.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mudassar.notes.auth.presentation.R
import com.mudassar.notes.base.BaseFragment
import com.mudassar.notes.design.AppHeader
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FragmentLogin : BaseFragment() {
    private val viewModel: LoginViewModel by viewModels()

    @Composable
    override fun UiContent() {
        val state by viewModel.state.collectAsStateWithLifecycle()
        LoginContent(
            state = state,
            onEmailChanged = viewModel::onEmailChanged,
            onPasswordChanged = viewModel::onPasswordChanged,
            onLoginClicked = viewModel::onLoginClicked,
            onBackClicked = viewModel::onBackClicked,
        )
    }
}

@Composable
private fun LoginContent(
    state: LoginUiState,
    onEmailChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onLoginClicked: () -> Unit,
    onBackClicked: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        AppHeader(stringResource(R.string.login_title), onBack = onBackClicked)

        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
        ) {

            OutlinedTextField(
                value = state.email,
                onValueChange = onEmailChanged,
                label = { Text(stringResource(R.string.login_email_label)) },
                singleLine = true,
                enabled = !state.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp),
            )

            OutlinedTextField(
                value = state.password,
                onValueChange = onPasswordChanged,
                label = { Text(stringResource(R.string.login_password_label)) },
                singleLine = true,
                enabled = !state.isLoading,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
            )

            if (state.error != null) {
                Text(
                    text = state.error.toMessage(),
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }

            Button(
                onClick = onLoginClicked,
                enabled = !state.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp),
            ) {
                Text(stringResource(if (state.isLoading) R.string.login_button_loading else R.string.login_button))
            }
        }
    }
}

@Composable
private fun ValidationError.toMessage(): String = when (this) {
    ValidationError.EmptyFields -> stringResource(R.string.login_error_empty_fields)
    ValidationError.LoginFailed -> stringResource(R.string.login_error_failed)
}
