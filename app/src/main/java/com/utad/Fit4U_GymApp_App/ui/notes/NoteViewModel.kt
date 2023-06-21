package com.utad.Fit4U_GymApp_App.ui.notes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.utad.Fit4U_GymApp_App.data.local.models.LocalNote
import com.utad.Fit4U_GymApp_App.repository.NoteRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class NoteViewModel @Inject constructor(
    val notaRepo: NoteRepo
):ViewModel() {

    val notas = notaRepo.obtenerTodasNotas()
    var notaVieja: LocalNote? = null
    var buscarQuery: String = ""

    fun syncNotas(
        onDone: (()->Unit)? = null
    ) = viewModelScope.launch{

        notaRepo.syncNotas()
        onDone?.invoke()
    }

    fun crearNota(
        noteTitle:String?,
        description:String?
    ) = viewModelScope.launch(Dispatchers.IO){
        val notaLocal = LocalNote(
            noteTitle = noteTitle,
            desription = description
        )
        notaRepo.crearNota(notaLocal)
    }

    fun borrarNota(
        noteId:String
    ) = viewModelScope.launch {
        notaRepo.borrarNotas(noteId)
    }

    fun deshacerBorrar(
        note:LocalNote
    ) = viewModelScope.launch {
        notaRepo.crearNota(note)
    }

    fun modificarNota(
        noteTitle:String?,
        description:String?
    ) = viewModelScope.launch(Dispatchers.IO) {

        if(noteTitle == notaVieja?.noteTitle && description == notaVieja?.desription && notaVieja?.connected == true){
            return@launch
        }

        val nota = LocalNote(
            noteTitle = noteTitle,
            desription = description,
            noteId = notaVieja!!.noteId
        )
        notaRepo.modificarNota(nota)
    }

    fun milliFecha(time:Long):String {
        val fecha = Date(time)
        val formatoSimpleFecha = SimpleDateFormat("hh:mm a | MMM d, yyyy", Locale.getDefault())
        return formatoSimpleFecha.format(fecha)
    }
}