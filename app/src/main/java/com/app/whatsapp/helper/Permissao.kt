package com.app.whatsapp.helper

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

object Permissao {

    fun validaPermissao(permissoes: Array<String>, activity: Activity, requestCode: Int): Boolean{

        if (Build.VERSION.SDK_INT >= 23){
            val listaPermissao : MutableList<String> = arrayListOf()
            permissoes.forEach {
                val temPermissao: Boolean =  ContextCompat.checkSelfPermission(activity, it) == PackageManager.PERMISSION_GRANTED
                if (!temPermissao) listaPermissao.add(it)
            }

            if (listaPermissao.isEmpty())
                return true
            val novasPermissoes: Array<String> = listaPermissao.toTypedArray()
            ActivityCompat.requestPermissions(activity, novasPermissoes, requestCode)

        }

        return true
    }
}