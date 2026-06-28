package com.marianapemberthy.mare.data

import androidx.room.*

import com.marianapemberthy.mare.model.RegistroDiario

import kotlinx.coroutines.flow.Flow

@Dao
interface RegistroDiarioDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(registro: RegistroDiario)

    @Query("SELECT * FROM registro_diario WHERE id = 1")
    fun obtener(): Flow<RegistroDiario?>

    @Query("SELECT * FROM registro_diario WHERE fecha = :fecha")
    suspend fun obtenerPorFecha(fecha: Long): RegistroDiario?
}