package com.marianapemberthy.mare.viewmodel

import android.app.Application
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.marianapemberthy.mare.data.MareDatabase
import com.marianapemberthy.mare.model.EstadoTarea
import com.marianapemberthy.mare.model.NivelEnergiaType
import com.marianapemberthy.mare.model.Tarea
import com.marianapemberthy.mare.util.FechaUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.Calendar
import java.util.TimeZone
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class TareaViewModelTest {

    private lateinit var viewModel: TareaViewModel
    private lateinit var database: MareDatabase
    private val testDispatcher = StandardTestDispatcher()

    private val diasKeys = listOf("Do", "Lu", "Ma", "Mi", "Ju", "Vi", "Sa")

    @Before
    fun setup() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
        Dispatchers.setMain(testDispatcher)

        val context = ApplicationProvider.getApplicationContext<Application>()

        database = Room.inMemoryDatabaseBuilder(context, MareDatabase::class.java)
            .allowMainThreadQueries()
            .setQueryCoroutineContext(testDispatcher)
            .build()

        MareDatabase.setInstanceForTesting(database)

        viewModel = TareaViewModel(context)
    }

    @After
    fun tearDown() {
        MareDatabase.setInstanceForTesting(null)
        database.close()
        Dispatchers.resetMain()
    }

    private fun claveDiaHoy(): String {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
            timeInMillis = FechaUtil.obtenerFechaHoy()
        }
        val indice = calendar.get(Calendar.DAY_OF_WEEK) - 1 // 0=Domingo
        return diasKeys[indice]
    }

    private fun claveDiaMañana(): String {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
            timeInMillis = FechaUtil.obtenerFechaHoy()
            add(Calendar.DAY_OF_MONTH, 1)
        }
        val indice = calendar.get(Calendar.DAY_OF_WEEK) - 1
        return diasKeys[indice]
    }

    private fun tareaRepetible(
        diasRepeticion: String,
        fechaFinRepeticion: Long = FechaUtil.obtenerFechaHoy() + 7L * 24 * 60 * 60 * 1000 // 1 semana para pruebas
    ) = Tarea(
        nombre = "Tarea repetible",
        iconoRes = 0,
        duracion = 30,
        fecha = FechaUtil.obtenerFechaHoy(),
        horaInicio = 480,
        nivelEnergia = NivelEnergiaType.MEDIA,
        repetir = true,
        diasRepeticion = diasRepeticion,
        fechaFinRepeticion = fechaFinRepeticion
    )

    @Test
    fun crearTarea_repetible_generaMultiplesOcurrencias() = runTest {
        val hoy = claveDiaHoy()
        val mañana = claveDiaMañana()
        val tarea = tareaRepetible(diasRepeticion = "$hoy, $mañana")

        viewModel.crearTarea(tarea)
        testDispatcher.scheduler.advanceUntilIdle()

        val lista = database.tareaDao().obtenerTodasStatic()
        assertTrue(lista.size >= 2)
        assertTrue(lista.any { it.estado == EstadoTarea.HOY })
        assertTrue(lista.any { it.estado == EstadoTarea.PENDIENTE && it.fecha > FechaUtil.obtenerFechaHoy() })
    }

    @Test
    fun crearTarea_repetible_noGeneraTareasParaDiasNoSeleccionados() = runTest {
        val hoy = claveDiaHoy()
        val todosLosDiasMenosHoy = diasKeys.filter { it != hoy }.joinToString(",")
        val tarea = tareaRepetible(diasRepeticion = todosLosDiasMenosHoy)

        viewModel.crearTarea(tarea)
        testDispatcher.scheduler.advanceUntilIdle()

        val lista = database.tareaDao().obtenerTodasStatic()
        assertFalse(lista.any { it.estado == EstadoTarea.HOY }, "No debería haber tareas para hoy si no se seleccionó el día")
    }

    @Test
    fun limpiarTareasAntiguas_eliminaTareasDeAyer() = runTest {
        val ayer = FechaUtil.obtenerFechaHoy() - 86_400_000L
        val tareaAyer = Tarea(
            nombre = "Tarea de ayer",
            iconoRes = 0,
            duracion = 10,
            fecha = ayer,
            nivelEnergia = NivelEnergiaType.BAJA
        )
        database.tareaDao().insertar(tareaAyer)

        val context = ApplicationProvider.getApplicationContext<Application>()
        val newViewModel = TareaViewModel(context)
        testDispatcher.scheduler.advanceUntilIdle()

        val lista = database.tareaDao().obtenerTodasStatic()
        assertTrue(lista.none { it.fecha < FechaUtil.obtenerFechaHoy() })
    }

    @Test
    fun completarTarea_soloCambiaEstadoDeEsaOcurrencia() = runTest {
        val hoy = claveDiaHoy()
        val mañana = claveDiaMañana()
        val tarea = tareaRepetible(diasRepeticion = "$hoy, $mañana")

        viewModel.crearTarea(tarea)
        testDispatcher.scheduler.advanceUntilIdle()

        val listaAntes = database.tareaDao().obtenerTodasStatic()
        val tareaHoy = listaAntes.first { it.estado == EstadoTarea.HOY }
        val numTareasAntes = listaAntes.size

        viewModel.completarTarea(tareaHoy)
        testDispatcher.scheduler.advanceUntilIdle()

        val listaDespues = database.tareaDao().obtenerTodasStatic()
        assertEquals(numTareasAntes, listaDespues.size, "No deberían crearse nuevas tareas al completar")
        
        val tareaRecuperada = database.tareaDao().obtenerTareaPorId(tareaHoy.id)
        assertEquals(EstadoTarea.COMPLETADA, tareaRecuperada?.estado)
    }

    @Test
    fun crearTarea_noRepetible_fechaHoy_quedaEstadoHoy() = runTest {
        val tarea = Tarea(
            nombre = "Tarea simple",
            iconoRes = 0,
            duracion = 15,
            fecha = FechaUtil.obtenerFechaHoy(),
            horaInicio = 600,
            nivelEnergia = NivelEnergiaType.ALTA,
            repetir = false
        )

        viewModel.crearTarea(tarea)
        testDispatcher.scheduler.advanceUntilIdle()

        val lista = database.tareaDao().obtenerTodasStatic()
        assertEquals(1, lista.size)
        assertEquals(EstadoTarea.HOY, lista[0].estado)
    }

    @Test
    fun completarTarea_noRepetible_noGeneraNuevasFilas() = runTest {
        val tarea = Tarea(
            nombre = "Tarea única",
            iconoRes = 0,
            duracion = 20,
            fecha = FechaUtil.obtenerFechaHoy(),
            horaInicio = 540,
            nivelEnergia = NivelEnergiaType.MEDIA,
            repetir = false
        )

        viewModel.crearTarea(tarea)
        testDispatcher.scheduler.advanceUntilIdle()

        val creada = database.tareaDao().obtenerTodasStatic().first()

        viewModel.completarTarea(creada)
        testDispatcher.scheduler.advanceUntilIdle()

        val lista = database.tareaDao().obtenerTodasStatic()

        assertEquals(1, lista.size)
        assertEquals(EstadoTarea.COMPLETADA, lista[0].estado)
    }
}
