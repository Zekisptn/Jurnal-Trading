package com.example.jurnaltreding.ui.journal

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.jurnaltreding.data.local.Emotion
import com.example.jurnaltreding.data.repository.fake.sampleEntries
import com.example.jurnaltreding.ui.components.AppTextField
import com.example.jurnaltreding.ui.components.DateField
import com.example.jurnaltreding.ui.components.EmptyState
import com.example.jurnaltreding.ui.components.ErrorBanner
import com.example.jurnaltreding.ui.components.ErrorState
import com.example.jurnaltreding.ui.components.LoadingButton
import com.example.jurnaltreding.ui.components.LoadingState
import com.example.jurnaltreding.ui.theme.JurnalTredingTheme
import com.example.jurnaltreding.util.JournalField
import com.example.jurnaltreding.util.JournalFormInput
import com.example.jurnaltreding.util.toFormInput

private const val MAX_PRICE_DIGITS = 9
private const val MAX_LOT_DIGITS = 6
private const val STOCK_CODE_LENGTH = 4

@Composable
fun JournalFormScreen(
    viewModel: JournalFormViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState) {
        if ((uiState as? JournalFormUiState.Editing)?.isSaved == true) onNavigateBack()
    }

    JournalFormContent(
        uiState = uiState,
        onSave = viewModel::save,
        onFieldEdited = viewModel::clearError,
        onRetry = viewModel::load,
        onNavigateBack = onNavigateBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun JournalFormContent(
    uiState: JournalFormUiState,
    onSave: (JournalFormInput) -> Unit,
    onFieldEdited: (JournalField) -> Unit,
    onRetry: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val isEditMode = (uiState as? JournalFormUiState.Editing)?.isEditMode ?: true

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditMode) "Edit Catatan" else "Tambah Catatan") },
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
            JournalFormUiState.Loading -> LoadingState(contentModifier)
            is JournalFormUiState.Error -> ErrorState(
                message = uiState.message,
                modifier = contentModifier,
                onRetry = onRetry
            )
            JournalFormUiState.NotFound -> EmptyState(
                title = "Catatan tidak ditemukan",
                message = "Catatan yang ingin diedit mungkin sudah dihapus.",
                modifier = contentModifier,
                actionLabel = "Kembali",
                onAction = onNavigateBack
            )
            is JournalFormUiState.Editing -> JournalFormFields(
                state = uiState,
                onSave = onSave,
                onFieldEdited = onFieldEdited,
                modifier = contentModifier
            )
        }
    }
}

