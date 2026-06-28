package com.marianapemberthy.mare.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf

import com.marianapemberthy.mare.data.MareDatabase
import com.marianapemberthy.mare.model.EstadoTarea
import com.marianapemberthy.mare.model.Tarea
import com.marianapemberthy.mare.util.FechaUtil

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.TimeZone

class TareaViewModel(application: Application) : AndroidViewModel(application) {

    private val TAG = "TareaViewModel"
    private val database = MareDatabase.getInstance(application)
    private val tareaDao = database.tareaDao()

    val todasLasTareas: Flow<List<Tarea>> = tareaDao.obtenerTodas()

    var tareaEnEdicion by mutableStateOf<Tarea?>(null)
        private set

    init {
        limpiarTareasAntiguas()
    }

    private fun limpiarTareasAntiguas() {
        val hoy = FechaUtil.obtenerFechaHoy()
        viewModelScope.launch {
            try {
                // Elimina tareas de días anteriores (completadas o no)
                tareaDao.eliminarTareasPasadas(hoy)
            } catch (e: Exception) {
                Log.e(TAG, "Error al limpiar tareas antiguas: ${e.message}")
            }
        }
    }

    fun crearTarea(tarea: Tarea) {
        viewModelScope.launch(Dispatchers.Main.immediate) {
            try {
                if (tarea.repetir && tarea.diasRepeticion.isNotBlank()) {
                    val ocurrencias = generarOcurrencias(tarea)
                    ocurrencias.forEach { tareaDao.insertar(it) }
                } else {
                    val esHoy = FechaUtil.esHoy(tarea.fecha)
                    val tareaAGuardar = tarea.copy(estado = if (esHoy) EstadoTarea.HOY else EstadoTarea.PENDIENTE)
                    tareaDao.insertar(tareaAGuardar)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error al crear tarea '${tarea.nombre}': ${e.message}")
            }
        }
    }

    private fun generarOcurrencias(tarea: Tarea): List<Tarea> {
        val ocurrencias = mutableListOf<Tarea>()
        val diasKeys = listOf("Do", "Lu", "Ma", "Mi", "Ju", "Vi", "Sa")
        val diasSeleccionados = tarea.diasRepeticion.split(",").map { it.trim() }
        val indicesSeleccionados = diasSeleccionados.mapNotNull { diasKeys.indexOf(it).takeIf { i -> i >= 0 } }

        if (indicesSeleccionados.isEmpty()) return listOf(tarea)

        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
            timeInMillis = tarea.fecha
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val fechaFin = if (tarea.fechaFinRepeticion > 0) tarea.fechaFinRepeticion else {
            tarea.fecha + (30L * 24 * 60 * 60 * 1000)
        }

        while (calendar.timeInMillis <= fechaFin) {
            val indiceDia = calendar.get(Calendar.DAY_OF_WEEK) - 1
            if (indicesSeleccionados.contains(indiceDia)) {
                val esHoy = FechaUtil.esHoy(calendar.timeInMillis)
                ocurrencias.add(tarea.copy(
                    id = 0,
                    fecha = calendar.timeInMillis,
                    estado = if (esHoy) EstadoTarea.HOY else EstadoTarea.PENDIENTE
                ))
            }
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }
        return ocurrencias
    }

    private fun esDiaRepeticionHoy(tarea: Tarea): Boolean {
        if (tarea.diasRepeticion.isBlank()) return false
        val diasKeys = listOf("Do", "Lu", "Ma", "Mi", "Ju", "Vi", "Sa")
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
            timeInMillis = FechaUtil.obtenerFechaHoy()
        }
        val indiceDia = calendar.get(Calendar.DAY_OF_WEEK) - 1
        val claveHoy = diasKeys.getOrNull(indiceDia) ?: return false
        val diasSeleccionados = tarea.diasRepeticion.split(",").map { it.trim() }
        return diasSeleccionados.contains(claveHoy)
    }

    fun actualizarTarea(tarea: Tarea) {
        viewModelScope.launch(Dispatchers.Main.immediate) {
            try {
                tareaDao.actualizar(tarea)
            } catch (e: Exception) {
                Log.e(TAG, "Error al actualizar tarea '${tarea.nombre}': ${e.message}")
            }
        }
    }

    fun eliminarTarea(tarea: Tarea) {
        viewModelScope.launch(Dispatchers.Main.immediate) {
            try {
                tareaDao.eliminar(tarea)
            } catch (e: Exception) {
                Log.e(TAG, "Error al eliminar tarea '${tarea.nombre}': ${e.message}")
            }
        }
    }

    fun completarTarea(tarea: Tarea) {
        val hoy = FechaUtil.obtenerFechaHoy()
        val tareaCompletada = tarea.copy(
            estado = EstadoTarea.COMPLETADA,
            fechaCompletada = hoy
        )

        viewModelScope.launch(Dispatchers.Main.immediate) {
            try {
                tareaDao.actualizar(tareaCompletada)
            } catch (e: Exception) {
                Log.e(TAG, "Error al completar tarea '${tarea.nombre}': ${e.message}")
            }
        }
    }

    fun actualizarTareaHoy(tarea: Tarea) {
        viewModelScope.launch {
            try {
                val actualizada = tarea.copy(estado = EstadoTarea.HOY)
                tareaDao.actualizar(actualizada)
            } catch (e: Exception) {
                Log.e(TAG, "Error al actualizar tarea a HOY '${tarea.nombre}': ${e.message}")
            }
        }
    }

    fun cargarTareaParaEdicion(id: Long) {
        viewModelScope.launch {
            try {
                tareaEnEdicion = tareaDao.obtenerTareaPorId(id)
            } catch (e: Exception) {
                Log.e(TAG, "Error al cargar tarea con ID $id: ${e.message}")
                tareaEnEdicion = null
            }
        }
    }

    fun limpiarTareaEnEdicion() {
        tareaEnEdicion = null
    }

    fun modificarTarea(tarea: Tarea) {
        viewModelScope.launch {
            try {
                val estadoActualizado = when {
                    tarea.repetir -> if (esDiaRepeticionHoy(tarea)) EstadoTarea.HOY else EstadoTarea.PENDIENTE
                    else -> if (FechaUtil.esHoy(tarea.fecha)) EstadoTarea.HOY else EstadoTarea.PENDIENTE
                }
                val actualizada = tarea.copy(estado = estadoActualizado)
                tareaDao.actualizar(actualizada)
            } catch (e: Exception) {
                Log.e(TAG, "Error al modificar tarea '${tarea.nombre}': ${e.message}")
            }
        }
    }
}
