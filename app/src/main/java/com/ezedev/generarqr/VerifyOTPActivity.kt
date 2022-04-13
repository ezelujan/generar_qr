package com.ezedev.generarqr

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ezedev.generarqr.databinding.ActivityVerifyOtpactivityBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthProvider

class VerifyOTPActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVerifyOtpactivityBinding
    private lateinit var verificationId: String
    private var tag = "VerifyOTPActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVerifyOtpactivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val textMobile = binding.textMobile

        textMobile.text = String.format("+57-%s", intent.getSerializableExtra("mobile"))
        setupOTPInputs()
        clickButtonVerify()
    }

    private fun clickButtonVerify() {
        val inputOTP1 = binding.inputOTP1
        val inputOTP2 = binding.inputOTP2
        val inputOTP3 = binding.inputOTP3
        val inputOTP4 = binding.inputOTP4
        val inputOTP5 = binding.inputOTP5
        val inputOTP6 = binding.inputOTP6
        val progressBar = binding.progressBar
        val buttonVerify = binding.buttonVerify
        verificationId = intent.getStringExtra("verificationId").toString()

        buttonVerify.setOnClickListener {
            if (
                inputOTP1.text.toString().trim().isEmpty() ||
                inputOTP2.text.toString().trim().isEmpty() ||
                inputOTP3.text.toString().trim().isEmpty() ||
                inputOTP4.text.toString().trim().isEmpty() ||
                inputOTP5.text.toString().trim().isEmpty() ||
                inputOTP6.text.toString().trim().isEmpty()
            ) {
                Toast.makeText(applicationContext, "Ingresa el código", Toast.LENGTH_SHORT).show()
            } else {
                val code = "${inputOTP1.text}${inputOTP2.text}${inputOTP3.text}${inputOTP4.text}${inputOTP5.text}${inputOTP6.text}"
                progressBar.visibility = View.VISIBLE
                buttonVerify.visibility = View.INVISIBLE
                val credential = PhoneAuthProvider.getCredential(verificationId, code)
                FirebaseAuth
                    .getInstance()
                    .signInWithCredential(credential)
                    .addOnCompleteListener { task ->
                        progressBar.visibility = View.GONE
                        buttonVerify.visibility = View.VISIBLE
                        if (task.isSuccessful) {
                            val intent = Intent(applicationContext, MainActivity::class.java)
                            startActivity(intent)
                        } else {
                            Log.w(tag, "signInWithCredential:failure", task.exception)
                            if (task.exception is FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(this, "El codigo ingresado es invalido", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(this, (task.exception as FirebaseAuthInvalidCredentialsException).message, Toast.LENGTH_LONG).show()
                            }
                        }
                    }
            }
        }
    }

    private fun setupOTPInputs() {
        binding.inputOTP1.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(!s.toString().trim().isEmpty()) {
                    binding.inputOTP2.requestFocus()
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
        binding.inputOTP2.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(!s.toString().trim().isEmpty()) {
                    binding.inputOTP3.requestFocus()
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
        binding.inputOTP3.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(!s.toString().trim().isEmpty()) {
                    binding.inputOTP4.requestFocus()
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
        binding.inputOTP4.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(!s.toString().trim().isEmpty()) {
                    binding.inputOTP5.requestFocus()
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
        binding.inputOTP5.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(!s.toString().trim().isEmpty()) {
                    binding.inputOTP6.requestFocus()
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
        binding.inputOTP6.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s.toString().trim().isNotEmpty()) {
                    binding.buttonVerify.callOnClick()
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(binding.inputOTP6.windowToken, 0)
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }
}