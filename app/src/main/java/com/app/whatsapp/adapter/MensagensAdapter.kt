package com.app.whatsapp.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.whatsapp.R
import com.app.whatsapp.helper.UsuarioFirebase
import com.app.whatsapp.model.Mensagem
import com.bumptech.glide.Glide
import com.google.firebase.database.core.Context

class MensagensAdapter(lista: List<Mensagem>, c: android.content.Context) :
    RecyclerView.Adapter<MensagensAdapter.MyViewHolder>() {

    private var mensagens : List<Mensagem> = lista
    private var context: android.content.Context = c
    companion object{
        const val TIPO_REMETENTE = 0
        const val TIPO_DESTINATARIO = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        var item: View? = null
        if (viewType == TIPO_REMETENTE){
            item = LayoutInflater
                .from(parent.context)
                .inflate(R.layout.adapter_mensagem_remetente, parent, false)
        }else if (viewType == TIPO_DESTINATARIO){
            item = LayoutInflater
                .from(parent.context)
                .inflate(R.layout.adapter_mensagem_destinatario, parent, false)
        }

        return MyViewHolder(item!!)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val mensagem: Mensagem = mensagens[position]
        val msg = mensagem.mensagem
        val imagem = mensagem.mensagem
        if (imagem != ""){
            val url: Uri = Uri.parse(imagem)
            Glide.with(context).load(url).into(holder.imagem)
            //esconder o texto
            holder.mensagem.visibility = View.GONE
        }else{
            holder.mensagem.text = msg
            //esconder a imagem
            holder.imagem.visibility = View.GONE
        }


    }

    override fun getItemCount(): Int {
        return mensagens.size
    }

    override fun getItemViewType(position: Int): Int {
        val mensagem: Mensagem = mensagens[position]

        val idUsuario: String = UsuarioFirebase.getIdUsuario()
        if(idUsuario == mensagem.idUsuario)
            return TIPO_REMETENTE
        return TIPO_DESTINATARIO
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val mensagem: TextView = itemView.findViewById(R.id.textMensagemTexto)
        val imagem: ImageView = itemView.findViewById(R.id.imageMensagemFoto)

    }
}