package hr.ferit.tomislavcelic.gamecompanion.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material3.Typography
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape

private val LightColors: ColorScheme = lightColorScheme(
    primary            = BluePrimary,
    onPrimary          = Color.White,
    primaryContainer   = BluePrimaryVariant,
    onPrimaryContainer = Color.White,
    secondary          = PurpleAccent,
    onSecondary        = Color.White,
    tertiary           = OrangeAccent,
    onTertiary         = Color.Black,
    background         = GreySurfaceLight,
    onBackground       = Color.Black,
    surface            = GreySurfaceLight,
    onSurface          = Color.Black
)

private val DarkColors: ColorScheme = darkColorScheme(
    primary            = BluePrimaryVariant,
    onPrimary          = Color.Black,
    primaryContainer   = BluePrimary,
    onPrimaryContainer = Color.White,
    secondary          = PurpleAccent,
    onSecondary        = Color.Black,
    tertiary           = OrangeAccent,
    onTertiary         = Color.Black,
    background         = GreySurfaceDark,
    onBackground       = Color.White,
    surface            = GreySurfaceDark,
    onSurface          = Color.White
)

// Simple headline / body tweak: bold “Registration”, regular body text
private val GCtypography = Typography(
    displayLarge = Typography().displayLarge.copy(
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp
    )
)

@Composable
fun GameCompanionTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColors else LightColors

    MaterialTheme(
        colorScheme = colors,
        typography  = GCtypography,
        shapes      = MaterialTheme.shapes.copy(extraLarge = RoundedCornerShape(28.dp)),
        content     = content
    )
}
