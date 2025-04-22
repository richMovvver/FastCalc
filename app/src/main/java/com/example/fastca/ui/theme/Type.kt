package com.example.fastca.ui.theme // Или ваш пакет

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Замените на ваши шрифты, если используете кастомные
val AppTypography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    titleLarge = TextStyle( // Используется для заголовков по умолчанию
        fontFamily = FontFamily.Default, // Замените на FontFamily.Cursive, если хотите
        fontWeight = FontWeight.Bold, // FontWeight.Normal
        fontSize = 22.sp, // 32.sp
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    // Определите другие стили текста по мере необходимости
)