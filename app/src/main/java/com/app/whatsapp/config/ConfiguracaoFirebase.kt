package com.app.whatsapp.config

import com.app.whatsapp.config.ConfiguracaoFirebase.database
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

object ConfiguracaoFirebase {

    private lateinit var database : DatabaseReference
    private lateinit var auth: FirebaseAuth

    fun getFirebaseDatabase() : DatabaseReference{
        database = Firebase.database.reference
        return database
    }

    fun getFirebaseAuth() : FirebaseAuth{
        auth = FirebaseAuth.getInstance()
        return auth
    }
}