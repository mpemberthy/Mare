package com.marianapemberthy.mare.data

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.marianapemberthy.mare.model.EstadoTarea
import com.marianapemberthy.mare.model.NivelEnergiaType
import com.marianapemberthy.mare.model.Tarea
import com.marianapemberthy.mare.util.FechaUtil
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TareaDaoTest {

    private lateinit var database: MareDatabase
    private lateinit var dao: TareaDao

    @Before
    fun crearBaseDeDatos() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        database = Room.inMemoryDatabaseBuilder(context, MareDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = database.tareaDao()
    }

    @After
    fun cerrarBaseDeDatos() {
        database.close()
    }

    private fun tareaDePrueba(
        nombre: String = "Tarea",
        fecha: Long = FechaUtil.obtenerFechaHoy(),
        horaInicio: Int = 480,
        nivelEnergia: NivelEnergiaType = NivelEnergiaType.MEDIA,
        estado: EstadoTarea = EstadoTarea.PENDIENTE,
        fechaCompletada: Long = 0L
    ) = Tarea(
        nombre = nombre,
        iconoRes = 0,
        duracion = 30,
        fecha = fecha,
        horaInicio = horaInicio,
        nivelEnergia = nivelEnergia,
        estado = estado,
        fechaCompletada = fechaCompletada
    )

    @Test
    fun insertar_yObtenerPorId_devuelveLaMismaTarea() = runBlocking {
        val id = dao.insertar(tareaDePrueba(nombre = "Estudiar"))

        val recuperada = dao.obtenerTareaPorId(id)

        assertEquals("Estudiar", recuperada?.nombre)
        assertEquals(id, recuperada?.id)
    }

    @Test
    fun insertar_devuelveIdGeneradoMayorQueCero() = runBlocking {
        val id = dao.insertar(tareaDePrueba())
        assertTrue(id > 0)
    }

    @Test
    fun obtenerTareasHoy_soloDevuelveEstadoHoyConFechaDeHoy() = runBlocking {
        val hoy = FechaUtil.obtenerFechaHoy()

        dao.insertar(tareaDePrueba(nombre = "A", fecha = hoy, estado = EstadoTarea.HOY))
        dao.insertar(tareaDePrueba(nombre = "B", fecha = hoy, estado = EstadoTarea.PENDIENTE))
        dao.insertar(tareaDePrueba(nombre = "C", fecha = hoy + 86_400_000L, estado = EstadoTarea.HOY))

        val resultado = dao.obtenerTareasHoy(hoy).first()

        assertEquals(1, resultado.size)
        assertEquals("A", resultado[0].nombre)
    }

    @Test
    fun obtenerCompletadasHoy_filtraPorFechaCompletada() = runBlocking {
        val hoy = FechaUtil.obtenerFechaHoy()
        val ayer = hoy - 86_400_000L

        dao.insertar(tareaDePrueba(nombre = "CompletadaHoy", estado = EstadoTarea.COMPLETADA, fechaCompletada = hoy))
        dao.insertar(tareaDePrueba(nombre = "CompletadaAyer", estado = EstadoTarea.COMPLETADA, fechaCompletada = ayer))
        dao.insertar(tareaDePrueba(nombre = "Pendiente", estado = EstadoTarea.PENDIENTE))

        val resultado = dao.obtenerCompletadasHoy(hoy).first()

        assertEquals(1, resultado.size)
        assertEquals("CompletadaHoy", resultado[0].nombre)
    }

    @Test
    fun obtenerTareasPorNivelesEnergia_excluyeCompletadasYHoy() = runBlocking {
        dao.insertar(tareaDePrueba(nombre = "Sugerible", nivelEnergia = NivelEnergiaType.BAJA, estado = EstadoTarea.PENDIENTE))
        dao.insertar(tareaDePrueba(nombre = "Completada", nivelEnergia = NivelEnergiaType.BAJA, estado = EstadoTarea.COMPLETADA))
        dao.insertar(tareaDePrueba(nombre = "DeHoy", nivelEnergia = NivelEnergiaType.BAJA, estado = EstadoTarea.HOY))

        val resultado = dao.obtenerTareasPorNivelesEnergia(listOf("BAJA")).first()

        assertEquals(1, resultado.size)
        assertEquals("Sugerible", resultado[0].nombre)
    }

    @Test
    fun actualizar_modificaCamposPersistidos() = runBlocking {
        val id = dao.insertar(tareaDePrueba(nombre = "Original", horaInicio = 480))
        val original = dao.obtenerTareaPorId(id)!!

        val modificada = original.copy(nombre = "Modificada", horaInicio = 900, estado = EstadoTarea.HOY)
        dao.actualizar(modificada)

        val recuperada = dao.obtenerTareaPorId(id)
        assertEquals("Modificada", recuperada?.nombre)
        assertEquals(900, recuperada?.horaInicio)
        assertEquals(EstadoTarea.HOY, recuperada?.estado)
    }

    @Test
    fun eliminar_quitaTareaDeLaBaseDeDatos() = runBlocking {
        val id = dao.insertar(tareaDePrueba(nombre = "Eliminar"))
        val tarea = dao.obtenerTareaPorId(id)!!

        dao.eliminar(tarea)

        val lista = dao.obtenerTodas().first()
        assertTrue(lista.none { it.id == id })
    }

    @Test
    fun obtenerTodas_ordenaPorFechaAscendente() = runBlocking {
        val hoy = FechaUtil.obtenerFechaHoy()
        dao.insertar(tareaDePrueba(nombre = "Pasado mañana", fecha = hoy + 2 * 86_400_000L))
        dao.insertar(tareaDePrueba(nombre = "Hoy", fecha = hoy))
        dao.insertar(tareaDePrueba(nombre = "Mañana", fecha = hoy + 86_400_000L))

        val lista = dao.obtenerTodas().first()

        assertEquals(listOf("Hoy", "Mañana", "Pasado mañana"), lista.map { it.nombre })
    }
}
