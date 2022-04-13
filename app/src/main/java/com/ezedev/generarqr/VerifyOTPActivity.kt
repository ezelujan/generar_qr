package com.ezedev.generarqr

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.ezedev.generarqr.databinding.ActivityVerifyOtpactivityBinding

class VerifyOTPActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVerifyOtpactivityBinding

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
        binding.buttonVerify.setOnClickListener {
            if (
                binding.inputOTP1.text.toString().trim().isEmpty() ||
                binding.inputOTP2.text.toString().trim().isEmpty() ||
                binding.inputOTP3.text.toString().trim().isEmpty() ||
                binding.inputOTP4.text.toString().trim().isEmpty() ||
                binding.inputOTP5.text.toString().trim().isEmpty() ||
                binding.inputOTP6.text.toString().trim().isEmpty()
            ) {
                Toast.makeText(applicationContext, "Ingresa el código", Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(applicationContext, MainActivity::class.java)
                startActivity(intent)
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