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
import com.utad.Fit4U_GymApp_App.databinding.FragmentLoginBinding
import com.utad.Fit4U_GymApp_App.utils.Result
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment:Fragment(R.layout.fragment_login) {

    private var _binding: FragmentLoginBinding? = null
    val binding: FragmentLoginBinding?
        get() = _binding

    private val userViewModel: UserViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentLoginBinding.bind(view)

        eventosLogeo()

        binding?.loginBtn?.setOnClickListener {
            val nombre = binding!!.nameEditTxt.text.toString()
            val email = binding!!.emailEditTxt.text.toString()
            val contrasena = binding!!.passwordEdtTxt.text.toString()

            userViewModel.accederUsuario(
                nombre.trim(),
                email.trim(),
                contrasena.trim()
            )
        }

    }

    private fun eventosLogeo() = lifecycleScope.launch{
        userViewModel.loginState.collect { result ->
            when(result){
                is Result.Success -> {
                    esconderBarraProgreso()
                    Toast.makeText(requireContext(), "Cuenta creada correctamente", Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                }
                is Result.Error -> {
                    esconderBarraProgreso()
                    Toast.makeText(requireContext(), result.errorMessage, Toast.LENGTH_SHORT).show()
                }
                is Result.Loading -> {
                    mostrarBarraProgeso()
                }
            }

        }
    }

    private fun mostrarBarraProgeso(){
        binding?.loginProgressBar?.isVisible = true
    }

    private fun esconderBarraProgreso(){
        binding?.loginProgressBar?.isVisible = false
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}