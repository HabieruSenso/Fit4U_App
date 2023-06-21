package com.utad.Fit4U_GymApp_App.data.remote.models

// en Remote los variables tienen que tener los mismos nombres que en la api

data class RemoteNote(
    val notaTitulo:String?,
    val descripcion:String?,
    val fecha:Long,
    val id:String
)
