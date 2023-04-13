package com.app.whatsapp.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.whatsapp.R
import com.app.whatsapp.activity.ChatActivity
import com.app.whatsapp.adapter.ConversasAdapter
import com.app.whatsapp.config.ConfiguracaoFirebase
import com.app.whatsapp.helper.RecyclerItemClickListener
import com.app.whatsapp.helper.UsuarioFirebase
import com.app.whatsapp.model.Conversa
import com.app.whatsapp.model.Usuario
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*

class ConversasFragment : Fragment() {

    private lateinit var recyclerViewListaConversas: RecyclerView
    private lateinit var conversasAdapter: ConversasAdapter
    val listaConversas = ArrayList<Conversa>()
    private lateinit var database: DatabaseReference
    private lateinit var conversasRef: DatabaseReference
    private lateinit var childEventListenerConversas: ChildEventListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_conversas, container, false)
        recyclerViewListaConversas = view.findViewById(R.id.recyclerViewConversas)


        //cofig Adapter
        conversasAdapter = ConversasAdapter(listaConversas, context)


        //config recyclerView
        val layoutManager : RecyclerView.LayoutManager = LinearLayoutManager(activity)
        recyclerViewListaConversas.layoutManager = layoutManager
        recyclerViewListaConversas.setHasFixedSize(true)
        recyclerViewListaConversas.adapter = conversasAdapter

        //Configurar evento de clique
        recyclerViewListaConversas.addOnItemTouchListener(
            RecyclerItemClickListener(
                activity,
                recyclerViewListaConversas,
                object : RecyclerItemClickListener.OnItemClickListener {
                    override fun onItemClick(view: View?, position: Int) {
                        val conversaSelecionada: Conversa = listaConversas[position]
                        val i: Intent = Intent(activity, ChatActivity::class.java)
                        i.putExtra("chatContato", conversaSelecionada.usuarioExibicao)
                        startActivity(i)
                    }
                    override fun onLongItemClick(view: View?, position: Int) {}
                    override fun onItemClick(parent: AdapterView<*>?, view: View, position: Int, id: Long) {

                    }
                }
            )
        )

        //config conversasRef
        val idUsuario: String = UsuarioFirebase.getIdUsuario()
        database = ConfiguracaoFirebase.getFirebaseDatabase()
        conversasRef = database.child("conversas").child(idUsuario)

        return view

    }

    override fun onStart() {
        super.onStart()
        recuperarConversas()
    }

    override fun onStop() {
        super.onStop()
        conversasRef.removeEventListener(childEventListenerConversas)
    }

    fun recuperarConversas(){
        listaConversas.clear()
        childEventListenerConversas = conversasRef.addChildEventListener(object : ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val conversa = snapshot.getValue(Conversa::class.java)
                if (conversa != null) {
                    listaConversas.add(conversa)
                    conversasAdapter.notifyDataSetChanged()

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