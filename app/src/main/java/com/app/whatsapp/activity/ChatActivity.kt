package com.app.whatsapp.activity

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
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
import com.app.whatsapp.model.Conversa
import com.app.whatsapp.model.Mensagem
import com.app.whatsapp.model.Usuario
import com.bumptech.glide.Glide
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream
import java.util.UUID


class ChatActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityChatBinding
    private lateinit var usuarioDestinatario: Usuario
    private lateinit var adapter: MensagensAdapter
    private lateinit var childEventListenerMensagens: ChildEventListener
    private lateinit var storage: StorageReference

    //id usuarios remetente e destinatario
    private lateinit var idUsuarioRemetente: String
    private lateinit var idUsuarioDestinatario: String
    private val mensagens : MutableList<Mensagem> = ArrayList()
    private lateinit var mensagensRef: DatabaseReference

    private lateinit var database: DatabaseReference
    companion object{
        val SELECAO_CAMERA: Int = 100
    }

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
        binding.contentChat.recyclerMensagens.recycledViewPool.setMaxRecycledViews(0,0);
        binding.contentChat.recyclerMensagens.adapter = adapter

        database = ConfiguracaoFirebase.getFirebaseDatabase()
        mensagensRef = database.child("mensagens")
            .child(idUsuarioRemetente)
            .child(idUsuarioDestinatario)

        storage = ConfiguracaoFirebase.getFirebaseStorage()

        //Evento de clique na camera
        binding.contentChat.imageCamera.setOnClickListener {
            val i: Intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if(i.resolveActivity(packageManager) != null){
                startActivityForResult(i, SELECAO_CAMERA)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == RESULT_OK){
            var imagem: Bitmap? = null
            try {
                when (requestCode) {
                    SELECAO_CAMERA -> {
                        if (data != null) {
                            imagem = data.extras?.get("data") as Bitmap?
                        }
                    }
                }
                if (imagem != null){
                    //Recuperar dados da img para o firebase
                    val baos: ByteArrayOutputStream = ByteArrayOutputStream()
                    imagem.compress(Bitmap.CompressFormat.JPEG, 70, baos)
                    val dadosImg: ByteArray = baos.toByteArray()

                    //criar nome da imagem
                    val nomeImagem: String = UUID.randomUUID().toString()

                    //config referencias do firebase
                    val imagemRef: StorageReference = storage.child("imagens")
                        .child("fotos")
                        .child(idUsuarioRemetente)
                        .child(nomeImagem)
                    val uploadTask = imagemRef.putBytes(dadosImg)
                    uploadTask.addOnFailureListener {
                        Log.d("Erro", "Erro ao fazer upload")
                        Toast.makeText(this, "Erro ao fazer upload da imagem!", Toast.LENGTH_SHORT).show()
                    }.addOnSuccessListener {
                        imagemRef.downloadUrl.addOnCompleteListener {
                            val downloadUrl = it.result
                            val mensagem: Mensagem = Mensagem()
                            mensagem.idUsuario = idUsuarioRemetente
                            mensagem.mensagem = "imagem.jpeg"
                            mensagem.imagem = downloadUrl.toString()

                            salvarMensagem(idUsuarioRemetente, idUsuarioDestinatario, mensagem)

                            salvarMensagem(idUsuarioDestinatario, idUsuarioRemetente, mensagem)
                            Toast.makeText(this, "Sucesso ao enviar a imagem!", Toast.LENGTH_SHORT).show()
                        }

                    }
                }
            }catch (e: Exception){
                e.printStackTrace()
            }
        }
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

            //salvar conversa
            salvarConversa(mensagem)
        }else{
            Toast.makeText(this@ChatActivity, "Digite uma mensagem para enviar!",
                Toast.LENGTH_LONG).show()
        }
    }

    private fun salvarConversa(msg: Mensagem) {
        val conversaRemetente: Conversa = Conversa()
        conversaRemetente.idRemetente = idUsuarioRemetente
        conversaRemetente.idDestinatario = idUsuarioDestinatario
        conversaRemetente.ultimaMensagem = msg.mensagem
        conversaRemetente.usuarioExibicao = usuarioDestinatario

        conversaRemetente.salvar()
    }

    fun salvarMensagem(idRemetente: String, idDestinatario: String, mensagem: Mensagem){
        val database: DatabaseReference = ConfiguracaoFirebase.getFirebaseDatabase()
        val mensagemRef = database.child("mensagens")

        mensagemRef.child(idRemetente)
            .child(idDestinatario)
            .push()
            .setValue(mensagem);
        //Limpar texto
        binding.contentChat.editMensagem.text.clear()
    }

    private fun recuperarMensagens(){
        mensagens.clear()
        childEventListenerMensagens = mensagensRef.addChildEventListener(object: ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val mensagem: Mensagem? = snapshot.getValue(Mensagem::class.java)
                mensagens.add(mensagem!!)
                adapter.notifyDataSetChanged()

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