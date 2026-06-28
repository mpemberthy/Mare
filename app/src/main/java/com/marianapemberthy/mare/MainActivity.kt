package com.marianapemberthy.mare

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

import com.marianapemberthy.mare.ui.screens.PantallaCrearTarea
import com.marianapemberthy.mare.ui.screens.PantallaInicio
import com.marianapemberthy.mare.ui.screens.PantallaRegistroEmocional
import com.marianapemberthy.mare.ui.screens.PantallaRegistroEnergia
import com.marianapemberthy.mare.ui.screens.PantallaEdicionTarea
import com.marianapemberthy.mare.ui.screens.PantallaTareasCompletadas
import com.marianapemberthy.mare.ui.theme.MareTheme
import com.marianapemberthy.mare.viewmodel.InicioViewModel
import com.marianapemberthy.mare.viewmodel.RegistroDiarioViewModel
import com.marianapemberthy.mare.viewmodel.TareaViewModel
import com.marianapemberthy.mare.util.FechaUtil
import com.marianapemberthy.mare.ui.theme.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        windowInsetsController.hide(WindowInsetsCompat.Type.navigationBars())

        setContent {
            MareTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val inicioViewModel: InicioViewModel = viewModel()
                    val registroViewModel: RegistroDiarioViewModel = viewModel()
                    val tareaViewModel: TareaViewModel = viewModel()

                    val registroDiarioState = inicioViewModel.registroHoy.collectAsState(initial = null)
                    val registroDiario = registroDiarioState.value
                    val fechaHoy = FechaUtil.obtenerFechaHoy()
                    val rutaInicial = if (registroDiario != null && registroDiario.fecha == fechaHoy) {
                        "inicio"
                    } else {
                        "registro_emocional"
                    }

                    var estaCargando by remember { mutableStateOf(true) }
                    LaunchedEffect(registroDiario) { kotlinx.coroutines.delay(300); estaCargando = false }
                    if (estaCargando) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                        }
                    } else {
                        NavHost(
                            navController = navController,
                            startDestination = rutaInicial
                        ) {
                            composable("inicio") {
                                PantallaInicio(
                                    viewModel = inicioViewModel,
                                    registroDiario = registroDiario,
                                    onAgregarTarea = { navController.navigate("crear_tarea") },
                                    onCompletarTarea = { tarea -> tareaViewModel.completarTarea(tarea) },
                                    onEditarTarea = { tarea -> navController.navigate("editar_tarea/${tarea.id}") },
                                    onNavegarACompletadas = { navController.navigate("tareas_completadas") }
                                )
                            }
                            composable("registro_emocional") {
                                PantallaRegistroEmocional(
                                    onEmocionSeleccionada = { emocion ->
                                        registroViewModel.seleccionarEmocion(emocion)
                                        navController.navigate("registro_energia")
                                    }
                                )
                            }
                            composable("registro_energia") {
                                PantallaRegistroEnergia(
                                    onNivelSeleccionado = { energia ->
                                        registroViewModel.seleccionarEnergia(energia)
                                        registroViewModel.guardarRegistroFinal()
                                        navController.navigate("inicio") {
                                            popUpTo("registro_emocional") { inclusive = true }
                                        }
                                    }
                                )
                            }
                            composable("crear_tarea") {
                                PantallaCrearTarea(
                                    onVolver = { navController.popBackStack() },
                                    onGuardarTarea = { tarea ->
                                        tareaViewModel.crearTarea(tarea)
                                        navController.popBackStack()
                                    }
                                )
                            }
                            composable("editar_tarea/{tareaId}") { backStackEntry ->
                                val tareaIdStr = backStackEntry.arguments?.getString("tareaId")
                                val tareaId = tareaIdStr?.toLongOrNull() ?: 0L

                                LaunchedEffect(tareaId) {
                                    tareaViewModel.cargarTareaParaEdicion(tareaId)
                                }

                                val tareaEnEdicion = tareaViewModel.tareaEnEdicion

                                if (tareaEnEdicion != null) {
                                    PantallaEdicionTarea(
                                        tarea = tareaEnEdicion,
                                        onVolver = {
                                            tareaViewModel.limpiarTareaEnEdicion()
                                            navController.popBackStack()
                                        },
                                        onGuardarTarea = { tareaActualizada ->
                                            tareaViewModel.modificarTarea(tareaActualizada)
                                            tareaViewModel.limpiarTareaEnEdicion()
                                            navController.popBackStack()
                                        },
                                        onEliminarTarea = { tareaAEliminar ->
                                            tareaViewModel.eliminarTarea(tareaAEliminar)
                                            tareaViewModel.limpiarTareaEnEdicion()
                                            navController.popBackStack()
                                        }
                                    )
                                } else {
                                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                        CircularProgressIndicator(color = Primario)
                                    }
                                }
                            }
                            composable("tareas_completadas") {
                                val tareasCompletadas by inicioViewModel.tareasCompletadasHoy.collectAsState(initial = emptyList())
                                PantallaTareasCompletadas(
                                    tareasCompletadas = tareasCompletadas,
                                    onVolver = { navController.popBackStack() },
                                    onAgregarTarea = { navController.navigate("crear_tarea") }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
