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
import com.example.glamora.databinding.FragmentSignUpBinding
import com.example.glamora.fragmentSignup.viewModel.SignUpViewModel
import com.example.glamora.util.State
import com.example.glamora.util.isNotShort
import com.example.glamora.util.isPasswordEqualRePassword
import com.example.glamora.util.isValidEmail
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SignUpFragment : Fragment() {

    private lateinit var signUpBinding: FragmentSignUpBinding
    private val signUpViewModel: SignUpViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        signUpBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_sign_up, container, false)
        return signUpBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeViewModel()

        signUpBinding.signUpBtn.setOnClickListener {
            resetErrors()

            signUpViewModel.validateAndSignUp(
                signUpBinding.editName.text.toString(),
                signUpBinding.editEmail.text.toString(),
                signUpBinding.editPassword.text.toString(),
                signUpBinding.editRepassword.text.toString(),
                signUpBinding.editPhone.text.toString()
            )
        }

        signUpBinding.login.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            signUpViewModel.signUpState.collect { state ->
                when (state) {
                    is State.Loading -> {
                        signUpBinding.progressBar.visibility = View.VISIBLE
                    }

                    is State.Success -> {
                        signUpBinding.progressBar.visibility = View.GONE

                        Toast.makeText(requireContext(), "Sign-up successful!", Toast.LENGTH_SHORT)
                            .show()

                        val userEmail = signUpBinding.editEmail.text.toString()
                        val userPassword = signUpBinding.editPassword.text.toString()

                        val bundle = Bundle().apply {
                            putString("userEmail", userEmail)
                            putString("userPassword", userPassword)
                        }

                        findNavController().navigate(R.id.action_signUpFragment_to_loginFragment , bundle)

                        //findNavController().popBackStack()//action_signUpFragment_to_loginFragment
                    }

                    is State.Error -> {
                        signUpBinding.progressBar.visibility = View.GONE
                        handleValidationError(state.message)
                    }

                    null -> {}
                }
            }
        }
    }

    private fun resetErrors() {
        signUpBinding.errNameTxt.visibility = View.GONE
        signUpBinding.editName.setBackgroundError(
            requireContext(),
            R.drawable.button_background,
            R.drawable.ic_user
        )

        signUpBinding.errEmailTxt.visibility = View.GONE
        signUpBinding.editEmail.setBackgroundError(
            requireContext(),
            R.drawable.button_background,
            R.drawable.ic_email
        )

        signUpBinding.errPassTxt.visibility = View.GONE
        signUpBinding.editPassword.setBackgroundError(
            requireContext(),
            R.drawable.button_background,
            R.drawable.ic_pass
        )

        signUpBinding.errRepassTxt.visibility = View.GONE
        signUpBinding.editRepassword.setBackgroundError(
            requireContext(),
            R.drawable.button_background,
            R.drawable.ic_pass
        )


        signUpBinding.errPhoneTxt.visibility = View.GONE
        signUpBinding.editPhone.setBackgroundError(
            requireContext(),
            R.drawable.button_background,
            R.drawable.ic_phone
        )


    }

    private fun handleValidationError(errorMessage: String) {
        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
        when (errorMessage) {
            "Phone has already been taken" -> {
                signUpBinding.errPhoneTxt.text = getString(R.string.err_phone_msg2)
                signUpBinding.errPhoneTxt.visibility = View.VISIBLE
                signUpBinding.editPhone.setBackgroundError(
                    requireContext(),
                    R.drawable.button_background_err,
                    R.drawable.ic_phone_err
                )

            }
            "InValid phone number" -> {
                signUpBinding.errPhoneTxt.text = getString(R.string.err_phone_msg)
                signUpBinding.errPhoneTxt.visibility = View.VISIBLE
                signUpBinding.editPhone.setBackgroundError(
                    requireContext(),
                    R.drawable.button_background_err,
                    R.drawable.ic_phone_err
                )

            }

            "Email has already been taken" -> {
                signUpBinding.errEmailTxt.text = getString(R.string.err_email_msg2)
                signUpBinding.errEmailTxt.visibility = View.VISIBLE
                signUpBinding.editEmail.setBackgroundError(
                    requireContext(),
                    R.drawable.button_background_err,
                    R.drawable.ic_email_err
                )
            }

            else -> {
                checkUserName()
                signUpBinding.errEmailTxt.text = getString(R.string.err_email_msg)
                checkUserEmail()
                checkUserPassword()
                checkUserRePassword()
                signUpBinding.errPhoneTxt.text = getString(R.string.err_phone_msg)
                checkUserPhone()
            }
        }

    }

    private fun checkUserName() {
        if (!isNotShort(signUpBinding.editName.text.toString())) {
            signUpBinding.errNameTxt.visibility = View.VISIBLE
            signUpBinding.editName.setBackgroundError(
                requireContext(),
                R.drawable.button_background_err,
                R.drawable.ic_user_err
            )
        }
    }

    private fun checkUserEmail() {
        if (!isValidEmail(signUpBinding.editEmail.text.toString())) {
            signUpBinding.errEmailTxt.visibility = View.VISIBLE
            signUpBinding.editEmail.setBackgroundError(
                requireContext(),
                R.drawable.button_background_err,
                R.drawable.ic_email_err
            )
        }
    }

    private fun checkUserPassword() {
        if (!isNotShort(signUpBinding.editPassword.text.toString())) {
            signUpBinding.errPassTxt.visibility = View.VISIBLE
            signUpBinding.editPassword.setBackgroundError(
                requireContext(),
                R.drawable.button_background_err,
                R.drawable.ic_pass_err
            )
        }
    }


    private fun checkUserRePassword() {
        if (!isPasswordEqualRePassword(
                signUpBinding.editPassword.text.toString(),
                signUpBinding.editRepassword.text.toString()

            )
        ) {
            signUpBinding.errRepassTxt.visibility = View.VISIBLE
            signUpBinding.editRepassword.setBackgroundError(
                requireContext(),
                R.drawable.button_background_err,
                R.drawable.ic_pass_err
            )
        }
    }

    private fun checkUserPhone() {
        if (!isNotShort(signUpBinding.editPhone.text.toString(), 11)) {
            signUpBinding.errPhoneTxt.visibility = View.VISIBLE
            signUpBinding.editPhone.setBackgroundError(
                requireContext(),
                R.drawable.button_background_err,
                R.drawable.ic_phone_err
            )
        }
    }


    private fun EditText.setBackgroundError(context: Context, backgroundRes: Int, iconRes: Int) {
        this.background = ContextCompat.getDrawable(context, backgroundRes)
        val drawableIcon = ContextCompat.getDrawable(context, iconRes)
        this.setCompoundDrawablesWithIntrinsicBounds(drawableIcon, null, null, null)
    }
}
