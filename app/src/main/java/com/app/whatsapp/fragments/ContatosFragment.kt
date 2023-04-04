package com.app.whatsapp.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.whatsapp.R
import com.app.whatsapp.activity.ChatActivity
import com.app.whatsapp.adapter.ContatosAdapter
import com.app.whatsapp.config.ConfiguracaoFirebase
import com.app.whatsapp.helper.RecyclerItemClickListener
import com.app.whatsapp.helper.RecyclerItemClickListener.*
import com.app.whatsapp.helper.UsuarioFirebase
import com.app.whatsapp.model.Usuario
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener


class ContatosFragment : Fragment() {

    private lateinit var recyclerViewListaContatos: RecyclerView
    private lateinit var adapter: ContatosAdapter
    val listaContatos = ArrayList<Usuario>()
    private lateinit var usuarioRef: DatabaseReference
    private lateinit var valueEventListenerContatos: ValueEventListener
    private lateinit var usuarioAtual: FirebaseUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_contatos, container, false)
        //configs iniciais
        recyclerViewListaContatos = view.findViewById(R.id.recyclerViewListaContatos)

        usuarioRef = ConfiguracaoFirebase.getFirebaseDatabase().child("usuario")
        usuarioAtual = UsuarioFirebase.getUsuarioFirebase()


        //config adapter
        adapter = ContatosAdapter(listaContatos, context)

        //config recyclerView
        val layoutManager : RecyclerView.LayoutManager = LinearLayoutManager(activity)
        recyclerViewListaContatos.layoutManager = layoutManager
        recyclerViewListaContatos.setHasFixedSize(true)
        recyclerViewListaContatos.adapter = adapter

        //config evento de clique no recyclerview
        recyclerViewListaContatos.addOnItemTouchListener(
            RecyclerItemClickListener(
                activity,
                recyclerViewListaContatos,
                object : OnItemClickListener {
                    override fun onItemClick(view: View?, position: Int) {
                        val i: Intent = Intent(activity, ChatActivity::class.java)
                        startActivity(i)
                    }
                    override fun onLongItemClick(view: View?, position: Int) {}
                    override fun onItemClick(parent: AdapterView<*>?, view: View, position: Int, id: Long) {

                    }
                }
            )
        )




        return view
    }

    override fun onStart() {
        super.onStart()
        recuperarContatos()
    }

    override fun onStop() {
        super.onStop()
        usuarioRef.removeEventListener(valueEventListenerContatos)
    }

    private fun recuperarContatos(){
        valueEventListenerContatos =  usuarioRef.addValueEventListener(object: ValueEventListener{
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                listaContatos.clear()
                for (dados: DataSnapshot in snapshot.children){

                    val usuario: Usuario? = dados.getValue(Usuario::class.java)
                    if (usuario != null) {
                        if (!usuarioAtual.email.equals(usuario.email))
                            listaContatos.add(usuario)
                    }
                }
                adapter.notifyDataSetChanged()
            }


            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }


}