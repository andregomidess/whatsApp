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
import android.text.Editable
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import com.app.whatsapp.R
import com.app.whatsapp.config.ConfiguracaoFirebase
import com.app.whatsapp.databinding.ActivityConfiguracoesBinding
import com.app.whatsapp.helper.Permissao
import com.app.whatsapp.helper.UsuarioFirebase
import com.app.whatsapp.model.Usuario
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import de.hdodenhof.circleimageview.CircleImageView
import java.io.ByteArrayOutputStream
import java.util.BitSet

class ConfiguracoesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityConfiguracoesBinding
    private val permissoesNecessarias = arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE)
    private val SELECAO_CAMERA: Int = 100
    private val SELECAO_GALERIA: Int = 200
    private lateinit var storageReference: StorageReference
    private lateinit var identificadorUsuario: String
    private lateinit var usuarioLogado:Usuario

    @SuppressLint("QueryPermissionsNeeded")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConfiguracoesBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        storageReference = ConfiguracaoFirebase.getFirebaseStorage()
        identificadorUsuario = UsuarioFirebase.getIdUsuario()
        usuarioLogado = UsuarioFirebase.getDadosUsuarioLogado()

        Permissao.validaPermissao(permissoesNecessarias, this, 1)

        binding.toolbar.toolbarPrincipal.title = "Configurações"
        setSupportActionBar(binding.toolbar.toolbarPrincipal)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //recuperar dados usuário
        val firebaseUser: FirebaseUser = UsuarioFirebase.getUsuarioFirebase()
        val url : Uri? = firebaseUser.photoUrl
        if (url != null){
            Glide.with(this)
                .load(url)
                .into(binding.circleImagePhotoPerfil)
        }else{
            binding.circleImagePhotoPerfil.setImageResource(R.drawable.padrao)
        }
        binding.editPerfilNome.setText(firebaseUser.displayName)

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

        binding.imageAtualizarNome.setOnClickListener {

            val nome : String = binding.editPerfilNome.text.toString()
            val retorno : Boolean = UsuarioFirebase.atualizarNomeUsuario(nome)
            if (retorno){

                usuarioLogado.nome = nome
                usuarioLogado.atualizar()
                Toast.makeText(this, "Nome atualizado com sucesso!", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(this, "Erro ao tualizar o nomme!", Toast.LENGTH_SHORT).show()
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

                    //Recuperar dados da img
                    val baos: ByteArrayOutputStream = ByteArrayOutputStream()
                    imagem.compress(Bitmap.CompressFormat.JPEG, 70, baos)
                    val dadosImg: ByteArray = baos.toByteArray()

                    val imagemRef : StorageReference = storageReference
                        .child("imagens")
                        .child("perfil")
                        .child("$identificadorUsuario.jpeg")

                    val uploadTask: UploadTask = imagemRef.putBytes(dadosImg)
                    uploadTask.addOnFailureListener {
                        Toast.makeText(this, "Erro ao fazer upload da imagem!", Toast.LENGTH_SHORT).show()
                    }.addOnSuccessListener {
                        Toast.makeText(this, "Sucesso ao fazer upload da imagem!", Toast.LENGTH_SHORT).show()
                        imagemRef.downloadUrl.addOnCompleteListener {
                            atualizaFotoUsuario(it.result)
                        }
                    }
                }
            }catch (e: Exception){
                e.printStackTrace()
            }
        }
    }

    fun atualizaFotoUsuario(url: Uri) {
        if (UsuarioFirebase.atualizarFotoUsuario(url)){
            usuarioLogado.foto = url.toString()
            usuarioLogado.atualizar()
            Toast.makeText(this, "Sua foto foi alterada com sucesso!", Toast.LENGTH_SHORT).show()
            
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
        builder.setMessage("Para utilizar o app é necessário aceitar as permissões!")
        builder.setCancelable(false)
        builder.setPositiveButton("Confirmar", DialogInterface.OnClickListener { dialog, which ->
            finish()
        })
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
}