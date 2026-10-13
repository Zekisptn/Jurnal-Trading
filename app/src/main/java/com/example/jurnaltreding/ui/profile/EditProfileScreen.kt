package com.example.jurnaltreding.ui.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.PersonOff
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.jurnaltreding.ui.components.AppTextField
import com.example.jurnaltreding.ui.components.EmptyState
import com.example.jurnaltreding.ui.components.ErrorBanner
import com.example.jurnaltreding.ui.components.ErrorState
import com.example.jurnaltreding.ui.components.LoadingButton
import com.example.jurnaltreding.ui.components.LoadingState
import com.example.jurnaltreding.ui.theme.JurnalTredingTheme

@Composable
fun EditProfileScreen(
    viewModel: EditProfileViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState) {
        if ((uiState as? EditProfileUiState.Editing)?.isSaved == true) onNavigateBack()
    }

    EditProfileContent(
        uiState = uiState,
        onSave = viewModel::save,
        onNameEdited = viewModel::clearNameError,
        onRetry = viewModel::load,
        onNavigateBack = onNavigateBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditProfileContent(
    uiState: EditProfileUiState,
    onSave: (name: String, investingStyle: String) -> Unit,
    onNameEdited: () -> Unit,
    onRetry: () -> Unit,
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Profil") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { innerPadding ->
        val contentModifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .consumeWindowInsets(innerPadding)
        when (uiState) {
            EditProfileUiState.Loading -> LoadingState(contentModifier)
            is EditProfileUiState.Error -> ErrorState(
                message = uiState.message,
                modifier = contentModifier,
                onRetry = onRetry
            )
            EditProfileUiState.NotFound -> EmptyState(
                title = "Data profil tidak tersedia",
                message = "Tidak ada data akun yang bisa diedit.",
                modifier = contentModifier,
                icon = Icons.Outlined.PersonOff,
                actionLabel = "Kembali",
                onAction = onNavigateBack
            )
            is EditProfileUiState.Editing -> EditProfileFields(
                state = uiState,
                onSave = onSave,
                onNameEdited = onNameEdited,
                modifier = contentModifier
            )
        }
    }
}

@Composable
private fun EditProfileFields(
    state: EditProfileUiState.Editing,
    onSave: (name: String, investingStyle: String) -> Unit,
    onNameEdited: () -> Unit,
    modifier: Modifier = Modifier
) {
    var name by rememberSaveable { mutableStateOf(state.initialName) }
    var investingStyle by rememberSaveable { mutableStateOf(state.initialInvestingStyle) }
    val enabled = !state.isSaving

    Column(
        modifier = modifier
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        AppTextField(
            value = name,
            onValueChange = {
                name = it
                onNameEdited()
            },
            label = "Nama",
            error = state.nameError,
            enabled = enabled,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                imeAction = ImeAction.Next
            )
        )
        AppTextField(
            value = state.email,
            onValueChange = {},
            label = "Email",
            supportingText = "Email tidak bisa diubah",
            enabled = false
        )
        AppTextField(
            value = investingStyle,
            onValueChange = { investingStyle = it },
            label = "Gaya investasi (opsional)",
            placeholder = "mis. Jangka panjang, swing trading",
            enabled = enabled,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences,
                imeAction = ImeAction.Done
            )
        )

        if (state.saveError != null) {
            ErrorBanner(message = state.saveError)
        }
        LoadingButton(
            text = "Simpan",
            onClick = { onSave(name, investingStyle) },
            modifier = Modifier.padding(top = 8.dp),
            isLoading = state.isSaving
        )
    }
}

@Composable
private fun PreviewEditProfile(uiState: EditProfileUiState) {
    JurnalTredingTheme {
        EditProfileContent(uiState, { _, _ -> }, {}, {}, {})
    }
}

@Preview(showBackground = true)
@Composable
private fun EditProfilePreview() {
    PreviewEditProfile(
        EditProfileUiState.Editing(
            email = "demo@jurnal.id",
            initialName = "Investor Demo",
            initialInvestingStyle = "Swing trading"
        )
    )
}

@Preview(showBackground = true, name = "Error validasi")
@Composable
private fun EditProfileValidationPreview() {
    PreviewEditProfile(
        EditProfileUiState.Editing(
            email = "demo@jurnal.id",
            initialName = "",
            initialInvestingStyle = "",
            nameError = "Nama wajib diisi",
            saveError = "Gagal menyimpan profil. Coba lagi."
        )
    )
}

@Preview(showBackground = true, name = "Kosong")
@Composable
private fun EditProfileNotFoundPreview() {
    PreviewEditProfile(EditProfileUiState.NotFound)
}

@Preview(showBackground = true, name = "Loading")
@Composable
private fun EditProfileLoadingPreview() {
    PreviewEditProfile(EditProfileUiState.Loading)
}

@Preview(showBackground = true, name = "Error")
@Composable
private fun EditProfileErrorPreview() {
    PreviewEditProfile(EditProfileUiState.Error("Gagal memuat profil."))
}
