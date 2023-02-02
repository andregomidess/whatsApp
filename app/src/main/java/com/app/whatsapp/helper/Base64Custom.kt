package com.app.whatsapp.helper

import android.util.Base64

object Base64Custom {

    fun codificarString(texto:String) : String{
        return Base64.encodeToString(texto.toByteArray(), Base64.DEFAULT).replace("(\\n|\\r)".toRegex(), "")
    }

    fun decodigicarBase64(textoCodificado: String) : String{
        return String(Base64.decode(textoCodificado, Base64.DEFAULT))
    }
}