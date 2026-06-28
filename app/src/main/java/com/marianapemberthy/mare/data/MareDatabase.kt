package com.marianapemberthy.mare.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

import com.marianapemberthy.mare.model.RegistroDiario
import com.marianapemberthy.mare.model.Tarea

@Database(
    entities = [Tarea::class, RegistroDiario::class],
    version = 5,
    exportSchema = false
)
@TypeConverters(Conversores::class)
abstract class MareDatabase : RoomDatabase() {

    abstract fun tareaDao(): TareaDao
    abstract fun registroDiarioDao(): RegistroDiarioDao

    companion object {
        @Volatile
        private var INSTANCE: MareDatabase? = null

        fun getInstance(context: Context): MareDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    MareDatabase::class.java,
                    "mare_database"
                )
                    .fallbackToDestructiveMigration()
                    .build().also { INSTANCE = it }
            }
        }
        @androidx.annotation.VisibleForTesting
        fun setInstanceForTesting(database: MareDatabase?) {
            synchronized(this) {
                INSTANCE = database
            }
        }
    }
}