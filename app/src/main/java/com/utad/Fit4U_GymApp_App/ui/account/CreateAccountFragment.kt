package com.utad.Fit4U_GymApp_App.ui.account

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.utad.Fit4U_GymApp_App.R
import com.utad.Fit4U_GymApp_App.databinding.FragmentCreateAccountBinding
import com.utad.Fit4U_GymApp_App.utils.Result
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CreateAccountFragment: Fragment(R.layout.fragment_create_account) {

    private var _binding: FragmentCreateAccountBinding? = null
    val binding:FragmentCreateAccountBinding?
        get() = _binding

    private val userViewModel: UserViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentCreateAccountBinding.bind(view)

        eventosRegistro()
        binding?.createAccountBtn?.setOnClickListener {
            val nombre = binding!!.userNameEdtTxt.text.toString()
            val email = binding!!.emailEditTxt.text.toString()
            val contrasena = binding!!.passwordEdtTxt.text.toString()
            val confirmarContrasena = binding!!.passwordReEnterEdtTxt.text.toString()

            userViewModel.crearUsuario(
                nombre.trim(),
                email.trim(),
                contrasena.trim(),
                confirmarContrasena.trim()
            )
        }
    }

    private fun eventosRegistro() = lifecycleScope.launch{
        userViewModel.registerState.collect { result ->
            when(result){
                is Result.Success -> {
                    esconderBarraProgeso()
                    Toast.makeText(requireContext(), "Cuenta correctamente creada", Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                }
                is Result.Error -> {
                    esconderBarraProgeso()
                    Toast.makeText(requireContext(), result.errorMessage, Toast.LENGTH_SHORT).show()
                }
                is Result.Loading -> {
                    mostrarBarraProgreso()
                }
            }

        }
    }

    private fun mostrarBarraProgreso(){
        binding?.createUserProgressBar?.isVisible = true
    }

    private fun esconderBarraProgeso(){
        binding?.createUserProgressBar?.isVisible = false
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}