package com.app.myappdeinventario.views.ui.theme

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
    primary = verdeAzuladoMedio,
    secondary = doradoSuave,
    tertiary = verdeMintoClaro,
    background = verdePetróleoOscuro,
    surface = verdePetróleoOscuro,
    onPrimary = blanco,
    onSecondary = verdeOscuroProfundo,
    onBackground = blanco,
    onSurface = blanco
)

private val LightColorScheme = lightColorScheme(
    primary = verdeAzuladoMedio,
    secondary = doradoSuave,
    tertiary = verdeSalviaClaro,
    background = verdeMintoClaro,
    surface = blanco,
    onPrimary = blanco,
    onSecondary = verdeOscuroProfundo,
    onBackground = verdeOscuroProfundo,
    onSurface = verdeOscuroProfundo
)

@Composable
fun MyAppDeInventarioTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Cambiar a false para usar tus colores personalizados
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