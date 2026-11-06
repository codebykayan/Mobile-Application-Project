package com.example.tinystep_stage2.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

private val LightColorScheme = lightColorScheme(
    primary = PrimaryGreen,
    onPrimary = OnPrimaryGreen,
    secondary = SecondaryAccent,
    onSecondary = OnSecondaryAccent,
    tertiary = TertiaryInProgress,
    onTertiary = OnTertiary,
    error = ErrorRed,
    onError = OnErrorRed,
    background = ParentScreenBackground,
    onBackground = AppText,
    surface = SurfaceWhite,
    onSurface = AppText,
    surfaceVariant = ChildScreenBackground,
    onSurfaceVariant = SecondaryText
)

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryGreen,
    onPrimary = OnPrimaryGreen,
    secondary = SecondaryAccent,
    onSecondary = OnSecondaryAccent,
    tertiary = TertiaryInProgress,
    onTertiary = OnTertiary,
    error = ErrorRed,
    onError = OnErrorRed,
    background = ParentScreenBackground,
    onBackground = AppText,
    surface = SurfaceWhite,
    onSurface = AppText,
    surfaceVariant = ChildScreenBackground,
    onSurfaceVariant = SecondaryText
)

val TinyStepsShapes = Shapes(
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp)
)

@Composable
fun TinyStepsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
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
        shapes = TinyStepsShapes,
        content = content
    )
}
