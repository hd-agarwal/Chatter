package com.example.chatter

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.chatter.databinding.ActivitySignInBinding
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.AuthUI.IdpConfig.PhoneBuilder
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth


class SignInActivity : AppCompatActivity() {
    val auth = FirebaseAuth.getInstance()
    private lateinit var _binding:ActivitySignInBinding

    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) { result: FirebaseAuthUIAuthenticationResult? ->
        if (result != null) {
            onSignInResult(result)
        }
    }

// ...

    // ...
    private fun startSignIn() {
        val phoneConfigWithDefaultNumber = PhoneBuilder()
            .setDefaultCountryIso("in")
            .build()
        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(
                listOf(
                    phoneConfigWithDefaultNumber,
                )
            )
            .setTheme(R.style.FirebaseAuthTheme)
            .build()
        signInLauncher.launch(signInIntent)
    }
    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        val response = result.idpResponse
        Log.d("TAG", "onSignInResult: inside onSignInResult")
        if (result.resultCode == RESULT_OK) {
            // Successfully signed in
            Log.d("TAG", "onSignInResult: Signed in successfully")
            startActivity(
                Intent(this,DashboardActivity::class.java))
            finish()
        } else {
            Log.d("TAG", "onSignInResult: Sign in failed")
            // Sign in failed
            if (response == null) {
                // User pressed back button
                Toast.makeText(this, "Sign in cancelled", Toast.LENGTH_SHORT).show()
//                showSnackbar(R.string.sign_in_cancelled)
                return
            }
            if (response.error!!.errorCode == ErrorCodes.NO_NETWORK) {
                Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show()
//                showSnackbar(R.string.no_internet_connection)
                return
            }
            Toast.makeText(this, "Unknown error", Toast.LENGTH_SHORT).show()
//            showSnackbar(R.string.unknown_error)
            Log.e("TAG", "Sign-in error: ", response.error)
        }
    }
//    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
//        if (result.resultCode == RESULT_OK) {
//            // Successfully signed in
//            val response = result.idpResponse
//            startActivity(
//                Intent(this, WelcomeBackActivity::class.java)
//                    .putExtra("my_token", response!!.idpToken)
//            )
//        }
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding= ActivitySignInBinding.inflate(layoutInflater)
        setContentView(_binding.root)
        if (auth.currentUser != null) {
            // already signed in
            startActivity(
                Intent(this,DashboardActivity::class.java))
            finish()
        } else {
            // not signed in
            startSignIn()
        }

    }
}