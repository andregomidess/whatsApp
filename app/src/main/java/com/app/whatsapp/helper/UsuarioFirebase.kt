package com.app.whatsapp.helper

import android.net.Uri
import android.util.Log
import com.app.whatsapp.config.ConfiguracaoFirebase
import com.app.whatsapp.model.Usuario
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest

object UsuarioFirebase {

    fun getIdUsuario(): String {
        val usuario: FirebaseAuth = ConfiguracaoFirebase.getFirebaseAuth()
        val email: String = usuario.currentUser!!.email!!

        return email.let { Base64Custom.codificarString(it) }
    }

    fun getUsuarioFirebase() : FirebaseUser{
        val usuario: FirebaseAuth = ConfiguracaoFirebase.getFirebaseAuth()
        return usuario.currentUser!!
    }

    fun atualizarFotoUsuario(url:Uri) : Boolean{

       try {
           val user : FirebaseUser = getUsuarioFirebase()
           val profile : UserProfileChangeRequest = UserProfileChangeRequest.Builder()
               .setPhotoUri(url)
               .build()
           user.updateProfile(profile).addOnCompleteListener {
               if (!it.isSuccessful){
                   Log.d("perfil", "Erro ao atualizar foto de perfil")

               }
           }
           return true
       }catch (e: Exception){
           e.printStackTrace()
           return false
       }
    }

    fun atualizarNomeUsuario(nome: String) : Boolean{

        try {
            val user : FirebaseUser = getUsuarioFirebase()
            val profile : UserProfileChangeRequest = UserProfileChangeRequest.Builder()
                .setDisplayName(nome)
                .build()
            user.updateProfile(profile).addOnCompleteListener {
                if (!it.isSuccessful){
                    Log.d("Nome", "Erro ao atualizar Nome do perfil")

                }
            }
            return true
        }catch (e: Exception){
            e.printStackTrace()
            return false
        }
    }

    fun getDadosUsuarioLogado() : Usuario{
        val firebaseUser: FirebaseUser = getUsuarioFirebase()
        val usuario: Usuario = Usuario()

        usuario.email = firebaseUser.email.toString()
        usuario.nome = firebaseUser.displayName.toString()

        if (firebaseUser.photoUrl == null){
            usuario.foto = ""
        }else{
            usuario.foto = firebaseUser.photoUrl.toString()
        }

        return usuario
    }
}