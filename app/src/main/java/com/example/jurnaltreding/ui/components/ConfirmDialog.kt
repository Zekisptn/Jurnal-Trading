package com.example.jurnaltreding.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.jurnaltreding.ui.theme.JurnalTredingTheme

/** Dialog konfirmasi untuk aksi penting seperti hapus catatan dan logout. */
@Composable
fun ConfirmDialog(
    title: String,
    message: String,
    confirmLabel: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    dismissLabel: String = "Batal",
    isDestructive: Boolean = false
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { Text(message) },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                colors = if (isDestructive) {
                    ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                } else {
                    ButtonDefaults.textButtonColors()
                }
            ) {
                Text(confirmLabel)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(dismissLabel) }
        }
    )
}

@Preview
@Composable
private fun ConfirmDialogPreview() {
    JurnalTredingTheme {
        ConfirmDialog(
            title = "Hapus catatan?",
            message = "Catatan BBCA akan dihapus permanen dan tidak bisa dikembalikan.",
            confirmLabel = "Hapus",
            onConfirm = {},
            onDismiss = {},
            isDestructive = true
        )
    }
}
