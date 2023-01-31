package com.app.whatsapp.helper

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

object ConfiguracaoFirebase {

    private lateinit var database : DatabaseReference
    private lateinit var auth: FirebaseAuth

    fun getFirebaseDatabase() : DatabaseReference{
        database = FirebaseDatabase.getInstance().reference
        return database
    }

    fun getFirebaseAuth() : FirebaseAuth{
        auth = FirebaseAuth.getInstance()
        return auth
    }
}