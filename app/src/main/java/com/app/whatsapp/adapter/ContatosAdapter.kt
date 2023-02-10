package com.app.whatsapp.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.whatsapp.R
import com.app.whatsapp.model.Usuario
import com.bumptech.glide.Glide
import com.google.firebase.database.DatabaseReference
import de.hdodenhof.circleimageview.CircleImageView

class ContatosAdapter(private val listaContatos: ArrayList<Usuario>, private val c: Context?) : RecyclerView.Adapter<ContatosAdapter.MyviewHolder>() {


    private val contatos: ArrayList<Usuario> = this.listaContatos
    private val context: Context? = this.c

    class MyviewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val foto: CircleImageView = itemView.findViewById(R.id.imageViewFotoContato)
        val nome: TextView = itemView.findViewById(R.id.textViewNomeContato)
        val email: TextView = itemView.findViewById(R.id.textViewEmailContato)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyviewHolder {
        val itemLista: View = LayoutInflater.from(parent.context).inflate(R.layout.adapter_contatos, parent, false)
        return MyviewHolder(itemLista)
    }

    override fun onBindViewHolder(holder: MyviewHolder, position: Int) {
        holder.foto.setImageResource(R.drawable.padrao)
        val usuario = contatos[position]
        holder.nome.text = usuario.nome
        holder.email.text = usuario.email
            val uri: Uri = Uri.parse(usuario.foto)
            if (context != null) {
                Glide.with(context).load(uri).into(holder.foto)
            }else{
                holder.foto.setImageResource(R.drawable.padrao)
            }
    }

    override fun getItemCount(): Int {
        return contatos.size
    }
}