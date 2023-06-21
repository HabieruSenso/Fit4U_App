package com.utad.Fit4U_GymApp_App.data.remote.models

// en Remote los variables tienen que tener los mismos nombres que en la api

data class User(
    val nombre:String? = null,
    val email:String,
    val contrasena:String
)
