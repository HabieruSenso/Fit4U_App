package com.utad.Fit4U_GymApp_App.data.remote

import com.utad.Fit4U_GymApp_App.data.remote.models.RemoteNote
import com.utad.Fit4U_GymApp_App.data.remote.models.SimpleResponse
import com.utad.Fit4U_GymApp_App.data.remote.models.User
import com.utad.Fit4U_GymApp_App.utils.Constants.API_VERSION
import retrofit2.http.*

interface NoteApi {

    @Headers("Content-Type: application/json")
    @POST("$API_VERSION/usuarios/registro")
    suspend fun crearCuenta(
        @Body user:User
    ): SimpleResponse

    @Headers("Content-Type: application/json")
    @POST("$API_VERSION/usuarios/login")
    suspend fun login(
        @Body user:User
    ): SimpleResponse

    // == NOTES ==

    @Headers("Content-Type: application/json")
    @POST("$API_VERSION/notas/crear")
    suspend fun crearNota(
        @Header("Authorization") token:String,
        @Body note:RemoteNote
    ): SimpleResponse

    @Headers("Content-Type: application/json")
    @GET("$API_VERSION/notas")
    suspend fun obtenerTodasNotas(
        @Header("Authorization") token:String
    ): List<RemoteNote>

    @Headers("Content-Type: application/json")
    @POST("$API_VERSION/notas/modificar")
    suspend fun modificarNota(
        @Header("Authorization") token:String,
        @Body note:RemoteNote
    ): SimpleResponse

    @Headers("Content-Type: application/json")
    @DELETE("$API_VERSION/notas/borrar")
    suspend fun borrarNota(
        @Header("Authorization") token:String,
        @Query("id") noteId:String
    ): SimpleResponse
}