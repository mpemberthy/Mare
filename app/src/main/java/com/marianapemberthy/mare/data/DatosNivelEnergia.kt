package com.marianapemberthy.mare.data

import com.marianapemberthy.mare.R
import com.marianapemberthy.mare.model.NivelEnergia
import com.marianapemberthy.mare.model.NivelEnergiaType
import com.marianapemberthy.mare.ui.theme.*

val nivelesEnergia = listOf(
    NivelEnergia("Alta", EnergiaAlta, R.drawable.high, NivelEnergiaType.ALTA),
    NivelEnergia("Media", EnergiaMedia, R.drawable.medium, NivelEnergiaType.MEDIA),
    NivelEnergia("Baja", EnergiaBaja, R.drawable.low, NivelEnergiaType.BAJA)
)