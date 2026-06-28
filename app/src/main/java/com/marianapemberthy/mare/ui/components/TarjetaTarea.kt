package com.marianapemberthy.mare.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.marianapemberthy.mare.R
import com.marianapemberthy.mare.model.EstadoTarea
import com.marianapemberthy.mare.model.NivelEnergiaType
import com.marianapemberthy.mare.model.Tarea
import com.marianapemberthy.mare.ui.theme.*

import kotlinx.coroutines.delay

@Composable
fun TarjetaTarea(
    tarea: Tarea,
    onCompletar: (Tarea) -> Unit = {},
    onEditar: (Tarea) -> Unit = {}
) {
    val colorFondo = when (tarea.nivelEnergia) {
        NivelEnergiaType.ALTA -> EnergiaAlta
        NivelEnergiaType.MEDIA -> EnergiaMedia
        else -> EnergiaBaja
    }

    val yaCompletada = tarea.estado == EstadoTarea.COMPLETADA

    var iniciarAnimacionCheck by remember(tarea.id) { mutableStateOf(false) }

    var yaNotificado by remember(tarea.id) { mutableStateOf(false) }

    LaunchedEffect(iniciarAnimacionCheck) {
        if (iniciarAnimacionCheck && !yaNotificado) {
            yaNotificado = true
            delay(400)
            onCompletar(tarea)
        }
    }

    val isCompletada = yaCompletada || iniciarAnimacionCheck

    val colorCirculo by animateColorAsState(
        targetValue = if (isCompletada) colorFondo.copy(alpha = 0.5f) else Blanco,
        animationSpec = tween(durationMillis = 400),
        label = "colorCirculo"
    )

    val opacidadCheck by animateFloatAsState(
        targetValue = if (isCompletada) 1f else 0f,
        animationSpec = tween(durationMillis = 300),
        label = "opacidadCheck"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
            .clickable { onEditar(tarea) },
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = colorFondo)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(colorCirculo, shape = CircleShape)
                        .semantics { contentDescription = "Completar tarea" }
                        .clickable(enabled = !isCompletada) {
                            iniciarAnimacionCheck = true
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.completed),
                        contentDescription = "Completada",
                        tint = Color.Unspecified,
                        modifier = Modifier
                            .size(20.dp)
                            .alpha(opacidadCheck)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    Text(
                        text = tarea.nombre,
                        style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp),
                        color = Negro
                    )
                    val textoDuracion = if (tarea.enHoras) "${tarea.duracion / 60} h"
                    else "${tarea.duracion} min"
                    Text(
                        text = textoDuracion,
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 14.sp),
                        color = GrisPrimario
                    )
                }
            }
        }
    }
}