package com.utad.Fit4U_GymApp_App.repository

import com.utad.Fit4U_GymApp_App.data.local.models.LocalNote
import com.utad.Fit4U_GymApp_App.data.remote.models.User
import com.utad.Fit4U_GymApp_App.utils.Result
import kotlinx.coroutines.flow.Flow

interface NoteRepo {

    suspend fun crearUsuario(user:User):Result<String>
    suspend fun login(user:User):Result<String>
    suspend fun obtenerUsuario():Result<User>
    suspend fun logout():Result<String>

    suspend fun crearNota(note:LocalNote): Result<String>
    suspend fun modificarNota(note:LocalNote): Result<String>
    fun obtenerTodasNotas():Flow<List<LocalNote>>
    suspend fun obtenerTodasNotasServidor()

    suspend fun borrarNotas(noteId:String)
    suspend fun syncNotas()
}