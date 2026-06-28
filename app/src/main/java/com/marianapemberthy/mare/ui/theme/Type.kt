package com.marianapemberthy.mare.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

import com.marianapemberthy.mare.R

val AtkinsonFamily = FontFamily(
    Font(R.font.ahn_regular, FontWeight.Normal),
    Font(R.font.ahn_light, FontWeight.Light),
    Font(R.font.ahn_medium, FontWeight.Medium),
    Font(R.font.ahn_semibold, FontWeight.SemiBold),
    Font(R.font.ahn_bold, FontWeight.Bold),
    Font(R.font.ahn_extrabold, FontWeight.ExtraBold)
)

val Typography = Typography(
    // Display — 48sp Bold
    displayLarge = TextStyle(
        fontFamily = AtkinsonFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 48.sp
    ),
    // Título 1 — 32sp Bold
    headlineLarge = TextStyle(
        fontFamily = AtkinsonFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp
    ),
    // Título 2 — 32sp SemiBold
    headlineMedium = TextStyle(
        fontFamily = AtkinsonFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 32.sp
    ),
    // Subtítulo 1 — 24sp Medium
    titleLarge = TextStyle(
        fontFamily = AtkinsonFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 24.sp
    ),
    // Subtítulo 2 — 24sp Light
    titleMedium = TextStyle(
        fontFamily = AtkinsonFamily,
        fontWeight = FontWeight.Light,
        fontSize = 24.sp
    ),
    // Cuerpo 1 — 16sp Regular
    bodyLarge = TextStyle(
        fontFamily = AtkinsonFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp
    ),
    // Cuerpo 2 — 18sp Regular
    bodyMedium = TextStyle(
        fontFamily = AtkinsonFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 18.sp,
        lineHeight = 28.sp
    ),
    // Cuerpo 3 — 20sp Regular
    bodySmall = TextStyle(
        fontFamily = AtkinsonFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 20.sp,
        lineHeight = 28.sp
    ),
    // Caption — 14sp Regular
    labelSmall = TextStyle(
        fontFamily = AtkinsonFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp
    )
)