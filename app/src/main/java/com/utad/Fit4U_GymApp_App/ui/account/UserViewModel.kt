package com.utad.Fit4U_GymApp_App.ui.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.utad.Fit4U_GymApp_App.data.remote.models.User
import com.utad.Fit4U_GymApp_App.repository.NoteRepo
import com.utad.Fit4U_GymApp_App.utils.Constants.MAXIMUM_PASSWORD_LENGTH
import com.utad.Fit4U_GymApp_App.utils.Constants.MINIMUM_PASSWORD_LENGTH
import com.utad.Fit4U_GymApp_App.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import java.util.regex.Pattern
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    val noteRepo: NoteRepo
): ViewModel() {

    private val _registerState = MutableSharedFlow<Result<String>>()
    val registerState:SharedFlow<Result<String>> = _registerState

    private val _loginState = MutableSharedFlow<Result<String>>()
    val loginState:SharedFlow<Result<String>> = _loginState

    private val _currentUserState = MutableSharedFlow<Result<User>>()
    val currentUserState:SharedFlow<Result<User>> = _currentUserState

    fun crearUsuario(
        nombre:String,
        email:String,
        contrasena:String,
        confirmarContrasena:String
    ) = viewModelScope.launch {
        _registerState.emit(Result.Loading())

        if(nombre.isEmpty() || email.isEmpty() || contrasena.isEmpty() || confirmarContrasena.isEmpty() || contrasena != confirmarContrasena){
            _registerState.emit(Result.Error("Algunos parametros estan vacios"))
            return@launch
        }

        if(!mailValido(email)){
            _registerState.emit(Result.Error("El email no es valido"))
            return@launch
        }

        if(!contrasenaValida(contrasena)){
            _registerState.emit(Result.Error("La contrseña deberia tener una longitud entre $MINIMUM_PASSWORD_LENGTH y $MAXIMUM_PASSWORD_LENGTH"))
            return@launch
        }

        val nuevoUsuario = User(
            nombre,
            email,
            contrasena
        )
        _registerState.emit(noteRepo.crearUsuario(nuevoUsuario))
    }

    fun accederUsuario(
        nombre:String,
        email:String,
        contrasena:String
    ) = viewModelScope.launch {
        _loginState.emit(Result.Loading())

        if(nombre.isEmpty() || email.isEmpty() || contrasena.isEmpty()){
            _loginState.emit(Result.Error("Algunos datos estan vacios"))
            return@launch
        }

        if(!mailValido(email)){
            _loginState.emit(Result.Error("El email no es valido"))
            return@launch
        }

        if(!contrasenaValida(contrasena)){
            _loginState.emit(Result.Error("La longitud de la contraseña deberia estar entre $MINIMUM_PASSWORD_LENGTH y $MAXIMUM_PASSWORD_LENGTH"))
            return@launch
        }

        val nuevoUsuario = User(
            nombre,
            email,
            contrasena
        )
        _loginState.emit(noteRepo.login(nuevoUsuario))
    }

    fun obtenerUsuarioActual()  = viewModelScope.launch{
        _currentUserState.emit(Result.Loading())
        _currentUserState.emit(noteRepo.obtenerUsuario())
    }

    fun logout() = viewModelScope.launch {
        val resultado = noteRepo.logout()
        if(resultado is Result.Success){
            obtenerUsuarioActual()
        }
    }

    private fun mailValido(email: String):Boolean {
        var regex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"
        val patron = Pattern.compile(regex)
        return (email.isNotEmpty() && patron.matcher(email).matches())
    }

    private fun contrasenaValida(password: String):Boolean {

        return (password.length in MINIMUM_PASSWORD_LENGTH..MAXIMUM_PASSWORD_LENGTH)
    }
}