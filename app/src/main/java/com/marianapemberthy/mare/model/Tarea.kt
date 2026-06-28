package com.marianapemberthy.mare.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tarea")
data class Tarea(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    var nombre: String,
    var duracion: Int,
    var enHoras: Boolean = false,
    var fecha: Long,
    var horaInicio: Int = 480,
    val nivelEnergia: NivelEnergiaType,
    var estado: EstadoTarea = EstadoTarea.PENDIENTE,
    var repetir: Boolean = false,
    var diasRepeticion: String = "",
    var fechaFinRepeticion: Long = 0L,
    var fechaCompletada: Long = 0L
)

