package com.marianapemberthy.mare.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.marianapemberthy.mare.R
import com.marianapemberthy.mare.data.nivelesEnergia
import com.marianapemberthy.mare.model.NivelEnergiaType
import com.marianapemberthy.mare.model.Tarea
import com.marianapemberthy.mare.ui.theme.*
import com.marianapemberthy.mare.util.FechaUtil
import com.marianapemberthy.mare.model.EstadoTarea

import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaEdicionTarea(
    tarea: Tarea,
    onVolver: () -> Unit = {},
    onGuardarTarea: (Tarea) -> Unit = {},
    onEliminarTarea: (Tarea) -> Unit = {}
) {
    var nombre by remember { mutableStateOf(tarea.nombre) }
    var duracion by remember { mutableStateOf(tarea.duracion) }
    var fecha by remember { mutableStateOf(tarea.fecha) }
    var fechaSeleccionada by remember { mutableStateOf(true) }
    var nivelEnergia by remember { mutableStateOf<NivelEnergiaType?>(tarea.nivelEnergia) }
    var repetir by remember { mutableStateOf(tarea.repetir) }

    val duracionInicialTexto = if (tarea.enHoras) (tarea.duracion / 60).toString() else tarea.duracion.toString()
    var duracionInputText by remember { mutableStateOf(duracionInicialTexto) }
    var enHoras by remember { mutableStateOf(tarea.enHoras) }
    var isHoursSelected by remember { mutableStateOf(tarea.enHoras) }

    var showDatePicker by remember { mutableStateOf(false) }

    var diasSeleccionados by remember {
        mutableStateOf(
            if (tarea.diasRepeticion.isNotEmpty()) tarea.diasRepeticion.split(",").toSet() else setOf()
        )
    }

    var fechaFinRepeticion by remember { mutableStateOf(if (tarea.fechaFinRepeticion > 0L) tarea.fechaFinRepeticion else FechaUtil.obtenerFechaHoy() + 30L * 24 * 60 * 60 * 1000) }
    var showDatePickerFin by remember { mutableStateOf(false) }

    val formatoFecha = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Blanco)
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = { onEliminarTarea(tarea) },
                    modifier = Modifier
                        .size(47.dp)
                        .background(color = Color.Unspecified, shape = RoundedCornerShape(50))
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.trash),
                        contentDescription = "Eliminar",
                        tint = Rojo
                    )
                }
                IconButton(
                    onClick = {
                        if (nombre.isNotBlank() && nivelEnergia != null) {
                            val tareaActualizada = tarea.copy(
                                nombre = nombre,
                                duracion = duracion,
                                enHoras = enHoras,
                                fecha = fecha,
                                nivelEnergia = nivelEnergia ?: NivelEnergiaType.MEDIA,
                                repetir = repetir,
                                diasRepeticion = if (repetir) diasSeleccionados.joinToString(",") else "",
                                fechaFinRepeticion = if (repetir) fechaFinRepeticion else 0L
                            )
                            onGuardarTarea(tareaActualizada)
                        }
                    },
                    modifier = Modifier
                        .size(47.dp)
                        .background(color = Color.Unspecified, shape = RoundedCornerShape(50))
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.accept),
                        contentDescription = "Guardar",
                        tint = Color.Unspecified
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "¿Cómo se llama la tarea?",
            style = MaterialTheme.typography.bodySmall,
            color = Negro
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            placeholder = { Text("Escribe el nombre de la tarea", color = GrisTerciario) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            textStyle = MaterialTheme.typography.bodyMedium.copy(color = Negro),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = GrisClaro,
                focusedBorderColor = Primario
            )
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (nombre.isNotBlank()) {
            Text(
                text = "¿Cuánto tiempo necesitas?",
                style = MaterialTheme.typography.bodySmall,
                color = Negro
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                textStyle = MaterialTheme.typography.bodyMedium.copy(color = Negro),
                value = duracionInputText,
                onValueChange = { input ->
                    if (input.all { it.isDigit() }) {
                        duracionInputText = input
                        val valorEntero = input.toIntOrNull() ?: 0
                        duracion = if (isHoursSelected) valorEntero * 60 else valorEntero
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                prefix = {
                    Icon(
                        painter = painterResource(id = R.drawable.clock),
                        contentDescription = "Duración",
                        tint = Color.Unspecified,
                        modifier = Modifier.size(24.dp).padding(end = 4.dp)
                    )
                },
                trailingIcon = {
                    Row(
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .background(GrisClaroSecundario, shape = RoundedCornerShape(16.dp))
                            .padding(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp, 28.dp)
                                .background(
                                    if (isHoursSelected) Primario else Color.Transparent,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable {
                                    isHoursSelected = true
                                    enHoras = true
                                    val valorEntero = duracionInputText.toIntOrNull() ?: 0
                                    duracion = valorEntero * 60
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text("h", color = if (isHoursSelected) Blanco else GrisTerciario, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                        }
                        Box(
                            modifier = Modifier
                                .size(36.dp, 28.dp)
                                .background(
                                    if (!isHoursSelected) Primario else Color.Transparent,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable {
                                    isHoursSelected = false
                                    enHoras = false
                                    val valorEntero = duracionInputText.toIntOrNull() ?: 0
                                    duracion = valorEntero
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text("m", color = if (!isHoursSelected) Blanco else GrisTerciario, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = GrisClaro,
                    focusedBorderColor = Primario
                )
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (duracion != 0 && !repetir) {
            Text(
                text = "¿Para cuándo es?",
                style = MaterialTheme.typography.bodySmall,
                color = Negro
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                textStyle = MaterialTheme.typography.bodyMedium.copy(color = Negro),
                value = formatoFecha.format(Date(fecha)),
                onValueChange = {},
                readOnly = true,
                leadingIcon = {
                    Icon(painter = painterResource(id = R.drawable.calendar), contentDescription = "Calendario")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDatePicker = true },
                enabled = false,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    disabledBorderColor = GrisClaro,
                    disabledTextColor = Negro,
                    disabledLeadingIconColor = Primario
                )
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (fechaSeleccionada) {
            Text(
                text = "¿Qué nivel de energía necesitas para esta tarea?",
                style = MaterialTheme.typography.bodySmall,
                color = Negro
            )
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                nivelesEnergia.forEach { nivelItem ->
                    val isSelected = nivelEnergia == nivelItem.tipo
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) nivelItem.color else GrisClaro,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .background(
                                if (isSelected) nivelItem.color else Color.Transparent,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { nivelEnergia = nivelItem.tipo },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = nivelItem.nombre,
                            color = Negro,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (nivelEnergia != null) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "¿Se repite esta tarea?",
                    style = MaterialTheme.typography.bodySmall,
                    color = Negro
                )
                Switch(
                    checked = repetir,
                    onCheckedChange = { nuevoValor ->
                        repetir = nuevoValor
                        if (!nuevoValor) {
                            diasSeleccionados = setOf()
                            fechaFinRepeticion = FechaUtil.obtenerFechaHoy() + 30L * 24 * 60 * 60 * 1000
                        }
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Blanco,
                        checkedTrackColor = Primario,
                        uncheckedThumbColor = GrisClaro,
                        uncheckedTrackColor = GrisClaroSecundario
                    )
                )
            }

            if (repetir) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val diasSemana = listOf("D", "L", "M", "M", "J", "V", "S")
                    val diasKeys = listOf("Do", "Lu", "Ma", "Mi", "Ju", "Vi", "Sa")

                    diasSemana.forEachIndexed { index, dia ->
                        val key = diasKeys[index]
                        val diaSelected = diasSeleccionados.contains(key)
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(
                                    color = if (diaSelected) Primario else GrisClaroSecundario,
                                    shape = RoundedCornerShape(50)
                                )
                                .clip(RoundedCornerShape(50))
                                .clickable {
                                    diasSeleccionados = if (diaSelected) {
                                        diasSeleccionados - key
                                    } else {
                                        diasSeleccionados + key
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = dia,
                                color = if (diaSelected) Blanco else GrisPrimario,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "¿Cuando quieres que se acabe de repetir esta tarea?",
                    style = MaterialTheme.typography.bodySmall,
                    color = Negro
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = formatoFecha.format(Date(fechaFinRepeticion)),
                    onValueChange = {},
                    readOnly = true,
                    leadingIcon = {
                        Icon(Icons.Default.DateRange, contentDescription = "Fin Calendario", tint = Primario)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showDatePickerFin = true },
                    enabled = false,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledBorderColor = GrisClaro,
                        disabledTextColor = Negro,
                        disabledLeadingIconColor = Primario
                    )
                )
            }

            if (tarea.estado != EstadoTarea.HOY) {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        if (nombre.isNotBlank() && nivelEnergia != null) {
                            val tareaAsignadaHoy = tarea.copy(
                                nombre = nombre,
                                duracion = duracion,
                                enHoras = enHoras,
                                fecha = FechaUtil.obtenerFechaHoy(),
                                nivelEnergia = nivelEnergia ?: NivelEnergiaType.MEDIA,
                                repetir = repetir,
                                diasRepeticion = if (repetir) diasSeleccionados.joinToString(",") else "",
                                fechaFinRepeticion = if (repetir) fechaFinRepeticion else 0L,
                                estado = EstadoTarea.HOY
                            )
                            onGuardarTarea(tareaAsignadaHoy)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Primario)
                ) {
                    Text(
                        text = "Añadir a TAREAS PARA HOY",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Blanco
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = fecha,
            selectableDates = object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                    return utcTimeMillis >= FechaUtil.obtenerFechaHoy()
                }
            }
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { ms ->
                        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
                        calendar.timeInMillis = ms
                        calendar.set(Calendar.HOUR_OF_DAY, 0)
                        calendar.set(Calendar.MINUTE, 0)
                        calendar.set(Calendar.SECOND, 0)
                        calendar.set(Calendar.MILLISECOND, 0)

                        fecha = calendar.timeInMillis
                        fechaSeleccionada = true
                    }
                    showDatePicker = false
                }) {
                    Text("Aceptar", color = Primario)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancelar", color = GrisTerciario)
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showDatePickerFin) {
        val datePickerStateFin = rememberDatePickerState(
            initialSelectedDateMillis = fechaFinRepeticion
        )
        DatePickerDialog(
            onDismissRequest = { showDatePickerFin = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerStateFin.selectedDateMillis?.let { fechaFinRepeticion = it }
                    showDatePickerFin = false
                }) {
                    Text("Aceptar", color = Primario)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePickerFin = false }) {
                    Text("Cancelar", color = GrisTerciario)
                }
            }
        ) {
            DatePicker(state = datePickerStateFin)
        }
    }
}