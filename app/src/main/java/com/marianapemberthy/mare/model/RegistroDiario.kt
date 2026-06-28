package com.marianapemberthy.mare.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "registro_diario")
data class RegistroDiario(
    @PrimaryKey
    val id: Int = 1,
    var nivelEnergia: NivelEnergiaType,
    var emocion: EmocionType,
    var fecha: Long
)