package cl.duoc.therionlabs.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val Light =
    lightColorScheme(
        primary = DuocBlue,
        onPrimary = Color.White,
        primaryContainer = Color(0xFFD9E7F0),
        onPrimaryContainer = DuocBlue,
        secondary = DuocBlue,
        secondaryContainer = DuocYellow,
        onSecondaryContainer = DuocBlue,
        tertiary = DuocBlue,
        background = DuocBackground,
        surface = Color.White,
    )
private val Dark =
    darkColorScheme(
        primary = DuocYellow,
        onPrimary = DuocBlue,
        primaryContainer = DuocBlue,
        onPrimaryContainer = Color.White,
        secondary = DuocYellow,
        secondaryContainer = DuocBlue,
        onSecondaryContainer = Color.White,
    )

@Composable
fun TherionLabsAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) Dark else Light,
        typography = Typography,
        content = content,
    )
}
