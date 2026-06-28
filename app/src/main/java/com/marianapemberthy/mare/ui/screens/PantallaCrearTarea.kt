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
import androidx.compose.material.icons.filled.ArrowDropDown
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

import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaCrearTarea(
    onVolver: () -> Unit = {},
    onGuardarTarea: (Tarea) -> Unit = {}
) {
    var nombre by remember { mutableStateOf("") }
    var duracion by remember { mutableStateOf(0) }
    var fecha by remember { mutableStateOf(FechaUtil.obtenerFechaHoy()) }
    var fechaSeleccionada by remember { mutableStateOf(false) }
    var nivelEnergia by remember { mutableStateOf<NivelEnergiaType?>(null) }
    var repetir by remember { mutableStateOf(false) }

    var duracionInputText by remember { mutableStateOf("") }
    var enHoras by remember { mutableStateOf(true) }
    var isHoursSelected by remember { mutableStateOf(true) }
    var showDatePicker by remember { mutableStateOf(false) }
    var diasSeleccionados by remember { mutableStateOf(setOf<String>()) }

    var fechaFinRepeticion by remember { mutableStateOf(FechaUtil.obtenerFechaHoy() + 30L * 24 * 60 * 60 * 1000) }
    var showDatePickerFin by remember { mutableStateOf(false) }

    var dropdownExpandido by remember { mutableStateOf(false) }

    val formatoFecha = remember {
        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
    }
    val puedeGuardar = nombre.isNotBlank() && nivelEnergia != null && (fechaSeleccionada || repetir)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Blanco)
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = onVolver,
                modifier = Modifier
                    .size(47.dp)
                    .background(color = Color.Unspecified, shape = RoundedCornerShape(50))
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.close),
                    contentDescription = "Cancelar",
                    tint = Color.Unspecified
                )
            }

            IconButton(
                onClick = {
                    if (puedeGuardar) {
                        val fechaFinal = if (repetir) FechaUtil.obtenerFechaHoy() else fecha
                        val nuevaTarea = Tarea(
                            nombre = nombre,
                            duracion = duracion,
                            enHoras = enHoras,
                            fecha = fechaFinal,
                            nivelEnergia = nivelEnergia ?: NivelEnergiaType.MEDIA,
                            iconoRes = 0,
                            repetir = repetir,
                            diasRepeticion = if (repetir) diasSeleccionados.joinToString(",") else "",
                            fechaFinRepeticion = if (repetir) fechaFinRepeticion else 0L
                        )
                        onGuardarTarea(nuevaTarea)
                    }
                },
                modifier = Modifier
                    .size(47.dp)
                    .background(color = Color.Unspecified, shape = RoundedCornerShape(50))
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.accept),
                    contentDescription = "Guardar",
                    tint = if (puedeGuardar) Color.Unspecified else GrisClaro
                )
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

        if (nombre.isNotBlank()) {
            Spacer(modifier = Modifier.height(24.dp))
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
                        modifier = Modifier
                            .size(24.dp)
                            .padding(end = 4.dp)
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
                            Text(
                                "h",
                                color = if (isHoursSelected) Blanco else GrisTerciario,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                            )
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
                            Text(
                                "m",
                                color = if (!isHoursSelected) Blanco else GrisTerciario,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                            )
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

        if (duracion != 0 && !repetir) {
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "¿Para cuándo es?",
                style = MaterialTheme.typography.bodySmall,
                color = Negro
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                textStyle = MaterialTheme.typography.bodyMedium.copy(color = Negro),
                value = if (fechaSeleccionada) formatoFecha.format(Date(fecha)) else "",
                onValueChange = {},
                placeholder = { Text("Selecciona una fecha", color = GrisTerciario) },
                readOnly = true,
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.calendar),
                        contentDescription = "Calendario",
                        tint = Primario
                    )
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

        if (fechaSeleccionada || (repetir && duracion != 0)) {
            Spacer(modifier = Modifier.height(24.dp))
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

        if (nivelEnergia != null) {
            Spacer(modifier = Modifier.height(24.dp))
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
                        } else {
                            fechaSeleccionada = false
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
                    text = "¿Cuándo quieres que se acabe de repetir esta tarea?",
                    style = MaterialTheme.typography.bodySmall,
                    color = Negro
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = formatoFecha.format(Date(fechaFinRepeticion)),
                    onValueChange = {},
                    readOnly = true,
                    leadingIcon = {
                        Icon(
                            Icons.Default.DateRange,
                            contentDescription = "Fin Calendario",
                            tint = Primario
                        )
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
                        fecha = ms
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
            initialSelectedDateMillis = fechaFinRepeticion,
            selectableDates = object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                    return utcTimeMillis >= FechaUtil.obtenerFechaHoy()
                }
            }
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

@Preview(showBackground = true, widthDp = 360, heightDp = 950)
@Composable
fun PreviewPantallaCrearTarea() {
    MareTheme {
        PantallaCrearTarea()
    }
}