@Composable
private fun JournalFormFields(
    state: JournalFormUiState.Editing,
    onSave: (JournalFormInput) -> Unit,
    onFieldEdited: (JournalField) -> Unit,
    modifier: Modifier = Modifier
) {
    val initial = state.initialInput
    var stockCode by rememberSaveable { mutableStateOf(initial.stockCode) }
    var buyDate by rememberSaveable { mutableStateOf(initial.buyDate) }
    var buyPrice by rememberSaveable { mutableStateOf(initial.buyPrice) }
    var lots by rememberSaveable { mutableStateOf(initial.lots) }
    var reason by rememberSaveable { mutableStateOf(initial.reason) }
    var emotion by rememberSaveable { mutableStateOf(initial.emotion) }
    var isSold by rememberSaveable { mutableStateOf(initial.isSold) }
    var sellDate by rememberSaveable { mutableStateOf(initial.sellDate) }
    var sellPrice by rememberSaveable { mutableStateOf(initial.sellPrice) }
    var evaluation by rememberSaveable { mutableStateOf(initial.evaluation) }

    val errors = state.fieldErrors
    val enabled = !state.isSaving
    val numberKeyboard = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next)

    Column(
        modifier = modifier
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SectionTitle("Pembelian")
        AppTextField(
            value = stockCode,
            onValueChange = { text ->
                stockCode = text.filter { it.isLetter() }.uppercase().take(STOCK_CODE_LENGTH)
                onFieldEdited(JournalField.STOCK_CODE)
            },
            label = "Kode saham",
            placeholder = "mis. BBCA",
            error = errors[JournalField.STOCK_CODE],
            enabled = enabled,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Characters,
                imeAction = ImeAction.Next
            )
        )
        DateField(
            label = "Tanggal beli",
            date = buyDate,
            onDateSelected = {
                buyDate = it
                onFieldEdited(JournalField.BUY_DATE)
                onFieldEdited(JournalField.SELL_DATE)
            },
            error = errors[JournalField.BUY_DATE],
            enabled = enabled
        )
        AppTextField(
            value = buyPrice,
            onValueChange = { text ->
                buyPrice = text.filter { it.isDigit() }.take(MAX_PRICE_DIGITS)
                onFieldEdited(JournalField.BUY_PRICE)
            },
            label = "Harga beli per lembar",
            prefix = "Rp",
            error = errors[JournalField.BUY_PRICE],
            enabled = enabled,
            keyboardOptions = numberKeyboard
        )
        AppTextField(
            value = lots,
            onValueChange = { text ->
                lots = text.filter { it.isDigit() }.take(MAX_LOT_DIGITS)
                onFieldEdited(JournalField.LOTS)
            },
            label = "Jumlah lot",
            supportingText = "1 lot = 100 lembar",
            error = errors[JournalField.LOTS],
            enabled = enabled,
            keyboardOptions = numberKeyboard
        )
        AppTextField(
            value = reason,
            onValueChange = {
                reason = it
                onFieldEdited(JournalField.REASON)
            },
            label = "Alasan membeli",
            placeholder = "Kenapa kamu membeli saham ini?",
            error = errors[JournalField.REASON],
            enabled = enabled,
            singleLine = false,
            minLines = 3,
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
        )

        Text("Emosi saat membeli", style = MaterialTheme.typography.bodyMedium)
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Emotion.entries.forEach { option ->
                FilterChip(
                    selected = emotion == option,
                    onClick = {
                        emotion = option
                        onFieldEdited(JournalField.EMOTION)
                    },
                    label = { Text(option.label) },
                    enabled = enabled
                )
            }
        }
        errors[JournalField.EMOTION]?.let { message ->
            Text(
                text = message,
                modifier = Modifier.padding(start = 16.dp),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error
            )
        }

        HorizontalDivider(Modifier.padding(vertical = 8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                SectionTitle("Penjualan")
                Text(
                    text = if (isSold) "Posisi Closed" else "Posisi masih Open",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text("Sudah dijual", style = MaterialTheme.typography.bodyMedium)
            Switch(
                checked = isSold,
                onCheckedChange = {
                    isSold = it
                    onFieldEdited(JournalField.SELL_DATE)
                    onFieldEdited(JournalField.SELL_PRICE)
                },
                modifier = Modifier.padding(start = 12.dp),
                enabled = enabled
            )
        }
        if (isSold) {
            DateField(
                label = "Tanggal jual",
                date = sellDate,
                onDateSelected = {
                    sellDate = it
                    onFieldEdited(JournalField.SELL_DATE)
                },
                error = errors[JournalField.SELL_DATE],
                enabled = enabled
            )
            AppTextField(
                value = sellPrice,
                onValueChange = { text ->
                    sellPrice = text.filter { it.isDigit() }.take(MAX_PRICE_DIGITS)
                    onFieldEdited(JournalField.SELL_PRICE)
                },
                label = "Harga jual per lembar",
                prefix = "Rp",
                error = errors[JournalField.SELL_PRICE],
                enabled = enabled,
                keyboardOptions = numberKeyboard
            )
        }
        AppTextField(
            value = evaluation,
            onValueChange = { evaluation = it },
            label = "Evaluasi (opsional)",
            placeholder = "Apa yang bisa dipelajari dari transaksi ini?",
            enabled = enabled,
            singleLine = false,
            minLines = 3,
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
        )

        if (state.saveError != null) {
            ErrorBanner(message = state.saveError)
        }
        LoadingButton(
            text = "Simpan",
            onClick = {
                onSave(
                    JournalFormInput(
                        stockCode = stockCode,
                        buyDate = buyDate,
                        buyPrice = buyPrice,
                        lots = lots,
                        reason = reason,
                        emotion = emotion,
                        isSold = isSold,
                        sellDate = sellDate,
                        sellPrice = sellPrice,
                        evaluation = evaluation
                    )
                )
            },
            modifier = Modifier.padding(top = 8.dp),
            isLoading = state.isSaving
        )
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary
    )
}

@Composable
private fun PreviewForm(uiState: JournalFormUiState) {
    JurnalTredingTheme {
        JournalFormContent(uiState, {}, {}, {}, {})
    }
}

@Preview(showBackground = true, heightDp = 1000, name = "Tambah (form kosong)")
@Composable
private fun JournalFormAddPreview() {
    PreviewForm(JournalFormUiState.Editing(isEditMode = false, initialInput = JournalFormInput()))
}

@Preview(showBackground = true, heightDp = 1100, name = "Edit")
@Composable
private fun JournalFormEditPreview() {
    PreviewForm(
        JournalFormUiState.Editing(isEditMode = true, initialInput = sampleEntries()[0].toFormInput())
    )
}

@Preview(showBackground = true, heightDp = 1200, name = "Error validasi")
@Composable
private fun JournalFormValidationPreview() {
    PreviewForm(
        JournalFormUiState.Editing(
            isEditMode = false,
            initialInput = JournalFormInput(isSold = true),
            fieldErrors = mapOf(
                JournalField.STOCK_CODE to "Kode saham wajib diisi",
                JournalField.BUY_DATE to "Tanggal beli wajib diisi",
                JournalField.BUY_PRICE to "Harga beli harus lebih dari 0",
                JournalField.LOTS to "Jumlah lot minimal 1",
                JournalField.REASON to "Alasan membeli wajib diisi",
                JournalField.EMOTION to "Pilih emosi saat membeli",
                JournalField.SELL_DATE to "Tanggal jual tidak boleh sebelum tanggal beli",
                JournalField.SELL_PRICE to "Harga jual wajib diisi"
            ),
            saveError = "Gagal menyimpan catatan. Coba lagi."
        )
    )
}

@Preview(showBackground = true, name = "Kosong")
@Composable
private fun JournalFormNotFoundPreview() {
    PreviewForm(JournalFormUiState.NotFound)
}

@Preview(showBackground = true, name = "Loading")
@Composable
private fun JournalFormLoadingPreview() {
    PreviewForm(JournalFormUiState.Loading)
}

@Preview(showBackground = true, name = "Error")
@Composable
private fun JournalFormErrorPreview() {
    PreviewForm(JournalFormUiState.Error("Gagal memuat catatan."))
}
