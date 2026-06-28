package com.marianapemberthy.mare.model

import androidx.compose.ui.graphics.Color

enum class EmocionType {
    FELIZ, BIEN, NEUTRAL, TRISTE, ENOJADO
}

data class Emocion(
    val nombre: String,
    val color: Color,
    val iconoRes: Int,
    val tipo: EmocionType
)


