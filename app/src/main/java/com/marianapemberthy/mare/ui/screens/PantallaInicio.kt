package com.marianapemberthy.mare.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextAlign

import com.marianapemberthy.mare.model.RegistroDiario
import com.marianapemberthy.mare.model.Tarea
import com.marianapemberthy.mare.viewmodel.InicioViewModel
import com.marianapemberthy.mare.ui.theme.*
import com.marianapemberthy.mare.ui.components.TarjetaTarea
import com.marianapemberthy.mare.ui.components.BarraNavegacion

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaInicio(
    viewModel: InicioViewModel,
    registroDiario: RegistroDiario?,
    onAgregarTarea: () -> Unit = {},
    onCompletarTarea: (Tarea) -> Unit = {},
    onEditarTarea: (Tarea) -> Unit,
    onNavegarACompletadas: () -> Unit
) {
    val tareasHoy by viewModel.tareasDeHoy.collectAsState(initial = emptyList())
    val tareasSugeridas by viewModel.obtenerTareasSugeridas(registroDiario).collectAsState(initial = emptyList())

    val localeEspanol = remember { Locale("es", "ES") }

    val calendarHoy = remember { Calendar.getInstance() }
    val nombreDia = remember { SimpleDateFormat("EEEE", localeEspanol).format(calendarHoy.time).replaceFirstChar { it.uppercase() } }
    val nombreMes = remember { SimpleDateFormat("MMMM", localeEspanol).format(calendarHoy.time).replaceFirstChar { it.uppercase() } }
    val anio = remember { calendarHoy.get(Calendar.YEAR) }

    val diasSemana = remember {
        val calSemana = Calendar.getInstance().apply {
            firstDayOfWeek = Calendar.SUNDAY
            set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
        }
        val hoy = Calendar.getInstance()

        (0..6).map { offset ->
            val cal = calSemana.clone() as Calendar
            cal.add(Calendar.DAY_OF_MONTH, offset)

            val inicialStr = cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, localeEspanol)
                ?.replace(".", "")
                ?.replaceFirstChar { it.uppercase() }
                ?.take(1) ?: ""

            val esHoyReal = cal.get(Calendar.DAY_OF_YEAR) == hoy.get(Calendar.DAY_OF_YEAR) &&
                    cal.get(Calendar.YEAR) == hoy.get(Calendar.YEAR)

            Triple(inicialStr, cal.get(Calendar.DAY_OF_MONTH), esHoyReal)
        }
    }

    Scaffold(
        bottomBar = {
            BarraNavegacion(
                onNavInicio = null,
                onNavCompletadas = onNavegarACompletadas,
                onNavAgregarTarea = onAgregarTarea,
                mostrarBotonAgregar = true
            )
        },
    ) { paddingInterno ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Blanco)
                .padding(paddingInterno)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$nombreMes $anio",
                    style = MaterialTheme.typography.titleLarge,
                    color = Negro
                )
            }

            Spacer(modifier = Modifier.height(30.dp))

            Text(
                text = nombreDia,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.displayLarge.copy(
                    fontSize = 48.sp,
                    fontWeight = FontWeight.ExtraBold
                ),
                color = Negro
            )

            Spacer(modifier = Modifier.height(25.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                diasSemana.forEach { (nombreDiaCorto, numeroDia, esHoy) ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .weight(1f)
                            .background(
                                color = if (esHoy) Primario else Color.Transparent,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(vertical = 8.dp)
                    ) {
                        Text(
                            text = nombreDiaCorto,
                            fontSize = 24.sp,
                            style = MaterialTheme.typography.titleLarge,
                            color = Negro
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = numeroDia.toString(),
                            fontSize = 24.sp,
                            style = MaterialTheme.typography.titleLarge,
                            color = Negro
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(25.dp))

            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                thickness = 2.dp,
                color = Negro
            )

            Spacer(modifier = Modifier.height(30.dp))

            Text(
                text = "TAREAS PARA HOY",
                style = MaterialTheme.typography.titleLarge,
                color = Negro
            )
            Spacer(modifier = Modifier.height(12.dp))

            if (tareasHoy.isEmpty()) {
                Text(
                    text = "No hay tareas asignadas para hoy",
                    style = MaterialTheme.typography.bodyLarge,
                    color = GrisTerciario,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            } else {
                tareasHoy.forEach { tarea ->
                    TarjetaTarea(tarea = tarea, onCompletar = { onCompletarTarea(it) }, onEditar = { onEditarTarea(it) })
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "TAREAS SUGERIDAS",
                style = MaterialTheme.typography.titleLarge,
                color = Negro
            )
            Spacer(modifier = Modifier.height(12.dp))

            if (tareasSugeridas.isEmpty()) {
                Text(
                    text = if (registroDiario == null) "Registra tu estado emocional para obtener sugerencias" else "No hay sugerencias para tu nivel de energía y emocion actual",
                    style = MaterialTheme.typography.bodyLarge,
                    color = GrisTerciario,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            } else {
                tareasSugeridas.forEach { tarea ->
                    TarjetaTarea(tarea = tarea, onCompletar = { onCompletarTarea(it) }, onEditar = { onEditarTarea(it) })
                }
            }
        }
    }
}
