package com.example.glamora.fragmentLogin.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.glamora.R
import com.example.glamora.data.model.customerModels.CustomerInfo
import com.example.glamora.databinding.FragmentLoginBinding
import com.example.glamora.fragmentLogin.viewModel.LoginViewModel
import com.example.glamora.mainActivity.view.Communicator
import com.example.glamora.mainActivity.viewModel.SharedViewModel
import com.example.glamora.util.State
import com.example.glamora.util.isNotShort
import com.example.glamora.util.isValidEmail
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private lateinit var loginBinding: FragmentLoginBinding
    private val loginViewModel: LoginViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by activityViewModels()

    private val communicator: Communicator by lazy {
        (requireActivity() as Communicator)
    }

    private lateinit var googleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 9001


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        loginBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false)
        return loginBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userEmail = arguments?.getString("userEmail")
        val userPassword = arguments?.getString("userPassword")

        if (userEmail != null && userPassword != null) {
            loginBinding.editEmail.setText(userEmail)
            loginBinding.editPassword.setText(userPassword)
        }


//        loginViewModel.fetchUserByEmail("kerolos.raouf5600@gmail.com")
//
//        lifecycleScope.launch {
//            loginViewModel.customerResult.collect { result ->
//                result?.onSuccess { customerInfo ->
//                    if (customerInfo != null) {
//                        Log.d("Abanob", "onViewCreated: ${customerInfo.userId}")
//                        Log.d("Abanob", "onViewCreated: ${customerInfo.email}")
//                        Log.d("Abanob", "onViewCreated: ${customerInfo.displayName}")
//                    } else {
//                        Log.d("Abanob", "onViewCreated: Null")
//                    }
//
//                }?.onFailure { exception ->
//                    Log.d("Abanob", "onViewCreated: ${exception.message}")
//                }
//            }
//        }

        setupGoogleSignIn()

        loginBinding.loginBtn.setOnClickListener {
            resetErrors()
            validateAndLogin()
        }

        loginBinding.signUp.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_signUpFragment)
        }

        loginBinding.signInGoogleBtn.setOnClickListener {
            signInWithGoogle()
        }

        loginBinding.guestBtn.setOnClickListener {

            val builder = AlertDialog.Builder(requireContext())

            builder.setTitle("Guest User")
            builder.setMessage("Are you sure you want to proceed as a guest?")

            builder.setPositiveButton("OK") { dialog, _ ->
                findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                dialog.dismiss()

            }

            builder.setNegativeButton("Close") { dialog, _ ->
                dialog.dismiss()
            }

            val alertDialog = builder.create()

            alertDialog.setOnShowListener {
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(
                    ContextCompat.getColor(requireContext(), R.color.light_blue)
                )
                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(
                    ContextCompat.getColor(requireContext(), R.color.light_blue)
                )
            }

            alertDialog.show()
        }

        loginBinding.resetBtn.setOnClickListener {
            val email = loginBinding.editEmail.text.toString()
            val isEmailValid = isValidEmail(email)

            if (isEmailValid) {
                loginViewModel.resetUserPass(email)
            } else {
                showErrorEmail()
            }

            lifecycleScope.launch {
                loginViewModel.toastMessage.collect { message ->
                    message?.let {
                        Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                        loginViewModel.clearToastMessage()
                    }
                }
            }
        }

        observeLoginState()
    }

    private fun validateAndLogin() {
        val email = loginBinding.editEmail.text.toString()
        val password = loginBinding.editPassword.text.toString()

        val isEmailValid = isValidEmail(email)
        val isPasswordValid = isNotShort(password)

        if (isEmailValid && isPasswordValid) {
            loginViewModel.loginWithEmail(email, password)
        } else {
            if (!isEmailValid) showErrorEmail()
            if (!isPasswordValid) showErrorPassword()
        }
    }

    private fun resetErrors() {
        loginBinding.errEmailTxt.visibility = View.GONE
        loginBinding.errPassTxt.visibility = View.GONE
        loginBinding.editEmail.background =
            ContextCompat.getDrawable(requireContext(), R.drawable.button_background)
        loginBinding.editPassword.background =
            ContextCompat.getDrawable(requireContext(), R.drawable.button_background)
        loginBinding.editEmail.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
        loginBinding.editPassword.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
    }

    private fun showErrorEmail() {
        loginBinding.errEmailTxt.visibility = View.VISIBLE
        loginBinding.editEmail.background =
            ContextCompat.getDrawable(requireContext(), R.drawable.button_background_err)
        val drawableIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_email_err)
        loginBinding.editEmail.setCompoundDrawablesWithIntrinsicBounds(
            drawableIcon,
            null,
            null,
            null
        )
    }

    private fun showErrorPassword() {
        loginBinding.errPassTxt.visibility = View.VISIBLE
        loginBinding.editPassword.background =
            ContextCompat.getDrawable(requireContext(), R.drawable.button_background_err)
        val drawableIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_pass_err)
        loginBinding.editPassword.setCompoundDrawablesWithIntrinsicBounds(
            drawableIcon,
            null,
            null,
            null
        )
    }

    private fun setupGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
    }

    private fun signInWithGoogle() {
        val signInIntent: Intent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                loginViewModel.loginWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Toast.makeText(requireContext(), "Google sign-in failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun observeLoginState() {
        lifecycleScope.launch {
            loginViewModel.loginState.collect { state ->
                when (state) {
                    is State.Loading -> {
                        loginBinding.progressBar.visibility = View.VISIBLE
                    }

                    is State.Success -> {
                        lifecycleScope.launch {
                            // Collect email once and wait for completion
                            val email = loginViewModel.customerEmail.firstOrNull()

                            if (email != null) {
                                // Fetch user by email and wait for the result
                                loginViewModel.fetchUserByEmail(email)

                                // Collect the customerResult and handle it
                                loginViewModel.customerResult.collect { result ->
                                    result?.onSuccess { customerInfo ->
                                        if (customerInfo != null) {
                                            sharedViewModel.setCustomerInfo(customerInfo)
                                        } else {
                                            sharedViewModel.setCustomerInfo(CustomerInfo(displayName = email.split("@")[0]))
                                        }

                                        loginBinding.progressBar.visibility = View.GONE
                                        Toast.makeText(requireContext(), "Login successful!", Toast.LENGTH_SHORT).show()
                                        findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                                    }
                                }
                            }
                        }
                    }


                    is State.Error -> {
                        loginBinding.progressBar.visibility = View.GONE
                        Toast.makeText(requireContext(), "Login failed", Toast.LENGTH_SHORT).show()
                        showErrorEmail()
                        showErrorPassword()
                    }

                    null -> {}
                }
            }
        }
    }


    override fun onStart() {
        super.onStart()
        communicator.hideBottomNav()
    }
}
