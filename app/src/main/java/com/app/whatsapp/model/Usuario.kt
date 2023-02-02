package com.app.whatsapp.model

import android.util.Log
import com.app.whatsapp.config.ConfiguracaoFirebase
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.Exclude

class Usuario() {

    var uid: String = ""
    var nome: String = ""
    var email: String = ""
    var senha: String = ""

    fun salvar(){

        val firebaseRef: DatabaseReference = ConfiguracaoFirebase.getFirebaseDatabase()
        val usuario : DatabaseReference = firebaseRef.child("usuario").child(this.uid)
        usuario.setValue(this).addOnSuccessListener {
            Log.i("Sucesso", "Sucesso ao gravar dados")
        }
        .addOnFailureListener{
            Log.i("Falha", it.stackTraceToString())
        }
    }

}