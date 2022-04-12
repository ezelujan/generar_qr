package com.ezedev.generarqr

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.ezedev.generarqr.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import java.util.*


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var auth: FirebaseAuth = Firebase.auth
    private var db = FirebaseFirestore.getInstance()
    private val tag = "activity_main"
    private var firstInfo = hashMapOf("https://www.coordinadora.com/" to 15000)
    private var otherInfo = hashMapOf(
        "https://www.google.com/" to 38000,
        "https://www.facebook.com/" to 240000,
        "https://www.gmail.com/" to 23000,
        "https://www.instagram.com/" to 65000,
        "https://www.twitter.com/" to 90000,
        "https://www.twitch.com/" to 792000,
        "https://ezedev.netlify.app/" to 430000,
        "https://material.io/" to 560000
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        authentication()
        binding.btnGenerateQR.setOnClickListener {
            binding.loading.visibility = View.VISIBLE
            val idCode = (100000..999999).random().toString()
            val payment = (100..9999999).random()
            val url = "https://us-central1-cm-registro-ingresos-dev.cloudfunctions.net/widgets/prueba/$idCode"
            generateQR(url) { result ->
                binding.imgQR.setBackgroundColor(0)
                binding.loading.visibility = View.INVISIBLE
                binding.btnGenerateQR.visibility = View.INVISIBLE
                binding.btnGenerateNewQR.visibility = View.VISIBLE
                setDocumentFs(url, payment, idCode)
                onSnapshotPayment(idCode)
                Log.d(tag, result.toString())
            }
        }
        binding.btnGenerateNewQR.setOnClickListener {
            binding.loading.visibility = View.VISIBLE
            val idCode = (100000..999999).random().toString()
            val payment = (100..9999999).random()
            val url = "https://us-central1-cm-registro-ingresos-dev.cloudfunctions.net/widgets/prueba/$idCode"
            generateQR(url) { result ->
                binding.imgQR.setBackgroundColor(0)
                binding.loading.visibility = View.INVISIBLE
                setDocumentFs(url, payment, idCode)
                onSnapshotPayment(idCode)
                Log.d(tag, result.toString())
            }
        }
    }

    private fun onSnapshotPayment(idCode: String) {
        val docRef = db.collection("Pagos").document(idCode)
        docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w(tag, "Listen failed.", e)
                return@addSnapshotListener
            }
            if (snapshot != null && snapshot.exists()) {
                if (snapshot.data!!["estado"] == "FINALIZADO") {
                    binding.tvOk.visibility = View.VISIBLE
                    binding.viewOk.visibility = View.VISIBLE
                    binding.imgQR.setImageResource(0)
                }
            } else {
                Log.d(tag, "Current data: null")
            }
        }
    }

    private fun generateQR(content: String, myCallback: (result: String?) -> Unit) {
        Handler(Looper.getMainLooper()).postDelayed({
            binding.tvOk.visibility = View.INVISIBLE
            binding.viewOk.visibility = View.INVISIBLE
            val bitmap = BarcodeEncoder().encodeBitmap(content, BarcodeFormat.QR_CODE, 400, 400)
            val imageViewQrCode = binding.imgQR
            imageViewQrCode.setImageBitmap(bitmap)
            myCallback.invoke("QR Generado con exito")
        }, (100..1000).random().toLong())
    }

    private fun setDocumentFs(url: String, payment: Int, idCode: String) {
        val model = hashMapOf(
            "fecha_registro" to Date(),
            "id" to null,
            "id_codigo" to idCode,
            "estado" to "EN PROCESO"
        )
        val modelSubCollection = hashMapOf(
            "url" to url.replace("[","").replace("]", ""),
            "pagos" to payment,
            "fecha_registro" to Date(),
            "estado" to "EN PROCESO"
        )
        val paymentsRef = db.collection("Pagos").document(idCode)
        val subPaymentsRef = db.collection("Pagos").document(idCode).collection("Pagos").document()
        paymentsRef.get()
            .addOnSuccessListener { document ->
                if (document.data != null) {
                    Log.d("Existe doc", document.data.toString())
                    db.runBatch { batch ->
                        batch.set(subPaymentsRef, modelSubCollection)
                    }.addOnSuccessListener {
                        Log.d(tag,"Batch completed")
                        subPaymentsRef.get().addOnSuccessListener { doc ->
                            paymentsRef.update("id", doc.id)
                        }
                    }.addOnFailureListener {
                        Log.w(tag, "Error execute batch", it)
                    }
                } else {
                    Log.w("No existe doc", "No existe el documento, se guardará uno nuevo")
                    db.runBatch { batch ->
                        batch.set(paymentsRef, model)
                        batch.set(subPaymentsRef, modelSubCollection)
                    }.addOnSuccessListener {
                        Log.d(tag,"Batch completed")
                        subPaymentsRef.get().addOnSuccessListener { doc ->
                            paymentsRef.update("id", doc.id)
                        }
                    }.addOnFailureListener {
                        Log.w(tag, "Error execute batch", it)
                    }
                }
            }.addOnFailureListener {
                Log.w(tag, "Get document failed with", it)
            }
    }

    private fun authentication() {
        auth
            .signInWithEmailAndPassword("it@coordinadora.com", "leqhddce")
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Log.d(tag, "Autenticacion exitosa")
                } else {
                    Log.w(tag, "Falló la autenticacion")
                }
            }
            .addOnFailureListener {
                Log.w(tag, "Falló la conexión para autenticarse $it")
            }
    }

    override fun onBackPressed() {
        auth.signOut()
        finishAffinity()
    }
}
