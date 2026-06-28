package com.marianapemberthy.mare.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Primario,
    secondary = Secundario,
    background = GrisSecundario,
    surface = GrisPrimario,
    onPrimary = Blanco,
    onSecondary = Negro,
    onBackground = Blanco,
    onSurface = Blanco,
    error = Rojo,
    onError = Blanco
)

private val LightColorScheme = lightColorScheme(
    primary = Primario,
    secondary = Secundario,
    background = Blanco,
    surface = Blanco,
    onPrimary = Blanco,
    onSecondary = Negro,
    onBackground = GrisPrimario,
    onSurface = GrisPrimario,
    error = Rojo,
    onError = Blanco
)

@Composable
fun MareTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
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