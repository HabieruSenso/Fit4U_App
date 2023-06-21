package com.utad.Fit4U_GymApp_App.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.utad.Fit4U_GymApp_App.data.local.dao.NoteDao
import com.utad.Fit4U_GymApp_App.data.local.models.LocalNote

@Database(
    entities = [LocalNote::class],
    version = 1,
    exportSchema = false
)
abstract class NoteDatabase: RoomDatabase() {

    abstract fun obtenerNotaDao(): NoteDao
}