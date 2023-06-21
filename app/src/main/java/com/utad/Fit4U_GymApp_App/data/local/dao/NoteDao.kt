package com.utad.Fit4U_GymApp_App.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.utad.Fit4U_GymApp_App.data.local.models.LocalNote
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarNota(note:LocalNote)


    @Query("SELECT * FROM LocalNote WHERE locallyDeleted =0 ORDER BY date DESC")
    fun obtenerTodasNotasOrdenFecha():Flow<List<LocalNote>>


    @Query("DELETE FROM LocalNote WHERE noteId=:noteId")
    suspend fun borrarNota(noteId:String)


    @Query("UPDATE LocalNote SET locallyDeleted = 1 WHERE noteId = :noteId")
    suspend fun borrarNotaLocalmente(noteId:String)


    @Query("SELECT * FROM LocalNote WHERE connected = 0")
    suspend fun obtenerTodasNotasLocales(): List<LocalNote>


    @Query("SELECT * FROM LocalNote WHERE locallyDeleted = 1")
    suspend fun obtenerTodasNotasBorradasLocales(): List<LocalNote>

}