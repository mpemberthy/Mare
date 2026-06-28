package com.marianapemberthy.mare.viewmodel

import android.app.Application
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.marianapemberthy.mare.data.MareDatabase
import com.marianapemberthy.mare.model.EstadoTarea
import com.marianapemberthy.mare.model.NivelEnergiaType
import com.marianapemberthy.mare.model.Tarea
import com.marianapemberthy.mare.util.FechaUtil
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.Calendar
import java.util.TimeZone

@RunWith(AndroidJUnit4::class)
class TareaViewModelIntegrationTest {

    private lateinit var context: Application
    private lateinit var database: MareDatabase
    private lateinit var viewModel: TareaViewModel

    private val diasKeys = listOf("Do", "Lu", "Ma", "Mi", "Ju", "Vi", "Sa")

    @Before
    fun setup() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
        context = ApplicationProvider.getApplicationContext()

        database = Room.inMemoryDatabaseBuilder(context, MareDatabase::class.java)
            .allowMainThreadQueries()
            .build()

        MareDatabase.setInstanceForTesting(database)
        viewModel = TareaViewModel(context)
    }

    @After
    fun tearDown() {
        MareDatabase.setInstanceForTesting(null)
        database.close()
    }

    private fun claveDiaHoy(): String {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        return diasKeys[calendar.get(Calendar.DAY_OF_WEEK) - 1]
    }

    private fun claveDiaMañana(): String {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
            add(Calendar.DAY_OF_MONTH, 1)
        }
        return diasKeys[calendar.get(Calendar.DAY_OF_WEEK) - 1]
    }

    @Test
    fun crearTareaRepetible_generaTodasLasOcurrenciasHastaFechaFin() = runBlocking {
        val hoy = claveDiaHoy()
        val mañana = claveDiaMañana()
        val fechaFin = FechaUtil.obtenerFechaHoy() + 7L * 86_400_000L // 1 semana

        val tarea = Tarea(
            nombre = "Meditar",
            iconoRes = 0,
            duracion = 10,
            fecha = FechaUtil.obtenerFechaHoy(),
            horaInicio = 480,
            nivelEnergia = NivelEnergiaType.MEDIA,
            repetir = true,
            diasRepeticion = "$hoy, $mañana",
            fechaFinRepeticion = fechaFin
        )

        viewModel.crearTarea(tarea)
        kotlinx.coroutines.delay(500)

        val lista = database.tareaDao().obtenerTodasStatic()

        assertTrue("Debería haber generado múltiples ocurrencias", lista.size >= 2)
        assertTrue("Debería existir la ocurrencia de hoy", lista.any { it.fecha == FechaUtil.obtenerFechaHoy() })
        
        val ocurrenciaHoy = lista.first { it.fecha == FechaUtil.obtenerFechaHoy() }
        assertEquals(EstadoTarea.HOY, ocurrenciaHoy.estado)
    }

    @Test
    fun completarTareaRepetible_noGeneraNuevasOcurrencias() = runBlocking {
        val hoy = claveDiaHoy()
        val tarea = Tarea(
            nombre = "Ejercicio",
            iconoRes = 0,
            duracion = 30,
            fecha = FechaUtil.obtenerFechaHoy(),
            nivelEnergia = NivelEnergiaType.MEDIA,
            repetir = true,
            diasRepeticion = hoy,
            fechaFinRepeticion = FechaUtil.obtenerFechaHoy() + 2L * 86_400_000L
        )

        viewModel.crearTarea(tarea)
        kotlinx.coroutines.delay(500)

        val listaAntes = database.tareaDao().obtenerTodasStatic()
        val numAntes = listaAntes.size
        val tareaHoy = listaAntes.first { it.estado == EstadoTarea.HOY }

        viewModel.completarTarea(tareaHoy)
        kotlinx.coroutines.delay(500)

        val listaDespues = database.tareaDao().obtenerTodasStatic()
        assertEquals("No se deben crear nuevas filas al completar", numAntes, listaDespues.size)
        
        val tareaRecuperada = database.tareaDao().obtenerTareaPorId(tareaHoy.id)
        assertEquals(EstadoTarea.COMPLETADA, tareaRecuperada?.estado)
    }

    @Test
    fun editarTarea_actualizaDatosEnBaseDeDatos() = runBlocking {
        val tarea = Tarea(
            nombre = "Leer",
            iconoRes = 0,
            duracion = 20,
            fecha = FechaUtil.obtenerFechaHoy(),
            horaInicio = 480,
            nivelEnergia = NivelEnergiaType.BAJA,
            repetir = false
        )

        viewModel.crearTarea(tarea)
        kotlinx.coroutines.delay(300)

        val lista = database.tareaDao().obtenerTodasStatic()
        val creada = lista.first()

        val editada = creada.copy(nombre = "Leer libro nuevo", horaInicio = 600)
        viewModel.modificarTarea(editada)
        kotlinx.coroutines.delay(300)

        val actualizada = database.tareaDao().obtenerTareaPorId(creada.id)
        assertNotNull(actualizada)
        assertEquals("Leer libro nuevo", actualizada?.nombre)
        assertEquals(600, actualizada?.horaInicio)
    }
}
