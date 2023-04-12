package com.app.whatsapp.activity

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.ui.AppBarConfiguration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.whatsapp.R
import com.app.whatsapp.adapter.MensagensAdapter
import com.app.whatsapp.config.ConfiguracaoFirebase
//import com.app.whatsapp.activity.databinding.ActivityChatBinding
import com.app.whatsapp.databinding.ActivityChatBinding
import com.app.whatsapp.helper.Base64Custom
import com.app.whatsapp.helper.UsuarioFirebase
import com.app.whatsapp.model.Mensagem
import com.app.whatsapp.model.Usuario
import com.bumptech.glide.Glide
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference


class ChatActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityChatBinding
    private lateinit var usuarioDestinatario: Usuario
    private lateinit var adapter: MensagensAdapter
    private lateinit var childEventListenerMensagens: ChildEventListener

    //id usuarios remetente e destinatario
    private lateinit var idUsuarioRemetente: String
    private lateinit var idUsuarioDestinatario: String
    private val mensagens : MutableList<Mensagem> = ArrayList()
    private lateinit var mensagensRef: DatabaseReference

    private lateinit var database: DatabaseReference

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

        //config adapter
        adapter = MensagensAdapter(mensagens, applicationContext)
        //config recyclerView
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(applicationContext)
        binding.contentChat.recyclerMensagens.layoutManager = layoutManager
        binding.contentChat.recyclerMensagens.setHasFixedSize(true)
        binding.contentChat.recyclerMensagens.adapter = adapter

        database = ConfiguracaoFirebase.getFirebaseDatabase()
        mensagensRef = database.child("mensagens")
            .child(idUsuarioRemetente)
            .child(idUsuarioDestinatario)


    }

    override fun onStart() {
        super.onStart()
        recuperarMensagens()
    }

    override fun onStop() {
        super.onStop()
        mensagensRef.removeEventListener(childEventListenerMensagens)
    }

    fun enviarMensagem(view:View){
        val textoMsg = binding.contentChat.editMensagem.text.toString()
        if (textoMsg.isNotEmpty()){
            val mensagem: Mensagem = Mensagem()
            mensagem.idUsuario = idUsuarioRemetente
            mensagem.mensagem = textoMsg

            //salvar mensagem para o remetente
            salvarMensagem(idUsuarioRemetente, idUsuarioDestinatario, mensagem)

            //salvar mensagem para o destinatario
            salvarMensagem(idUsuarioDestinatario, idUsuarioRemetente, mensagem)
        }else{
            Toast.makeText(this@ChatActivity, "Digite uma mensagem para enviar!",
                Toast.LENGTH_LONG).show()
        }
    }

    fun salvarMensagem(idRemetente: String, idDestinatario: String, mensagem: Mensagem){
        val database: DatabaseReference = ConfiguracaoFirebase.getFirebaseDatabase()

        mensagensRef.push().setValue(mensagem)
        //Limpar texto
        binding.contentChat.editMensagem.text.clear()
    }

    private fun recuperarMensagens(){
        childEventListenerMensagens = mensagensRef.addChildEventListener(object: ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val mensagem: Mensagem? = snapshot.getValue(Mensagem::class.java)
                if (mensagem != null) {
                    mensagens.add(mensagem)
                    adapter.notifyDataSetChanged()
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                TODO("Not yet implemented")
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

    }

}