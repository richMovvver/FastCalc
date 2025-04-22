package com.example.fastca.ui.theme // Или ваш пакет

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Определите вашу светлую цветовую схему M3
private val LightColorScheme = lightColorScheme(
    primary = Purple40, // Основной цвет (например, для кнопок)
    secondary = PurpleGrey40,
    tertiary = Pink40,
    background = LightGrey, // Цвет фона экрана
    surface = White, // Цвет поверхностей (карточки, квадраты)
    onPrimary = White,
    onSecondary = White,
    onTertiary = White,
    onBackground = Black,
    onSurface = Black,
    // Можно переопределить и другие цвета: error, outline и т.д.
    surfaceVariant = MidGrey // Можно использовать для фона кнопки, если primary не подходит
)

// Определите темную схему, если планируете поддерживать темную тему
private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80,
    background = Color(0xFF1C1B1F), // Пример темного фона
    surface = Color(0xFF1C1B1F),
    // ... настройте остальные цвета для темной темы
)

@Composable
fun FastCaTheme( // Переименуйте, если хотите (например, в FastCaM3Theme)
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color доступен на Android 12+
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

    // Настройка системных баров (статус-бар, навигационный бар)
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // Устанавливаем цвет статус-бара
            window.statusBarColor = colorScheme.primary.toArgb() // Или colorScheme.background.toArgb()
            // Настраиваем иконки статус-бара (светлые/темные)
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            // Можно также настроить навигационный бар:
            // window.navigationBarColor = colorScheme.background.toArgb()
            // WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography, // Используем нашу типографику
        // shapes = AppShapes, // Можно определить свои формы (Shapes.kt)
        content = content
    )
}