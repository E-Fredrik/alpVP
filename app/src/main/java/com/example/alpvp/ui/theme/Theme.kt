package com.example.alpvp.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = ElectricBlue,
    onPrimary = SurfaceWhite,
    primaryContainer = ElectricBlueDark,
    secondary = CoralRed,
    onSecondary = SurfaceWhite,
    tertiary = MintGreen,
    background = Gray900,
    surface = Gray800,
    error = ErrorRed,
    onBackground = SurfaceWhite,
    onSurface = Gray100
)

private val LightColorScheme = lightColorScheme(
    primary = ElectricBlue,
    onPrimary = SurfaceWhite,
    primaryContainer = ElectricBlueLight,
    secondary = CoralRed,
    onSecondary = SurfaceWhite,
    secondaryContainer = CoralRedDark,
    tertiary = MintGreen,
    background = BackgroundLight,
    surface = SurfaceWhite,
    error = ErrorRed,
    onBackground = Gray900,
    onSurface = Gray900,
    surfaceVariant = Gray100,
    onSurfaceVariant = Gray600
)

@Composable
fun AlpVPTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color disabled for brand consistency
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}