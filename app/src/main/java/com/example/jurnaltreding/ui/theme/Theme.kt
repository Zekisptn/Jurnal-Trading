package com.example.jurnaltreding.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = Green40,
    onPrimary = Color.White,
    primaryContainer = GreenContainer90,
    onPrimaryContainer = OnGreenContainer10,
    secondary = Slate40,
    onSecondary = Color.White,
    secondaryContainer = SlateContainer90,
    onSecondaryContainer = OnSlateContainer10,
    tertiary = Amber40,
    onTertiary = Color.White,
    tertiaryContainer = AmberContainer90,
    onTertiaryContainer = OnAmberContainer10,
    error = Red40,
    onError = Color.White,
    errorContainer = RedContainer90,
    onErrorContainer = OnRedContainer10,
    background = Background99,
    onBackground = OnSurface10,
    surface = Surface99,
    onSurface = OnSurface10,
    surfaceVariant = SurfaceVariant90,
    onSurfaceVariant = OnSurfaceVariant30,
    outline = Outline50,
    outlineVariant = OutlineVariant80,
    surfaceContainerLowest = SurfaceContainerLowest100,
    surfaceContainerLow = SurfaceContainerLow96,
    surfaceContainer = SurfaceContainer94,
    surfaceContainerHigh = SurfaceContainerHigh92,
    surfaceContainerHighest = SurfaceContainerHighest90
)

private val DarkColorScheme = darkColorScheme(
    primary = Green80,
    onPrimary = OnGreen20,
    primaryContainer = GreenContainer30,
    onPrimaryContainer = GreenContainer90,
    secondary = Slate80,
    onSecondary = OnSlate20,
    secondaryContainer = SlateContainer30,
    onSecondaryContainer = SlateContainer90,
    tertiary = Amber80,
    onTertiary = OnAmber20,
    tertiaryContainer = AmberContainer30,
    onTertiaryContainer = AmberContainer90,
    error = Red80,
    onError = OnRed20,
    errorContainer = RedContainer30,
    onErrorContainer = RedContainer90,
    background = Background10,
    onBackground = OnSurface90,
    surface = Surface10,
    onSurface = OnSurface90,
    surfaceVariant = SurfaceVariant30,
    onSurfaceVariant = OnSurfaceVariant80,
    outline = Outline60,
    outlineVariant = OutlineVariant30,
    surfaceContainerLowest = SurfaceContainerLowest4,
    surfaceContainerLow = SurfaceContainerLow10,
    surfaceContainer = SurfaceContainer12,
    surfaceContainerHigh = SurfaceContainerHigh17,
    surfaceContainerHighest = SurfaceContainerHighest22
)

/** Warna tambahan di luar ColorScheme Material 3: penanda untung dan rugi. */
@Immutable
data class TradingColors(
    val profit: Color,
    val loss: Color
)

private val LightTradingColors = TradingColors(profit = ProfitLight, loss = LossLight)
private val DarkTradingColors = TradingColors(profit = ProfitDark, loss = LossDark)

private val LocalTradingColors = staticCompositionLocalOf { LightTradingColors }

val MaterialTheme.tradingColors: TradingColors
    @Composable
    @ReadOnlyComposable
    get() = LocalTradingColors.current

@Composable
fun JurnalTredingTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val tradingColors = if (darkTheme) DarkTradingColors else LightTradingColors

    CompositionLocalProvider(LocalTradingColors provides tradingColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            shapes = Shapes,
            content = content
        )
    }
}
