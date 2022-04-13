package com.ezedev.generarqr

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.ezedev.generarqr.databinding.ActivityMainBinding
import com.ezedev.generarqr.databinding.ActivityPruebaOtherBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class Prueba_Other : AppCompatActivity() {
    private lateinit var binding: ActivityPruebaOtherBinding
    var mFirebaseAuth: FirebaseAuth? = null
    var verificationIds:String ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPruebaOtherBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loginSessionFir()
        manageButton()

    }

    private fun manageButton() {

        binding.button.setOnClickListener( View.OnClickListener {
            createCall()
        })
        binding.button2.setOnClickListener( View.OnClickListener {
            if(binding.textInputEditText.text.toString().trim().isNotEmpty()){
                verificarCodigo(binding.textInputEditText.text.toString().trim())
            }
        })
    }

    private fun createCall() {
        var activity = this
        val options = PhoneAuthOptions.newBuilder(mFirebaseAuth!!)
            .setPhoneNumber("+573133799116")       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this)                 // Activity (for callback binding)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                override fun onCodeSent(
                    verificationId: String,
                    forceResendingToken: PhoneAuthProvider.ForceResendingToken
                ) {
                    verificationIds=verificationId
                    Toast.makeText(activity,"mensaje "+verificationIds,Toast.LENGTH_LONG).show()
                }

                override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                    var codigo:String = phoneAuthCredential.smsCode.toString()
                    Toast.makeText(activity,"codigo "+codigo,Toast.LENGTH_LONG).show()
                    Log.i("codigo",codigo)
                   // verificarCodigo(phoneAuthCredential.smsCode!!)

                }

                override fun onVerificationFailed(e: FirebaseException) {
                    Toast.makeText(activity,"error faildes "+e.message.toString(),Toast.LENGTH_LONG).show()
                   Log.i("error failed",e.message.toString())
                }
            })          // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)


    }

    private fun verificarCodigo(codigo: String) {

        var credential:PhoneAuthCredential = PhoneAuthProvider.getCredential(verificationIds!!,codigo)
       mFirebaseAuth!!.signInWithCredential(credential).addOnCompleteListener {
            if(it.isSuccessful){
                Toast.makeText(this,"success",Toast.LENGTH_LONG).show()
                Log.i("success","success")
            }else{
                Toast.makeText(this,"error",Toast.LENGTH_LONG).show()
                Log.i("error succes","error")
            }
        }.addOnFailureListener( OnFailureListener {
           Toast.makeText(this,"error faildes "+it.message.toString(),Toast.LENGTH_LONG).show()
            Log.i("error failed",it.message.toString())
        })

    }


    fun loginSessionFir() {
        mFirebaseAuth = FirebaseAuth.getInstance(getInstance(this)!!)
        var user = mFirebaseAuth!!.currentUser
    }
    private var INSTANCE: FirebaseApp? = null

    fun getInstance(context: Context): FirebaseApp? {
        if (INSTANCE == null) {
            INSTANCE = getSecondProject(context)
        }
        return INSTANCE
    }

    private fun getSecondProject(context: Context): FirebaseApp? {
        val options1 = FirebaseOptions.Builder()
            .setApiKey("AIzaSyDqOZ2fopAtWtzhKVv-xwAC3lYWhEag7po")
            .setApplicationId("1:970175729993:android:e8e3673ea293374e5a0a98")
            .setProjectId("cm-whatsapp-app-dev")
            .build()
        FirebaseApp.initializeApp(context, options1, "admin")
        return FirebaseApp.getInstance("admin")
    }

}