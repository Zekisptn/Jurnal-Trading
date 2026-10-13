package com.example.jurnaltreding.ui.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.jurnaltreding.ui.components.AppTextField
import com.example.jurnaltreding.ui.components.ErrorBanner
import com.example.jurnaltreding.ui.components.LoadingButton
import com.example.jurnaltreding.ui.components.PasswordField
import com.example.jurnaltreding.ui.theme.JurnalTredingTheme

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onNavigateToRegister: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    LoginContent(
        uiState = uiState,
        onSubmit = viewModel::login,
        onFieldEdited = viewModel::clearError,
        onNavigateToRegister = onNavigateToRegister
    )
}

@Composable
private fun LoginContent(
    uiState: AuthUiState,
    onSubmit: (email: String, password: String) -> Unit,
    onFieldEdited: (AuthField) -> Unit,
    onNavigateToRegister: () -> Unit
) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ShowChart,
                contentDescription = null,
                modifier = Modifier.size(56.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(12.dp))
            Text("Jurnal Trading", style = MaterialTheme.typography.headlineMedium)
            Text(
                text = "Buku harian investor saham",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(32.dp))

            if (uiState.generalError != null) {
                ErrorBanner(message = uiState.generalError)
                Spacer(Modifier.height(16.dp))
            }

            AppTextField(
                value = email,
                onValueChange = {
                    email = it
                    onFieldEdited(AuthField.EMAIL)
                },
                label = "Email",
                error = uiState.fieldErrors[AuthField.EMAIL],
                enabled = !uiState.isLoading,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                )
            )
            Spacer(Modifier.height(8.dp))
            PasswordField(
                value = password,
                onValueChange = {
                    password = it
                    onFieldEdited(AuthField.PASSWORD)
                },
                label = "Password",
                error = uiState.fieldErrors[AuthField.PASSWORD],
                enabled = !uiState.isLoading,
                imeAction = ImeAction.Done,
                keyboardActions = KeyboardActions(onDone = { onSubmit(email, password) })
            )
            Spacer(Modifier.height(16.dp))
            LoadingButton(
                text = "Masuk",
                onClick = { onSubmit(email, password) },
                isLoading = uiState.isLoading
            )
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Belum punya akun?", style = MaterialTheme.typography.bodyMedium)
                TextButton(onClick = onNavigateToRegister, enabled = !uiState.isLoading) {
                    Text("Daftar")
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Form kosong")
@Composable
private fun LoginPreview() {
    JurnalTredingTheme {
        LoginContent(AuthUiState(), { _, _ -> }, {}, {})
    }
}

@Preview(showBackground = true, name = "Loading")
@Composable
private fun LoginLoadingPreview() {
    JurnalTredingTheme {
        LoginContent(AuthUiState(isLoading = true), { _, _ -> }, {}, {})
    }
}

@Preview(showBackground = true, name = "Error")
@Composable
private fun LoginErrorPreview() {
    JurnalTredingTheme {
        LoginContent(
            uiState = AuthUiState(
                fieldErrors = mapOf(
                    AuthField.EMAIL to "Format email tidak valid",
                    AuthField.PASSWORD to "Password wajib diisi"
                ),
                generalError = "Email atau password salah"
            ),
            onSubmit = { _, _ -> },
            onFieldEdited = {},
            onNavigateToRegister = {}
        )
    }
}
