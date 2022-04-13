package com.ezedev.generarqr

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.ezedev.generarqr.databinding.ActivitySendOtpactivityBinding
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.concurrent.TimeUnit

class SendOTPActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySendOtpactivityBinding
    private var auth: FirebaseAuth = Firebase.auth
    private val tag = "SendOTPActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySendOtpactivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val inputMobile = binding.inputNumber
        val buttonGetOTP = binding.buttonGetOPT
        val progressBar = binding.progressBar

        buttonGetOTP.setOnClickListener {
            if (inputMobile.text.toString().trim().isEmpty()) {
                Toast.makeText(applicationContext, "Ingresa el número", Toast.LENGTH_SHORT).show()
            } else {
                progressBar.visibility = View.VISIBLE
                buttonGetOTP.visibility = View.INVISIBLE

                val options = PhoneAuthOptions.newBuilder(auth)
                    .setPhoneNumber("+57" + inputMobile.text.toString())
                    .setTimeout(60L, TimeUnit.SECONDS)
                    .setActivity(this)
                    .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                            progressBar.visibility = View.GONE
                            buttonGetOTP.visibility = View.VISIBLE
                            Log.d(tag, "onVerificationCompleted:$credential")
                        }

                        override fun onVerificationFailed(e: FirebaseException) {
                            Log.w(tag, "onVerificationFailed", e)
                            if (e is FirebaseAuthInvalidCredentialsException) {
                                Log.w(tag, "Invalid request", e)
                            } else if (e is FirebaseTooManyRequestsException) {
                                Log.w(tag, "The SMS quota for the project has been exceeded", e)
                            }
                            progressBar.visibility = View.GONE
                            buttonGetOTP.visibility = View.VISIBLE
                            Toast.makeText(applicationContext, e.message, Toast.LENGTH_LONG).show()
                        }

                        override fun onCodeSent(
                            verificationId: String,
                            token: PhoneAuthProvider.ForceResendingToken
                        ) {
                            val storedVerificationId = verificationId
                            val resendToken = token
                            progressBar.visibility = View.GONE
                            buttonGetOTP.visibility = View.VISIBLE
                            val intent = Intent(applicationContext, VerifyOTPActivity::class.java)
                            intent.putExtra("mobile", inputMobile.text.toString())
                            intent.putExtra("verificationId", verificationId)
                            startActivity(intent)
                            Log.d(tag, "onCodeSent:$verificationId")
                        }
                    })
                    .build()
                PhoneAuthProvider.verifyPhoneNumber(options)
            }
        }
    }
}