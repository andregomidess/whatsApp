package com.app.whatsapp.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.app.whatsapp.databinding.ActivityConfiguracoesBinding

class ConfiguracoesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityConfiguracoesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConfiguracoesBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.toolbar.toolbarPrincipal.title = "Configurações"
        setSupportActionBar(binding.toolbar.toolbarPrincipal)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
}