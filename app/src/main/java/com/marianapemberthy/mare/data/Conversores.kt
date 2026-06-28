package com.marianapemberthy.mare.data

import androidx.room.TypeConverter

import com.marianapemberthy.mare.model.EmocionType
import com.marianapemberthy.mare.model.EstadoTarea
import com.marianapemberthy.mare.model.NivelEnergiaType

class Conversores {
    @TypeConverter
    fun fromEmocionType(value: EmocionType): String = value.name

    @TypeConverter
    fun toEmocionType(value: String): EmocionType = EmocionType.valueOf(value)

    @TypeConverter
    fun fromNivelEnergiaType(value: NivelEnergiaType): String = value.name

    @TypeConverter
    fun toNivelEnergiaType(value: String): NivelEnergiaType = NivelEnergiaType.valueOf(value)

    @TypeConverter
    fun fromEstadoTarea(value: EstadoTarea): String = value.name

    @TypeConverter
    fun toEstadoTarea(value: String): EstadoTarea = EstadoTarea.valueOf(value)
}