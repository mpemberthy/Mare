package com.marianapemberthy.mare.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

import com.marianapemberthy.mare.model.Tarea
import com.marianapemberthy.mare.ui.components.BarraNavegacion
import com.marianapemberthy.mare.ui.components.TarjetaTarea
import com.marianapemberthy.mare.ui.theme.*

@Composable
fun PantallaTareasCompletadas(
    tareasCompletadas: List<Tarea>,
    onVolver: () -> Unit,
    onAgregarTarea: () -> Unit = {}
) {
    Scaffold(
        bottomBar = {
            BarraNavegacion(
                onNavInicio = onVolver,
                onNavCompletadas = null,
                onNavAgregarTarea = onAgregarTarea,
                mostrarBotonAgregar = true
            )
        }
    ) { paddingInterno ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Blanco)
                .padding(paddingInterno)
                .padding(24.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Tareas Completadas Hoy",
                style = MaterialTheme.typography.headlineLarge,
                color = Negro
            )

            Spacer(modifier = Modifier.height(24.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (tareasCompletadas.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 64.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Aún no has completado ninguna tarea hoy",
                            style = MaterialTheme.typography.bodyLarge,
                            color = GrisTerciario,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    tareasCompletadas.forEach { tarea ->
                        TarjetaTarea(
                            tarea = tarea,
                            onCompletar = {},
                            onEditar = {}
                        )
                    }
                }
            }
        }
    }
}
