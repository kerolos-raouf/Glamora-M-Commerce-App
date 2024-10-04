package com.example.glamora.fragmentSignup.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.glamora.R
import com.example.glamora.data.firebase.SignUpState
import com.example.glamora.databinding.FragmentSignUpBinding
import com.example.glamora.fragmentSignup.viewModel.SignUpViewModel
import com.example.glamora.util.isNotShort
import com.example.glamora.util.isPasswordEqualRePassword
import com.example.glamora.util.isValidEmail
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SignUpFragment : Fragment() {

    private lateinit var aBinding: FragmentSignUpBinding
    private val signUpViewModel: SignUpViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        aBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_sign_up, container, false)
        return aBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeViewModel()

        aBinding.signUpBtn.setOnClickListener {
            signUpViewModel.validateAndSignUp(
                aBinding.editName.text.toString(),
                aBinding.editEmail.text.toString(),
                aBinding.editPassword.text.toString(),
                aBinding.editRepassword.text.toString(),
                aBinding.editPhone.text.toString()
            )
        }

        aBinding.login.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch{
            signUpViewModel.signUpState.collect { state ->
                when (state) {
                    is SignUpState.Loading -> {
                        // Show loading state if needed
                    }
                    is SignUpState.Success -> {
                        Toast.makeText(requireContext(), "Sign-up successful!", Toast.LENGTH_SHORT).show()
                        findNavController().popBackStack()
                    }
                    is SignUpState.Error -> {
                        handleValidationError(state.message)
                    }
                    SignUpState.Idle -> Unit
                }
            }
        }
    }

    private fun handleValidationError(errorMessage: String) {
        // Show error based on the type of error
        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
        // Further UI adjustments for individual fields
        checkUserName()
        checkUserEmail()
        checkUserPassword()
        checkUserRePassword()
        checkUserPhone()
    }

    private fun checkUserName() {
        if (isNotShort(aBinding.editName.text.toString())) {
            aBinding.errNameTxt.visibility = View.GONE
        } else {
            aBinding.errNameTxt.visibility = View.VISIBLE
            aBinding.editName.setBackgroundError(requireContext(), R.drawable.button_background_err, R.drawable.ic_user_err)
        }
    }

    private fun checkUserEmail() {
        if (isValidEmail(aBinding.editEmail.text.toString())) {
            aBinding.errEmailTxt.visibility = View.GONE
        } else {
            aBinding.errEmailTxt.visibility = View.VISIBLE
            aBinding.editEmail.setBackgroundError(requireContext(), R.drawable.button_background_err, R.drawable.ic_email_err)
        }
    }

    private fun checkUserPassword() {
        if (isNotShort(aBinding.editPassword.text.toString())) {
            aBinding.errPassTxt.visibility = View.GONE
        } else {
            aBinding.errPassTxt.visibility = View.VISIBLE
            aBinding.editPassword.setBackgroundError(requireContext(), R.drawable.button_background_err, R.drawable.ic_pass_err)
        }
    }


    private fun checkUserRePassword() {
        if (isPasswordEqualRePassword(aBinding.editPassword.text.toString(), aBinding.editRepassword.text.toString())) {
            aBinding.errRepassTxt.visibility = View.GONE
        } else {
            aBinding.errRepassTxt.visibility = View.VISIBLE
            aBinding.editRepassword.setBackgroundError(requireContext(), R.drawable.button_background_err, R.drawable.ic_pass_err)
        }
    }

    private fun checkUserPhone() {
        if (isNotShort(aBinding.editPhone.text.toString(), 11)) {
            aBinding.errPhoneTxt.visibility = View.GONE
        } else {
            aBinding.errPhoneTxt.visibility = View.VISIBLE
            aBinding.editPhone.setBackgroundError(requireContext(), R.drawable.button_background_err, R.drawable.ic_phone_err)
        }
    }


    private fun EditText.setBackgroundError(context: Context, backgroundRes: Int, iconRes: Int) {
        this.background = ContextCompat.getDrawable(context, backgroundRes)
        val drawableIcon = ContextCompat.getDrawable(context, iconRes)
        this.setCompoundDrawablesWithIntrinsicBounds(drawableIcon, null, null, null)
    }
}
