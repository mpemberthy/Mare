package com.marianapemberthy.mare.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope

import com.marianapemberthy.mare.data.MareDatabase
import com.marianapemberthy.mare.model.EmocionType
import com.marianapemberthy.mare.model.NivelEnergiaType
import com.marianapemberthy.mare.model.RegistroDiario
import com.marianapemberthy.mare.model.Tarea
import com.marianapemberthy.mare.util.FechaUtil

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

import java.util.Calendar

class InicioViewModel(application: Application) : AndroidViewModel(application) {

    private val database = MareDatabase.getInstance(application)
    private val tareaDao = database.tareaDao()
    private val registroDao = database.registroDiarioDao()

    private val fechaHoy = FechaUtil.obtenerFechaHoy()
    private val valorEmociones = mapOf(
        EmocionType.FELIZ to 10,
        EmocionType.BIEN to 8,
        EmocionType.NEUTRAL to 6,
        EmocionType.TRISTE to 4,
        EmocionType.ENOJADO to 2
    )

    private val valorEnergia = mapOf(
        NivelEnergiaType.ALTA to 10,
        NivelEnergiaType.MEDIA to 5,
        NivelEnergiaType.BAJA to 3,
    )

    val registroHoy: Flow<RegistroDiario?> = registroDao.obtener()

    val tareasDeHoy: Flow<List<Tarea>> = tareaDao.obtenerTareasHoy(fechaHoy)

    val tareasCompletadasHoy: Flow<List<Tarea>> = tareaDao.obtenerCompletadasHoy(fechaHoy)

    fun obtenerTareasSugeridas(registroHoy: RegistroDiario?): Flow<List<Tarea>> {
        if (registroHoy == null) {
            return flowOf(emptyList())
        }

        val puntosEnergia = valorEnergia[registroHoy.nivelEnergia] ?: 0
        val puntosEmocion = valorEmociones[registroHoy.emocion] ?: 0

        val puntajeTotal = puntosEnergia + puntosEmocion
        val limiteTareas = maxOf(puntajeTotal / 2, 1)

        val nivelesPermitidos = when (registroHoy.nivelEnergia) {
            NivelEnergiaType.ALTA -> listOf(NivelEnergiaType.ALTA.name, NivelEnergiaType.MEDIA.name, NivelEnergiaType.BAJA.name)
            NivelEnergiaType.MEDIA -> listOf(NivelEnergiaType.MEDIA.name, NivelEnergiaType.BAJA.name)
            NivelEnergiaType.BAJA -> listOf(NivelEnergiaType.BAJA.name)
        }

        return tareaDao.obtenerTareasPorNivelesEnergia(nivelesPermitidos).map { listaDeTareas ->
            listaDeTareas.take(limiteTareas)
        }
    }

}

