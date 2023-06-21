package com.utad.Fit4U_GymApp_App.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.utad.Fit4U_GymApp_App.utils.Constants.EMAIL_KEY
import com.utad.Fit4U_GymApp_App.utils.Constants.JWT_TOKEN_KEY
import com.utad.Fit4U_GymApp_App.utils.Constants.NAME_KEY
import kotlinx.coroutines.flow.first

class SessionManager(val context:Context){

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("session_manager")

    suspend fun refrescarSesion(token:String, name:String, email:String) {
        val jwtTokenKey = stringPreferencesKey(JWT_TOKEN_KEY)
        val nameKey = stringPreferencesKey(NAME_KEY)
        val emailKey = stringPreferencesKey(EMAIL_KEY)
        context.dataStore.edit { preferences ->
            preferences[jwtTokenKey] = token
            preferences[nameKey] = name
            preferences[emailKey] = email
        }
    }

    suspend fun obtenerJwtToken():String? {
        val jwtTokenKey = stringPreferencesKey(JWT_TOKEN_KEY)
        val preferencias = context.dataStore.data.first()
        return preferencias[jwtTokenKey]
    }

    suspend fun obtenerUsuarioActual():String? {
        val nameKey = stringPreferencesKey(NAME_KEY)
        val preferencias = context.dataStore.data.first()
        return preferencias[nameKey]
    }

    suspend fun obtenerMailUsuarioActual():String? {
        val emailKey = stringPreferencesKey(EMAIL_KEY)
        val preferencias = context.dataStore.data.first()
        return preferencias[emailKey]
    }

    suspend fun logout(){
        context.dataStore.edit {
            it.clear()
        }
    }
}