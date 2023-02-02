package com.app.whatsapp.activity

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.app.whatsapp.databinding.ActivityConfiguracoesBinding
import com.app.whatsapp.helper.Permissao

class ConfiguracoesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityConfiguracoesBinding
    private val permissoesNecessarias = arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConfiguracoesBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        Permissao.validaPermissao(permissoesNecessarias, this, 1)

        binding.toolbar.toolbarPrincipal.title = "Configurações"
        setSupportActionBar(binding.toolbar.toolbarPrincipal)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
}