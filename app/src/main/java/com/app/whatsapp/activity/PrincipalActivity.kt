package com.app.whatsapp.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import com.app.whatsapp.R
import com.app.whatsapp.databinding.ActivityPrincipalBinding

abstract class PrincipalActivity : AppCompatActivity() {

    private lateinit var binding : ActivityPrincipalBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrincipalBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val toolbar:Toolbar = binding.toolbar.toolbarPrincipal
        toolbar.title = "WhatsApp"
        setSupportActionBar(toolbar)


    }
}