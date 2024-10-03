package com.example.glamora.fragmentSignup.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.example.glamora.R
import com.example.glamora.databinding.FragmentSignUpBinding
import com.example.glamora.util.isNotShort
import com.example.glamora.util.isPasswordEqualRePassword
import com.example.glamora.util.isValidEmail

class SignUpFragment : Fragment() {

    private lateinit var SignUpBinding : FragmentSignUpBinding

    private var validName = false
    private var validEmail = false
    private var validPass = false
    private var validRepass = false
    private var validPhone = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        SignUpBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_sign_up, container, false)
        return SignUpBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        SignUpBinding.signUpBtn.setOnClickListener{
            checkUserName()
            checkUserEmail()
            checkUserPassword()
            checkUserRePassword()
            checkUserPhone()

            // handel logic here
            if(validName && validEmail && validPass && validRepass && validPhone)
            {

            }


        }

        SignUpBinding.login.setOnClickListener{
            findNavController().popBackStack()
        }
    }


    private fun checkUserName(){

        if(isNotShort(SignUpBinding.editName.text.toString()))
        {
            validName = true
        }
        else{
            SignUpBinding.errNameTxt.visibility = View.VISIBLE

            SignUpBinding.editName.background = ContextCompat.getDrawable(requireContext(), R.drawable.button_background_err)

            val drawableIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_user_err)

            SignUpBinding.editName.setCompoundDrawablesWithIntrinsicBounds(drawableIcon, null, null, null)
        }
    }

    private fun checkUserEmail(){

        if(isValidEmail(SignUpBinding.editEmail.text.toString()))
        {
            validEmail = true
        }
        else{
            SignUpBinding.errEmailTxt.visibility = View.VISIBLE

            SignUpBinding.editEmail.background = ContextCompat.getDrawable(requireContext(), R.drawable.button_background_err)

            val drawableIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_email_err)

            SignUpBinding.editEmail.setCompoundDrawablesWithIntrinsicBounds(drawableIcon, null, null, null)
        }
    }

    private fun checkUserPassword(){
        if (!isNotShort(SignUpBinding.editPassword.text.toString())){
            SignUpBinding.errPassTxt.visibility = View.VISIBLE

            SignUpBinding.editPassword.background = ContextCompat.getDrawable(requireContext(), R.drawable.button_background_err)

            val drawableIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_pass_err)

            SignUpBinding.editPassword.setCompoundDrawablesWithIntrinsicBounds(drawableIcon, null, null, null)

        }

        else{
            validPass = true
        }
    }

    private fun checkUserRePassword(){
        if(isPasswordEqualRePassword(SignUpBinding.editPassword.text.toString(),SignUpBinding.editRepassword.text.toString()))
        {
            validRepass = true
        }
        else{
            SignUpBinding.errRepassTxt.visibility = View.VISIBLE
            SignUpBinding.editRepassword.background = ContextCompat.getDrawable(requireContext(), R.drawable.button_background_err)

            val drawableIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_pass_err)

            SignUpBinding.editRepassword.setCompoundDrawablesWithIntrinsicBounds(drawableIcon, null, null, null)

        }
    }

    private fun checkUserPhone(){
        if(isNotShort(SignUpBinding.editPhone.text.toString(),11)){
            validPhone = true
        }
        else{
            SignUpBinding.errPhoneTxt.visibility = View.VISIBLE

            SignUpBinding.editPhone.background = ContextCompat.getDrawable(requireContext(), R.drawable.button_background_err)

            val drawableIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_phone_err)

            SignUpBinding.editPhone.setCompoundDrawablesWithIntrinsicBounds(drawableIcon, null, null, null)
        }
    }

}