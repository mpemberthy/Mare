package com.marianapemberthy.mare.data

import androidx.room.*
import com.marianapemberthy.mare.model.Tarea
import kotlinx.coroutines.flow.Flow

@Dao
interface TareaDao {

    @Insert
    suspend fun insertar(tarea: Tarea): Long

    @Update
    suspend fun actualizar(tarea: Tarea)

    @Delete
    suspend fun eliminar(tarea: Tarea)


    @Query("DELETE FROM tarea WHERE fecha < :fechaHoy")
    suspend fun eliminarTareasPasadas(fechaHoy: Long)

    @Query("SELECT * FROM tarea WHERE id = :id")
    suspend fun obtenerTareaPorId(id: Long): Tarea?

    @Query("SELECT * FROM tarea ORDER BY fecha ASC")
    fun obtenerTodas(): Flow<List<Tarea>>

    @Query("SELECT * FROM tarea WHERE estado = 'HOY' AND fecha = :fechaHoy")
    fun obtenerTareasHoy(fechaHoy: Long): Flow<List<Tarea>>

    @Query("SELECT * FROM tarea WHERE estado = 'COMPLETADA' AND fechaCompletada = :fechaHoy ORDER BY nombre ASC")
    fun obtenerCompletadasHoy(fechaHoy: Long): Flow<List<Tarea>>

    @Query("SELECT * FROM tarea WHERE estado = 'PENDIENTE'")
    fun obtenerPendientes(): Flow<List<Tarea>>

    @Query("SELECT * FROM tarea WHERE nivelEnergia IN (:nivelesPermitidos) AND estado = 'PENDIENTE' AND repetir = 0")
    fun obtenerTareasPorNivelesEnergia(nivelesPermitidos: List<String>): Flow<List<Tarea>>
}