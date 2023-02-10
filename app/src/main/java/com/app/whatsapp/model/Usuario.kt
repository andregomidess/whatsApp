package com.app.whatsapp.model

import android.util.Log
import com.app.whatsapp.config.ConfiguracaoFirebase
import com.app.whatsapp.helper.UsuarioFirebase
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.Exclude
import com.google.firebase.database.FirebaseDatabase
import java.util.Objects

class Usuario() {

    @set:Exclude @get:Exclude
    var uid: String = ""
    var nome: String = ""
    var email: String = ""
    @set:Exclude @get:Exclude
    var senha: String = ""
    var foto : String = ""

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

    fun atualizar(){
        val identificadorUSusario: String = UsuarioFirebase.getIdUsuario()
        val database : DatabaseReference = ConfiguracaoFirebase.getFirebaseDatabase()
        val usuariosRef: DatabaseReference = database.child("usuarios").child(identificadorUSusario)

        val valorUsuario: MutableMap<String, Any> = converterParaMap()
        usuariosRef.updateChildren(valorUsuario)

    }

    @Exclude
    fun converterParaMap() : MutableMap<String, Any>{
        val usuarioMap : MutableMap<String, Any> = mutableMapOf()
        usuarioMap["email"] = email
        usuarioMap["nome"] = nome
        usuarioMap["foto"] = foto

        return usuarioMap
    }

}