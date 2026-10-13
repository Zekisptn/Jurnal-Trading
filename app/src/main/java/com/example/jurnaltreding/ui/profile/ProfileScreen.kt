package com.example.jurnaltreding.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.PersonOff
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.jurnaltreding.data.local.User
import com.example.jurnaltreding.ui.components.ConfirmDialog
import com.example.jurnaltreding.ui.components.EmptyState
import com.example.jurnaltreding.ui.components.ErrorState
import com.example.jurnaltreding.ui.components.LoadingState
import com.example.jurnaltreding.ui.theme.JurnalTredingTheme
import com.example.jurnaltreding.util.formatDate

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onEditProfile: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    ProfileContent(
        uiState = uiState,
        onEditProfile = onEditProfile,
        onLogout = viewModel::logout,
        onRetry = viewModel::load
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileContent(
    uiState: ProfileUiState,
    onEditProfile: () -> Unit,
    onLogout: () -> Unit,
    onRetry: () -> Unit
) {
    var showLogoutDialog by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Profil") }) }
    ) { innerPadding ->
        val contentModifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
        when (uiState) {
            ProfileUiState.Loading -> LoadingState(contentModifier)
            is ProfileUiState.Error -> ErrorState(
                message = uiState.message,
                modifier = contentModifier,
                onRetry = onRetry
            )
            ProfileUiState.NotFound -> EmptyState(
                title = "Data profil tidak tersedia",
                message = "Keluar lalu masuk kembali untuk memuat ulang akunmu.",
                modifier = contentModifier,
                icon = Icons.Outlined.PersonOff,
                actionLabel = "Keluar",
                onAction = onLogout
            )
            is ProfileUiState.Success -> ProfileDetail(
                user = uiState.user,
                isLoggingOut = uiState.isLoggingOut,
                onEditProfile = onEditProfile,
                onLogoutClick = { showLogoutDialog = true },
                modifier = contentModifier
            )
        }
    }

    if (showLogoutDialog) {
        ConfirmDialog(
            title = "Keluar dari akun?",
            message = "Kamu perlu masuk lagi untuk melihat jurnalmu.",
            confirmLabel = "Keluar",
            isDestructive = true,
            onConfirm = {
                showLogoutDialog = false
                onLogout()
            },
            onDismiss = { showLogoutDialog = false }
        )
    }
}

@Composable
private fun ProfileDetail(
    user: User,
    isLoggingOut: Boolean,
    onEditProfile: () -> Unit,
    onLogoutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(88.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = user.name.trim().take(1).uppercase(),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
        Spacer(Modifier.height(12.dp))
        Text(text = user.name, style = MaterialTheme.typography.titleLarge)
        Text(
            text = user.email,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ProfileField(label = "Nama", value = user.name)
                HorizontalDivider()
                ProfileField(label = "Email", value = user.email)
                HorizontalDivider()
                ProfileField(label = "Gaya investasi", value = user.investingStyle ?: "Belum diisi")
                HorizontalDivider()
                ProfileField(label = "Bergabung sejak", value = formatDate(user.createdAt))
            }
        }
        Spacer(Modifier.height(24.dp))

        FilledTonalButton(
            onClick = onEditProfile,
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoggingOut
        ) {
            Icon(Icons.Filled.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
            Text("Edit Profil", modifier = Modifier.padding(start = 8.dp))
        }
        Spacer(Modifier.height(8.dp))
        OutlinedButton(
            onClick = onLogoutClick,
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoggingOut,
            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
        ) {
            Icon(
                Icons.AutoMirrored.Filled.Logout,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Text("Keluar", modifier = Modifier.padding(start = 8.dp))
        }
    }
}

@Composable
private fun ProfileField(label: String, value: String) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(text = value, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
private fun PreviewProfile(uiState: ProfileUiState) {
    JurnalTredingTheme {
        ProfileContent(uiState, {}, {}, {})
    }
}

@Preview(showBackground = true)
@Composable
private fun ProfilePreview() {
    PreviewProfile(
        ProfileUiState.Success(
            User(
                id = 1,
                name = "Investor Demo",
                email = "demo@jurnal.id",
                passwordHash = "",
                salt = "",
                investingStyle = "Swing trading"
            )
        )
    )
}

@Preview(showBackground = true, name = "Kosong")
@Composable
private fun ProfileNotFoundPreview() {
    PreviewProfile(ProfileUiState.NotFound)
}

@Preview(showBackground = true, name = "Loading")
@Composable
private fun ProfileLoadingPreview() {
    PreviewProfile(ProfileUiState.Loading)
}

@Preview(showBackground = true, name = "Error")
@Composable
private fun ProfileErrorPreview() {
    PreviewProfile(ProfileUiState.Error("Gagal memuat profil."))
}
