package com.marianapemberthy.mare.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

import com.marianapemberthy.mare.R
import com.marianapemberthy.mare.ui.theme.*

@Composable
fun BarraNavegacion(
    onNavInicio: (() -> Unit)? = null,
    onNavCompletadas: (() -> Unit)? = null,
    onNavAgregarTarea: (() -> Unit)? = null,
    mostrarBotonAgregar: Boolean = true
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Blanco)
    ) {
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            thickness = 2.dp,
            color = Negro
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .padding(horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { onNavInicio?.invoke() },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.home),
                    contentDescription = "Inicio",
                    tint = if (onNavInicio == null) Primario else Negro
                )
            }

            Spacer(modifier = Modifier.width(24.dp))

            IconButton(
                onClick = { onNavCompletadas?.invoke() },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.completed),
                    contentDescription = "Tareas Completadas",
                    tint = if (onNavCompletadas == null) Primario else Negro
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            if (mostrarBotonAgregar) {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .background(Primario, shape = CircleShape)
                        .clickable { onNavAgregarTarea?.invoke() },
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.add),
                        contentDescription = "Agregar",
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}
