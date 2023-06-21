package com.utad.Fit4U_GymApp_App.repository

import com.utad.Fit4U_GymApp_App.data.local.dao.NoteDao
import com.utad.Fit4U_GymApp_App.data.local.models.LocalNote
import com.utad.Fit4U_GymApp_App.data.remote.NoteApi
import com.utad.Fit4U_GymApp_App.data.remote.models.RemoteNote
import com.utad.Fit4U_GymApp_App.data.remote.models.User
import com.utad.Fit4U_GymApp_App.utils.Result
import com.utad.Fit4U_GymApp_App.utils.SessionManager
import com.utad.Fit4U_GymApp_App.utils.networkConexion
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class NoteRepoImpl @Inject constructor(
    val noteApi: NoteApi,
    val noteDao: NoteDao,
    val sessionManager: SessionManager
):NoteRepo {

    override suspend fun syncNotas() {
        try {
            sessionManager.obtenerJwtToken() ?: return
            if (!networkConexion(sessionManager.context)) {
                return
            }

            val notasBorradasLocalmente = noteDao.obtenerTodasNotasBorradasLocales()
            notasBorradasLocalmente.forEach {
                borrarNotas(it.noteId)
            }

            val notasNoConectadas = noteDao.obtenerTodasNotasLocales()
            notasNoConectadas.forEach {
                crearNota(it)
            }

            val notasNoModificadas = noteDao.obtenerTodasNotasLocales()
            notasNoModificadas.forEach {
                modificarNota(it)
            }

        } catch (e:Exception){
            e.printStackTrace()
        }
    }

    override suspend fun borrarNotas(noteId: String) {
        try{
            noteDao.borrarNotaLocalmente(noteId)
            val token = sessionManager.obtenerJwtToken() ?: kotlin.run {
                noteDao.borrarNota(noteId)
                return
            }
            if (!networkConexion(sessionManager.context)) {
                return
            }

            val respuesta = noteApi.borrarNota(
                "Bearer $token",
                noteId
            )

            if(respuesta.success){
                noteDao.borrarNota(noteId)
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    override fun obtenerTodasNotas(): Flow<List<LocalNote>> = noteDao.obtenerTodasNotasOrdenFecha()

    override suspend fun obtenerTodasNotasServidor() {
        try{
            val token = sessionManager.obtenerJwtToken() ?: return
            if (!networkConexion(sessionManager.context)) {
                return
            }
            val result = noteApi.obtenerTodasNotas("Bearer $token")
            result.forEach { remoteNote ->
                noteDao.insertarNota(
                    LocalNote(
                        noteTitle = remoteNote.notaTitulo,
                        desription = remoteNote.descripcion,
                        date = remoteNote.fecha,
                        connected = true,
                        noteId = remoteNote.id
                    )
                )
            }
        }catch (e:Exception){
            e.printStackTrace()
        }

    }

    override suspend fun crearNota(note: LocalNote): Result<String> {
        try {
            noteDao.insertarNota(note)
            val token = sessionManager.obtenerJwtToken()
                ?: return Result.Success("Nota guardada en la base de datos local")
            if(!networkConexion(sessionManager.context)){
                return Result.Error("Sin conexion a internet")
            }

            val resultado = noteApi.crearNota(
                "Bearer $token",
                RemoteNote(
                    notaTitulo = note.noteTitle,
                    descripcion = note.desription,
                    fecha = note.date,
                    id = note.noteId
                )
            )

            return if(resultado.success){
                noteDao.insertarNota(note.also { it.connected = true })
                Result.Success("Nota guardada satiscatoriamente")
            } else {
                Result.Error(resultado.message)
            }
        } catch (e:Exception){
            e.printStackTrace()
            return Result.Error(e.message ?: "Se vienen cositas, no se guardo bien")
        }

    }

    override suspend fun modificarNota(note: LocalNote): Result<String> {
        try {
            noteDao.insertarNota(note)
            val token = sessionManager.obtenerJwtToken()
                ?: return Result.Success("Nota actualizada en la base de datos local")

            if(!networkConexion(sessionManager.context)){
                return Result.Error("Sin conexion a internet")
            }

            val resultado = noteApi.modificarNota(
                "Bearer $token",
                RemoteNote(
                    notaTitulo = note.noteTitle,
                    descripcion = note.desription,
                    fecha = note.date,
                    id = note.noteId
                )
            )

            return if(resultado.success){
                noteDao.insertarNota(note.also { it.connected = true })
                Result.Success("Nota actualizada correctamente")
            } else {
                Result.Error(resultado.message)
            }
        } catch (e:Exception){
            e.printStackTrace()
            return Result.Error(e.message ?: "Se vienen cositas, no se puede actualizar")
        }
    }

    override suspend fun crearUsuario(user: User): Result<String> {

        return try {
            if(!networkConexion(sessionManager.context)){
                Result.Error<String>("Sin conexion a internet")
            }

            val resultado = noteApi.crearCuenta(user)
            if(resultado.success){
                sessionManager.refrescarSesion(resultado.message,user.nombre ?:"",user.email)
                Result.Success("Usuario creado correctamente")
            } else {
                Result.Error<String>(resultado.message)
            }
        }catch (e:Exception) {
            e.printStackTrace()
            Result.Error<String>(e.message ?: "Algunos porblemas han ocurrido, el usuario no se ha creado correctamente")
        }

    }

    override suspend fun login(user: User): Result<String> {
        return try {
            if(!networkConexion(sessionManager.context)){
                Result.Error<String>("Sin conexion a internet")
            }

            val resultado = noteApi.login(user)
            if(resultado.success){
                sessionManager.refrescarSesion(resultado.message,user.nombre ?:"",user.email)
                obtenerTodasNotasServidor()
                Result.Success("Loggeo Correctamente")
            } else {
                Result.Error<String>(resultado.message)
            }
        }catch (e:Exception) {
            e.printStackTrace()
            Result.Error<String>(e.message ?: "Se vienen cositas, no se loggeo correctamente")
        }
    }

    override suspend fun obtenerUsuario(): Result<User> {
        return try {
            val nombre = sessionManager.obtenerUsuarioActual()
            val email = sessionManager.obtenerMailUsuarioActual()
            if(nombre == null || email == null){
                Result.Error<User>("Usuario no Loggeado")
            }
            Result.Success(User(nombre,email!!,""))
        } catch (e:Exception){
            e.printStackTrace()
            Result.Error(e.message ?: "Se vienen cositas, el usuario no puede acceder")
        }
    }

    override suspend fun logout(): Result<String> {
        return try {
            sessionManager.logout()
            Result.Success("EL usuario ha salido del servicio correctamente")
        } catch (e:Exception){
            e.printStackTrace()
            Result.Error(e.message ?: "Se vienen cositas, el usuario no ha podido salir de manera correcta")
        }
    }
}