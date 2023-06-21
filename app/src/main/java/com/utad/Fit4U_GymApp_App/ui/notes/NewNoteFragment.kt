package com.utad.Fit4U_GymApp_App.ui.notes

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.utad.Fit4U_GymApp_App.R
import com.utad.Fit4U_GymApp_App.databinding.FragmentNewNoteBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NewNoteFragment:Fragment(R.layout.fragment_new_note) {

    private var _binding: FragmentNewNoteBinding? = null
    val binding: FragmentNewNoteBinding?
        get() = _binding

    val noteViewModel: NoteViewModel by activityViewModels()
    val args:NewNoteFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentNewNoteBinding.bind(view)

        noteViewModel.notaVieja = args.note

        noteViewModel.notaVieja?.noteTitle?.let {
            binding?.newNoteTitleEditText?.setText(it)
        }

        noteViewModel.notaVieja?.desription?.let {
            binding?.newNoteDescriptionEditText?.setText(it)
        }
        binding?.date?.isVisible = noteViewModel.notaVieja != null
        noteViewModel.notaVieja?.date?.let {
            binding?.date?.text = noteViewModel.milliFecha(it)
        }

    }

    override fun onPause() {
        super.onPause()
        if(noteViewModel.notaVieja == null){
            crearNota()
        } else {
            modificarNota()
        }
    }

    private fun crearNota() {

        val tituloNota = binding?.newNoteTitleEditText?.text?.toString()?.trim()
        val descripcion = binding?.newNoteDescriptionEditText?.text?.toString()?.trim()

        if(tituloNota.isNullOrEmpty() && descripcion.isNullOrEmpty()){
            Toast.makeText(requireContext(), "Nota vacia", Toast.LENGTH_SHORT).show()
            return
        }
        noteViewModel.crearNota(tituloNota,descripcion)
    }
    private fun modificarNota() {

        val notaTitulo = binding?.newNoteTitleEditText?.text?.toString()?.trim()
        val descripcion = binding?.newNoteDescriptionEditText?.text?.toString()?.trim()

        if(notaTitulo.isNullOrEmpty() && descripcion.isNullOrEmpty()) {
            noteViewModel.borrarNota(noteViewModel.notaVieja!!.noteId)
            return
        }
        noteViewModel.modificarNota(notaTitulo,descripcion)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}