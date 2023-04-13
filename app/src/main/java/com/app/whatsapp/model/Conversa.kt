package com.app.whatsapp.model

import com.app.whatsapp.config.ConfiguracaoFirebase
import com.google.firebase.database.DatabaseReference

class Conversa {
    var idRemetente: String = ""
    var idDestinatario: String = ""
    var ultimaMensagem: String = ""
    var usuarioExibicao: Usuario? = null

    public fun salvar(){
        val database: DatabaseReference = ConfiguracaoFirebase.getFirebaseDatabase()
        val conversaRef: DatabaseReference = database.child("conversas")
        conversaRef.child(this.idRemetente)
            .child(this.idDestinatario)
            .setValue(this)
    }


}