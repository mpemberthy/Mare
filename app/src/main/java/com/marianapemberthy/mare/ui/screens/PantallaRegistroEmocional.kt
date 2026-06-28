package com.marianapemberthy.mare.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

import com.marianapemberthy.mare.R
import com.marianapemberthy.mare.data.emociones
import com.marianapemberthy.mare.ui.theme.*
import com.marianapemberthy.mare.model.EmocionType

@Composable
fun PantallaRegistroEmocional(
    onEmocionSeleccionada: (EmocionType) -> Unit = {}
) {
    var emocionSeleccionada by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Blanco)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
    ) {
        Text(
            text = "¿Cómo te sientes hoy?",
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center,
            color = Negro
        )

        Spacer(modifier = Modifier.height(8.dp))

        emociones.forEach { emocion ->
            val seleccionada = emocionSeleccionada == emocion.nombre
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(104.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        if (seleccionada) emocion.color.copy(alpha = 0.7f)
                        else emocion.color
                    )
                    .clickable {
                        emocionSeleccionada = emocion.nombre
                        onEmocionSeleccionada(emocion.tipo)
                    }
                    .padding(
                        start = 24.dp,
                        end = 24.dp,
                        top = 24.dp,
                        bottom = 24.dp
                    ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(47.dp)
                        .clip(CircleShape)
                        .background(Blanco),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(
                            id = when (emocion.nombre) {
                                "Feliz" -> R.drawable.happy
                                "Bien" -> R.drawable.good
                                "Neutral" -> R.drawable.neutral
                                "Triste" -> R.drawable.sad
                                else -> R.drawable.angry
                            }
                        ),
                        contentDescription = emocion.nombre,
                        tint = Color.Unspecified,
                        modifier = Modifier.size(32.dp)
                    )
                }
                Text(
                    text = emocion.nombre,
                    style = MaterialTheme.typography.bodySmall,
                    color = Negro
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun PantallaRegistroEmocionalPreview() {
    MareTheme {
        PantallaRegistroEmocional()
    }
}