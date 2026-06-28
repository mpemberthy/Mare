package com.marianapemberthy.mare.util

import java.util.Calendar
import java.util.TimeZone
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

object FechaUtil {

    fun obtenerFechaHoy(): Long {
        val localCalendar = Calendar.getInstance()
        return Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
            set(Calendar.YEAR, localCalendar.get(Calendar.YEAR))
            set(Calendar.MONTH, localCalendar.get(Calendar.MONTH))
            set(Calendar.DAY_OF_MONTH, localCalendar.get(Calendar.DAY_OF_MONTH))
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    fun esHoy(fechaMillis: Long): Boolean {
        if (fechaMillis == 0L) return false
        val fechaSeleccionada = Instant.ofEpochMilli(fechaMillis)
            .atZone(ZoneOffset.UTC)
            .toLocalDate()
        val hoyUtc = Instant.ofEpochMilli(obtenerFechaHoy())
            .atZone(ZoneOffset.UTC)
            .toLocalDate()
        return fechaSeleccionada == hoyUtc
    }
}