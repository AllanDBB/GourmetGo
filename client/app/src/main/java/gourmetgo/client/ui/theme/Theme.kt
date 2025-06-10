package gourmetgo.client.ui.theme

// to detect system theme
//import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val GourmetGoPrimary = Color(0xFF388E3C) //verde
private val GourmetGoSecondary = Color(0xFFD4A60D) //amarillo
private val GourmetGoTertiary = Color(0xFF1976D2)// azul links

private val DarkColorScheme = darkColorScheme(
    primary = GourmetGoPrimary,
    secondary = GourmetGoSecondary,
    tertiary = GourmetGoTertiary,
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White,
)

private val LightColorScheme = lightColorScheme(
    primary = GourmetGoPrimary,
    secondary = GourmetGoSecondary,
    tertiary = GourmetGoTertiary,
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),



    )

@Composable
fun AppTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}