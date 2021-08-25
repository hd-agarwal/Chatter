package com.example.chatter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.chatter.databinding.ActivityLoginBinding
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import java.util.concurrent.TimeUnit

class LoginActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "LoginActivity"
    }

    private lateinit var _binding: ActivityLoginBinding
    private val auth = FirebaseAuth.getInstance()
    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            Log.d(TAG, "onVerificationCompleted:$credential")
            Toast.makeText(this@LoginActivity, "Verification complete", Toast.LENGTH_SHORT).show()
            updateUI(UIState.VERIFYING_OTP)
            signInWithPhoneAuthCredential(credential)
        }

        override fun onVerificationFailed(e: FirebaseException) {
            Log.w(TAG, "onVerificationFailed", e)
            if (e is FirebaseAuthInvalidCredentialsException) {
                // Invalid request
                Toast.makeText(
                    this@LoginActivity,
                    "Invalid phone number! Request rejected",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (e is FirebaseTooManyRequestsException) {
                // The SMS quota for the project has been exceeded
                Toast.makeText(this@LoginActivity, "Please try after 24 hours", Toast.LENGTH_SHORT)
                    .show()
            }
            // Show a message and update the UI
            updateUI(UIState.ENTER_PHONE)
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken
        ) {
            // The SMS verification code has been sent to the provided phone number, we
            // now need to ask the user to enter the code and then construct a credential
            // by combining the code with a verification ID.
            Log.d(TAG, "onCodeSent:$verificationId")
            Toast.makeText(this@LoginActivity, "OTP sent", Toast.LENGTH_SHORT).show()
            updateUI(UIState.ENTER_OTP)
            _binding.btnVerify.setOnClickListener {
                val credential =
                    PhoneAuthProvider.getCredential(verificationId, _binding.etOTP.text.toString())
                updateUI(UIState.VERIFYING_OTP)
                signInWithPhoneAuthCredential(credential)
            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(_binding.root)
        initUI()
    }

    private fun initiateLogin(phoneNumber: String) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this)                 // Activity (for callback binding)
            .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
// TODO: 25/08/21 login successful
                    Toast.makeText(this, "Successfully signed in", Toast.LENGTH_SHORT).show()
                    val user = task.result?.user
                } else {
                    // Sign in failed, display a message and update the UI
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                        Toast.makeText(this@LoginActivity, "Incorrect OTP", Toast.LENGTH_SHORT)
                            .show()
                        updateUI(UIState.ENTER_OTP)
                    } else {
                        Toast.makeText(
                            this@LoginActivity,
                            "Verification Failed",
                            Toast.LENGTH_SHORT
                        ).show()
                        updateUI(UIState.ENTER_PHONE)
                    }
                }
            }
    }

    private fun initUI() {
        this.title = "Login with phone"
        _binding.btnSendOTP.setOnClickListener {
            var phone = _binding.etPhone.text.toString()
            if ((phone.startsWith("+") && phone.length < 13) || phone.length < 10) {
                Toast.makeText(
                    this@LoginActivity,
                    "Please enter a valid phone number",
                    Toast.LENGTH_SHORT
                ).show()
                updateUI(UIState.ENTER_PHONE)
            } else {
                updateUI(UIState.SENDING_OTP)
                if (phone.length == 10)
                    phone = "+91$phone"
                initiateLogin(phone)
            }
        }
        _binding.btnReset.setOnClickListener {
            updateUI(UIState.ENTER_PHONE)
            _binding.etPhone.apply {
                setText("")
            }
        }

        _binding.etPhone.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (_binding.etPhone.text.toString().isEmpty()) {
                    _binding.btnReset.apply {
                        isEnabled = false
                    }
                    _binding.btnSendOTP.apply {
                        isEnabled = false
                    }
                } else {
                    _binding.btnReset.apply {
                        isEnabled = true
                    }
                    _binding.btnSendOTP.apply {
                        isEnabled = true
                    }
                }
            }

        })
        _binding.etOTP.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (_binding.etOTP.text.toString().isEmpty()) {
                    _binding.btnVerify.apply {
                        isEnabled = false
                    }
                } else {
                    _binding.btnVerify.apply {
                        isEnabled = true
                    }
                }
            }
        })
    }

    enum class UIState {
        ENTER_PHONE,
        SENDING_OTP,
        ENTER_OTP,
        VERIFYING_OTP
    }

    fun updateUI(uiState: UIState) = when (uiState) {
        UIState.ENTER_PHONE -> {
            _binding.etPhone.apply {
                isEnabled = true
                visibility = View.VISIBLE
            }
            _binding.progressBar.apply {
                visibility = View.INVISIBLE
            }
            _binding.btnVerify.apply {
                visibility = View.INVISIBLE
            }
            _binding.etOTP.apply {
                isEnabled = false
                visibility = View.INVISIBLE
            }
            _binding.btnReset.apply {
                visibility = View.VISIBLE
            }
            _binding.btnSendOTP.apply {
                visibility = View.VISIBLE
            }
            _binding.progressBarVerify.apply {
                visibility = View.INVISIBLE
            }
        }
        UIState.SENDING_OTP -> {
            _binding.etPhone.apply {
                isEnabled = false
                visibility = View.VISIBLE
            }
            _binding.progressBar.apply {
                visibility = View.VISIBLE
            }
            _binding.btnVerify.apply {
                visibility = View.VISIBLE
            }
            _binding.etOTP.apply {
                isEnabled = true
                visibility = View.VISIBLE
            }
            _binding.btnReset.apply {
                visibility = View.VISIBLE
                isEnabled = false
            }
            _binding.btnSendOTP.apply {
                visibility = View.VISIBLE
                isEnabled = false
            }
            _binding.progressBarVerify.apply {
                visibility = View.INVISIBLE
            }
        }
        UIState.ENTER_OTP -> {
            _binding.etPhone.apply {
                isEnabled = false
                visibility = View.VISIBLE
            }
            _binding.progressBar.apply {
                visibility = View.INVISIBLE
            }
            _binding.btnVerify.apply {
                visibility = View.VISIBLE
            }
            _binding.etOTP.apply {
                isEnabled = true
                visibility = View.VISIBLE
                setText("")
            }
            _binding.btnReset.apply {
                visibility = View.VISIBLE
                isEnabled = true
            }
            _binding.btnSendOTP.apply {
                visibility = View.VISIBLE
                isEnabled = false
            }
            _binding.progressBarVerify.apply {
                visibility = View.INVISIBLE
            }
        }
        UIState.VERIFYING_OTP -> {
            _binding.etPhone.apply {
                isEnabled = false
                visibility = View.VISIBLE
            }
            _binding.progressBar.apply {
                visibility = View.INVISIBLE
            }
            _binding.btnVerify.apply {
                visibility = View.VISIBLE
                isEnabled = false
            }
            _binding.etOTP.apply {
                isEnabled = false
                visibility = View.VISIBLE
            }
            _binding.btnReset.apply {
                visibility = View.VISIBLE
                isEnabled = false
            }
            _binding.btnSendOTP.apply {
                visibility = View.VISIBLE
                isEnabled = false
            }
            _binding.progressBarVerify.apply {
                visibility = View.VISIBLE
            }
        }
    }
}