package com.example.jurnaltreding.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import com.example.jurnaltreding.util.formatDate
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.util.Locale

private val localeId: Locale = Locale.forLanguageTag("id-ID")

// DatePicker Material 3 memakai epoch millis UTC.
private fun LocalDate.toUtcMillis(): Long = atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()

private fun Long.toLocalDate(): LocalDate = Instant.ofEpochMilli(this).atZone(ZoneOffset.UTC).toLocalDate()

/** Field tanggal yang membuka date picker saat disentuh. Tanggal setelah [maxDate] tidak bisa dipilih. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateField(
    label: String,
    date: LocalDate?,
    onDateSelected: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
    error: String? = null,
    enabled: Boolean = true,
    maxDate: LocalDate = LocalDate.now()
) {
    var showPicker by rememberSaveable { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }

    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect { interaction ->
            if (interaction is PressInteraction.Release) showPicker = true
        }
    }

    OutlinedTextField(
        value = date?.let { formatDate(it) }.orEmpty(),
        onValueChange = {},
        modifier = modifier.fillMaxWidth(),
        readOnly = true,
        enabled = enabled,
        label = { Text(label) },
        placeholder = { Text("Pilih tanggal") },
        trailingIcon = { Icon(Icons.Filled.CalendarMonth, contentDescription = null) },
        supportingText = error?.let { { Text(it) } },
        isError = error != null,
        singleLine = true,
        interactionSource = interactionSource
    )

    if (showPicker) {
        IndonesianLocale {
            DatePickerDialogContent(
                date = date,
                maxDate = maxDate,
                onDateSelected = onDateSelected,
                onDismiss = { showPicker = false }
            )
        }
    }
}

/**
 * Memaksa [content] memakai bahasa Indonesia apa pun bahasa perangkatnya.
 * DatePicker membaca nama bulan/hari dari LocalConfiguration dan teks judulnya dari LocalContext.
 */
@Composable
private fun IndonesianLocale(content: @Composable () -> Unit) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val localizedConfiguration = remember(configuration) {
        Configuration(configuration).apply { setLocale(localeId) }
    }
    val localizedContext = remember(context, localizedConfiguration) {
        context.createConfigurationContext(localizedConfiguration)
    }
    CompositionLocalProvider(
        LocalConfiguration provides localizedConfiguration,
        LocalContext provides localizedContext,
        content = content
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DatePickerDialogContent(
    date: LocalDate?,
    maxDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    onDismiss: () -> Unit
) {
    val maxMillis = maxDate.toUtcMillis()
    val pickerState = rememberDatePickerState(
        initialSelectedDateMillis = (date ?: maxDate).toUtcMillis(),
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long) = utcTimeMillis <= maxMillis
            override fun isSelectableYear(year: Int) = year <= maxDate.year
        }
    )
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    pickerState.selectedDateMillis?.let { onDateSelected(it.toLocalDate()) }
                    onDismiss()
                }
            ) {
                Text("Pilih")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal") }
        }
    ) {
        DatePicker(state = pickerState)
    }
}
