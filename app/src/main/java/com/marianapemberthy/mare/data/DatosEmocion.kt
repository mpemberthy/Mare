package com.marianapemberthy.mare.data

import com.marianapemberthy.mare.R
import com.marianapemberthy.mare.model.Emocion
import com.marianapemberthy.mare.model.EmocionType
import com.marianapemberthy.mare.ui.theme.*

val emociones = listOf(
    Emocion("Feliz", ColorFeliz, R.drawable.happy, EmocionType.FELIZ),
    Emocion("Bien", ColorBien, R.drawable.good, EmocionType.BIEN),
    Emocion("Neutral", ColorNeutral, R.drawable.neutral, EmocionType.NEUTRAL),
    Emocion("Triste", ColorTriste, R.drawable.sad, EmocionType.TRISTE),
    Emocion("Enojado", ColorEnojado, R.drawable.angry, EmocionType.ENOJADO)
)