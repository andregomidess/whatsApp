package com.app.whatsapp.helper

import com.app.whatsapp.config.ConfiguracaoFirebase
import com.google.firebase.auth.FirebaseAuth

object UsuarioFirebase {

    fun getIdUsuario(): String {
        val usuario: FirebaseAuth = ConfiguracaoFirebase.getFirebaseAuth()
        val email: String = usuario.currentUser!!.email!!

        return email.let { Base64Custom.codificarString(it) }
    }
}