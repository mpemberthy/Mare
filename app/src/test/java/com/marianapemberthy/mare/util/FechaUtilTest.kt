package com.marianapemberthy.mare.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar
import java.util.TimeZone
class FechaUtilTest {

    @Test
    fun obtenerFechaHoy_devuelveMedianocheUTC() {
        val hoy = FechaUtil.obtenerFechaHoy()

        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
            timeInMillis = hoy
        }

        assertEquals(0, calendar.get(Calendar.HOUR_OF_DAY))
        assertEquals(0, calendar.get(Calendar.MINUTE))
        assertEquals(0, calendar.get(Calendar.SECOND))
        assertEquals(0, calendar.get(Calendar.MILLISECOND))
    }

    @Test
    fun esHoy_conFechaDeHoy_devuelveTrue() {
        val hoy = FechaUtil.obtenerFechaHoy()
        assertTrue(FechaUtil.esHoy(hoy))
    }

    @Test
    fun esHoy_conFechaDeAyer_devuelveFalse() {
        val ayer = FechaUtil.obtenerFechaHoy() - 24 * 60 * 60 * 1000L
        assertFalse(FechaUtil.esHoy(ayer))
    }

    @Test
    fun esHoy_conFechaFutura_devuelveFalse() {
        val mañana = FechaUtil.obtenerFechaHoy() + 24 * 60 * 60 * 1000L
        assertFalse(FechaUtil.esHoy(mañana))
    }

    @Test
    fun esHoy_conFechaCero_devuelveFalse() {
        assertFalse(FechaUtil.esHoy(0L))
    }

}
