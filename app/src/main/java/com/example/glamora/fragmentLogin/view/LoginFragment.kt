package com.example.glamora.fragmentLogin.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.example.glamora.R
import com.example.glamora.databinding.FragmentLoginBinding
import com.example.glamora.util.isNotShort
import com.example.glamora.util.isValidEmail

class LoginFragment : Fragment() {

    private lateinit var loginBinding: FragmentLoginBinding
    private var validEmail = false
    private var validPass = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        loginBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_login,container,false)

        return loginBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loginBinding.loginBtn.setOnClickListener{
            checkUserEmail()
            checkUserPassword()

            // handel logic here
            if(validEmail && validPass){

            }
        }


        loginBinding.signUp.setOnClickListener{
            findNavController().navigate(R.id.action_loginFragment_to_signUpFragment)
        }


    }


    private fun checkUserEmail(){

        if(isValidEmail(loginBinding.editEmail.text.toString()))
        {
            validEmail = true
        }
        else{
            loginBinding.errEmailTxt.visibility = View.VISIBLE

            loginBinding.editEmail.background = ContextCompat.getDrawable(requireContext(), R.drawable.button_background_err)

            val drawableIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_email_err)

            loginBinding.editEmail.setCompoundDrawablesWithIntrinsicBounds(drawableIcon, null, null, null)
        }
    }

    private fun checkUserPassword(){
        if(isNotShort(loginBinding.editPassword.text.toString())){
            validPass = true
        }
        else
        {
            loginBinding.errPassTxt.text = "Oops! Password Can't Be Empty"
            loginBinding.errPassTxt.visibility = View.VISIBLE

            loginBinding.editPassword.background = ContextCompat.getDrawable(requireContext(), R.drawable.button_background_err)

            val drawableIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_pass_err)

            loginBinding.editPassword.setCompoundDrawablesWithIntrinsicBounds(drawableIcon, null, null, null)
        }
    }

}