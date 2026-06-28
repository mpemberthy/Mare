package com.marianapemberthy.mare.model

import androidx.compose.ui.graphics.Color

enum class NivelEnergiaType{
    BAJA, MEDIA, ALTA
}

data class NivelEnergia(
    val nombre: String,
    val color: Color,
    val iconoRes: Int,
    val tipo: NivelEnergiaType
)
