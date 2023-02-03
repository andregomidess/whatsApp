package com.app.whatsapp.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import com.app.whatsapp.databinding.ActivityConfiguracoesBinding
import com.app.whatsapp.helper.Permissao
import de.hdodenhof.circleimageview.CircleImageView
import java.util.BitSet

class ConfiguracoesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityConfiguracoesBinding
    private val permissoesNecessarias = arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE)
    private final val SELECAO_CAMERA: Int = 100
    private final val SELECAO_GALERIA: Int = 200

    @SuppressLint("QueryPermissionsNeeded")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConfiguracoesBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        Permissao.validaPermissao(permissoesNecessarias, this, 1)

        binding.toolbar.toolbarPrincipal.title = "Configurações"
        setSupportActionBar(binding.toolbar.toolbarPrincipal)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.imageButtonCamera.setOnClickListener {
            val i: Intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if(i.resolveActivity(packageManager) != null){
                startActivityForResult(i, SELECAO_CAMERA)
            }
        }

        binding.imageButtonGaleria.setOnClickListener {
            val i : Intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            if (i.resolveActivity(packageManager) != null){
                startActivityForResult(i, SELECAO_GALERIA)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK){
            var imagem: Bitmap? = null

            try {
                when (requestCode) {
                     SELECAO_CAMERA -> {
                         if (data != null) {
                             imagem = data.extras?.get("data") as Bitmap?
                         }
                     }
                    SELECAO_GALERIA -> {
                        val localImgSelecionada: Uri = data?.data!!
                        if (Build.VERSION.SDK_INT < 28){
                            imagem = MediaStore.Images.Media.getBitmap(contentResolver, localImgSelecionada)
                        }else{
                            val source = ImageDecoder.createSource(contentResolver, localImgSelecionada)
                            imagem = ImageDecoder.decodeBitmap(source)
                        }


                    }
                }
                if (imagem != null){
                    binding.circleImagePhotoPerfil.setImageBitmap(imagem)
                }
            }catch (e: Exception){
                e.printStackTrace()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        for (permissaoResultado : Int in grantResults){
            if (permissaoResultado == PackageManager.PERMISSION_DENIED)
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