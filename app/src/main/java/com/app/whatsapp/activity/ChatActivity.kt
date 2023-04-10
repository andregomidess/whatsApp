package com.app.whatsapp.activity

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.app.whatsapp.R
import com.app.whatsapp.config.ConfiguracaoFirebase
//import com.app.whatsapp.activity.databinding.ActivityChatBinding
import com.app.whatsapp.databinding.ActivityChatBinding
import com.app.whatsapp.helper.Base64Custom
import com.app.whatsapp.helper.UsuarioFirebase
import com.app.whatsapp.model.Mensagem
import com.app.whatsapp.model.Usuario
import com.bumptech.glide.Glide
import com.google.firebase.database.DatabaseReference


class ChatActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityChatBinding
    private lateinit var usuarioDestinatario: Usuario

    //id usuarios remetente e destinatario
    private lateinit var idUsuarioRemetente: String
    private lateinit var idUsuarioDestinatario: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbar.title = ""
        setSupportActionBar(binding.toolbar)

        binding.contentChat.fabEnviar.drawable?.setTint(ContextCompat.getColor(this, R.color.white))

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //recupera dados do usuario remetente
        idUsuarioRemetente = UsuarioFirebase.getIdUsuario()

        //config. iniciais
        //Recuperar dados do usuário destinatario
        val bundle: Bundle? = intent.extras
        if (bundle != null) {
            usuarioDestinatario = bundle.getSerializable("chatContato") as Usuario
            binding.textViewNomeChat.text = usuarioDestinatario.nome
            val foto: String = usuarioDestinatario.foto
            if (foto != ""){
                val url: Uri = Uri.parse(usuarioDestinatario.foto)
                Glide.with(this@ChatActivity).load(url).into(binding.circleImageViewFoto)
            }else{
                binding.circleImageViewFoto.setImageResource(R.drawable.padrao)
            }

            //recuperar dados usuario destinatario
            idUsuarioDestinatario = Base64Custom.codificarString(usuarioDestinatario.email)

        }


    }

    fun enviarMensagem(view:View){
        val textoMsg = binding.contentChat.editMensagem.text.toString()
        if (textoMsg.isNotEmpty()){
            val mensagem: Mensagem = Mensagem()
            mensagem.idUsuario = idUsuarioRemetente
            mensagem.mensagem = textoMsg

            //salvar mensagem para o remetente
            salvarMensagem(idUsuarioRemetente, idUsuarioDestinatario, mensagem)
        }else{
            Toast.makeText(this@ChatActivity, "Digite uma mensagem para enviar!",
                Toast.LENGTH_LONG).show()
        }
    }

    fun salvarMensagem(idRemetente: String, idDestinatario: String, mensagem: Mensagem){
        val database: DatabaseReference = ConfiguracaoFirebase.getFirebaseDatabase()
        val mensagemRef: DatabaseReference = database.child("mensagens")

        mensagemRef.child(idRemetente)
            .child(idDestinatario)
            .push()
            .setValue(mensagem)
        //Limpar texto
        binding.contentChat.editMensagem.text.clear()
    }

}