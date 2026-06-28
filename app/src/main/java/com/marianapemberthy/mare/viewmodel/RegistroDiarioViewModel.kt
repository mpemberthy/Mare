package com.marianapemberthy.mare.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope

import com.marianapemberthy.mare.data.MareDatabase
import com.marianapemberthy.mare.model.EmocionType
import com.marianapemberthy.mare.model.NivelEnergiaType
import com.marianapemberthy.mare.model.RegistroDiario
import com.marianapemberthy.mare.util.FechaUtil

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RegistroDiarioViewModel(application: Application) : AndroidViewModel(application) {

    private val TAG = "RegistroDiarioViewModel"
    private val database = MareDatabase.getInstance(application)
    private val dao = database.registroDiarioDao()

    val registroActual: Flow<RegistroDiario?> = dao.obtener()

    private val _emocionSeleccionada = MutableStateFlow<EmocionType?>(null)
    val emocionSeleccionada: StateFlow<EmocionType?> = _emocionSeleccionada

    private val _energiaSeleccionada = MutableStateFlow<NivelEnergiaType?>(null)
    val energiaSeleccionada: StateFlow<NivelEnergiaType?> = _energiaSeleccionada

    fun seleccionarEmocion(emocion: EmocionType) {
        _emocionSeleccionada.value = emocion
    }

    fun seleccionarEnergia(energia: NivelEnergiaType) {
        _energiaSeleccionada.value = energia
    }

    fun guardarRegistroFinal() {
        val emocion = _emocionSeleccionada.value
        val energia = _energiaSeleccionada.value

        if (emocion == null || energia == null) {
            Log.w(TAG, "guardarRegistroFinal() llamado sin emoción o energía seleccionada.")
            return
        }

        viewModelScope.launch {
            try {
                dao.insertar(
                    RegistroDiario(
                        id = 1,
                        emocion = emocion,
                        nivelEnergia = energia,
                        fecha = FechaUtil.obtenerFechaHoy()
                    )
                )
                _emocionSeleccionada.value = null
                _energiaSeleccionada.value = null
            } catch (e: Exception) {
                Log.e(TAG, "Error al guardar registro diario: ${e.message}")
            }
        }
    }
}
