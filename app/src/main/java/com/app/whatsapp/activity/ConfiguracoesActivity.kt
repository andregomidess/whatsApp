package com.app.whatsapp.activity

import android.Manifest
import android.content.DialogInterface
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        for (permissaoResultado : Int in grantResults){
            if (permissaoResultado == PackageManager.PERMISSION_GRANTED)
                alertaValidacaoPermissao()
        }
    }

    private fun alertaValidacaoPermissao() {
        val builder : AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("Permissões negadas")
        builder.setMessage("Para utilizar o app é necessário caeitar as permissões!")
        builder.setCancelable(false)
        builder.setPositiveButton("Confirmar", DialogInterface.OnClickListener { dialog, which ->
            finish()
        })
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
}