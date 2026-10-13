package com.example.jurnaltreding.ui.auth

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
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
fun RegisterScreen(
    viewModel: RegisterViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    RegisterContent(
        uiState = uiState,
        onSubmit = viewModel::register,
        onFieldEdited = viewModel::clearError,
        onNavigateBack = onNavigateBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RegisterContent(
    uiState: AuthUiState,
    onSubmit: (name: String, email: String, password: String, confirmPassword: String) -> Unit,
    onFieldEdited: (AuthField) -> Unit,
    onNavigateBack: () -> Unit
) {
    var name by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }
    val submit = { onSubmit(name, email, password, confirmPassword) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Daftar Akun") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Buat akun untuk mulai mencatat transaksi sahammu.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(20.dp))

            if (uiState.generalError != null) {
                ErrorBanner(message = uiState.generalError)
                Spacer(Modifier.height(16.dp))
            }

            AppTextField(
                value = name,
                onValueChange = {
                    name = it
                    onFieldEdited(AuthField.NAME)
                },
                label = "Nama",
                error = uiState.fieldErrors[AuthField.NAME],
                enabled = !uiState.isLoading,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Next
                )
            )
            Spacer(Modifier.height(8.dp))
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
                enabled = !uiState.isLoading
            )
            Spacer(Modifier.height(8.dp))
            PasswordField(
                value = confirmPassword,
                onValueChange = {
                    confirmPassword = it
                    onFieldEdited(AuthField.CONFIRM_PASSWORD)
                },
                label = "Konfirmasi password",
                error = uiState.fieldErrors[AuthField.CONFIRM_PASSWORD],
                enabled = !uiState.isLoading,
                imeAction = ImeAction.Done,
                keyboardActions = KeyboardActions(onDone = { submit() })
            )
            Spacer(Modifier.height(16.dp))
            LoadingButton(
                text = "Daftar",
                onClick = submit,
                isLoading = uiState.isLoading
            )
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Sudah punya akun?", style = MaterialTheme.typography.bodyMedium)
                TextButton(onClick = onNavigateBack, enabled = !uiState.isLoading) {
                    Text("Masuk")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun RegisterPreview() {
    JurnalTredingTheme {
        RegisterContent(AuthUiState(), { _, _, _, _ -> }, {}, {})
    }
}

@Preview(showBackground = true, name = "Loading")
@Composable
private fun RegisterLoadingPreview() {
    JurnalTredingTheme {
        RegisterContent(AuthUiState(isLoading = true), { _, _, _, _ -> }, {}, {})
    }
}

@Preview(showBackground = true, name = "Error")
@Composable
private fun RegisterErrorPreview() {
    JurnalTredingTheme {
        RegisterContent(
            uiState = AuthUiState(
                fieldErrors = mapOf(
                    AuthField.NAME to "Nama wajib diisi",
                    AuthField.EMAIL to "Email sudah terdaftar",
                    AuthField.PASSWORD to "Password minimal 6 karakter",
                    AuthField.CONFIRM_PASSWORD to "Konfirmasi password tidak sama"
                )
            ),
            onSubmit = { _, _, _, _ -> },
            onFieldEdited = {},
            onNavigateBack = {}
        )
    }
}
