package com.utad.Fit4U_GymApp_App.data.local.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.util.*

@Entity
data class LocalNote(
    var noteTitle:String? = null,
    var desription:String? = null,
    var date:Long = System.currentTimeMillis(),
    var connected:Boolean = false,
    var locallyDeleted:Boolean = false,
    @PrimaryKey(autoGenerate = false)
    var noteId:String = UUID.randomUUID().toString()
): Serializable
