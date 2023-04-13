package com.app.whatsapp.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.whatsapp.R
import com.app.whatsapp.model.Conversa
import com.app.whatsapp.model.Usuario
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView

class ConversasAdapter(lista: ArrayList<Conversa>, c: Context?): RecyclerView.Adapter<ConversasAdapter.MyViewHolder>() {

    private val conversas: ArrayList<Conversa> = lista
    private val context: Context? = c

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val foto: CircleImageView = itemView.findViewById(R.id.imageViewFotoContato)
        val nome: TextView = itemView.findViewById(R.id.textViewNomeContato)
        val ultimaMensagem: TextView = itemView.findViewById(R.id.textViewEmailContato)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemLista: View = LayoutInflater.from(parent.context).inflate(R.layout.adapter_contatos, parent, false)
        return MyViewHolder(itemLista)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val conversa: Conversa = conversas[position]
        holder.ultimaMensagem.text = conversa.ultimaMensagem

        val usuario = conversa.usuarioExibicao
        holder.nome.text = usuario!!.nome
        val uri: Uri = Uri.parse(usuario.foto)
        if (usuario.foto != ""){
            if (context != null) {
                Glide.with(context).load(uri).into(holder.foto)
            }

        }else{
            holder.foto.setImageResource(R.drawable.padrao)
        }

    }

    override fun getItemCount(): Int {
        return conversas.size
    }

}