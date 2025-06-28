package com.lunnaris.clicky.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

val ColorScheme.success: Color
    @Composable
    get() = if (isSystemInDarkTheme()) SuccessDark else SuccessLight

val ColorScheme.onSuccess: Color
    @Composable
    get() = if (isSystemInDarkTheme()) OnSuccessDark else OnSuccessLight

val ColorScheme.successContainer: Color
    @Composable
    get() = if (isSystemInDarkTheme()) SuccessContainerDark else SuccessContainerLight

val ColorScheme.onSuccessContainer: Color
    @Composable
    get() = if (isSystemInDarkTheme()) OnSuccessContainerDark else OnSuccessContainerLight

private val DarkColorScheme = darkColorScheme(
    primary = TealCyan40,
    onPrimary = Color.White,
    primaryContainer = TealCyan80,
    onPrimaryContainer = Color.White,

    secondary = TealCyan70,
    onSecondary = Color.White,
    secondaryContainer = TealCyan80,
    onSecondaryContainer = Color.White,
)

private val LightColorScheme = lightColorScheme(
    primary = TealCyan60,
    onPrimary = Color.White,
    primaryContainer = TealCyan80,
    onPrimaryContainer = OnTealCyan80,

    secondary = TealCyan60,
    onSecondary = Color.White,
    secondaryContainer = TealCyan80,
    onSecondaryContainer = OnTealCyan80,
)

@Composable
fun ClickyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
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