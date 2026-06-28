package com.marianapemberthy.mare.ui

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.marianapemberthy.mare.model.EstadoTarea
import com.marianapemberthy.mare.model.NivelEnergiaType
import com.marianapemberthy.mare.model.Tarea
import com.marianapemberthy.mare.ui.components.TarjetaTarea
import com.marianapemberthy.mare.ui.theme.MareTheme
import com.marianapemberthy.mare.util.FechaUtil
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TarjetaTareaTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun tareaDePrueba() = Tarea(
        id = 1,
        nombre = "Tarea de prueba",
        iconoRes = 0,
        duracion = 20,
        fecha = FechaUtil.obtenerFechaHoy(),
        horaInicio = 480,
        nivelEnergia = NivelEnergiaType.MEDIA,
        estado = EstadoTarea.PENDIENTE
    )

    @Test
    fun tapEnCirculo_llamaOnCompletarUnaSolaVez() {
        var contador = 0

        composeTestRule.setContent {
            MareTheme {
                TarjetaTarea(
                    tarea = tareaDePrueba(),
                    onCompletar = { contador++ },
                    onEditar = {}
                )
            }
        }

        val circulo = composeTestRule.onNodeWithContentDescription("Completar tarea")

        circulo.performClick()

        composeTestRule.mainClock.advanceTimeBy(500)
        composeTestRule.waitForIdle()

        assertEquals(1, contador)
    }

    @Test
    fun dobleTapRapidoEnCirculo_noDuplicaLlamadaAOnCompletar() {
        var contador = 0

        composeTestRule.setContent {
            MareTheme {
                TarjetaTarea(
                    tarea = tareaDePrueba(),
                    onCompletar = { contador++ },
                    onEditar = {}
                )
            }
        }

        val circulo = composeTestRule.onNodeWithContentDescription("Completar tarea")

        circulo.performClick()
        circulo.performClick()

        composeTestRule.mainClock.advanceTimeBy(500)
        composeTestRule.waitForIdle()

        assertEquals(1, contador)
    }

    @Test
    fun tareaYaCompletada_circuloDeshabilitado() {
        var contador = 0

        composeTestRule.setContent {
            MareTheme {
                TarjetaTarea(
                    tarea = tareaDePrueba().copy(estado = EstadoTarea.COMPLETADA),
                    onCompletar = { contador++ },
                    onEditar = {}
                )
            }
        }

        val circulo = composeTestRule.onNodeWithContentDescription("Completar tarea")
        circulo.performClick()

        composeTestRule.mainClock.advanceTimeBy(500)
        composeTestRule.waitForIdle()

        assertEquals(0, contador)
    }
}
