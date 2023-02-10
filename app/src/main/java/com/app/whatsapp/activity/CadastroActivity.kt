package com.app.whatsapp.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.app.whatsapp.databinding.ActivityCadastroBinding
import com.app.whatsapp.config.ConfiguracaoFirebase
import com.app.whatsapp.helper.Base64Custom
import com.app.whatsapp.helper.UsuarioFirebase
import com.app.whatsapp.model.Usuario
import com.google.firebase.auth.*

class CadastroActivity : AppCompatActivity() {

    private lateinit var binding : ActivityCadastroBinding
    private lateinit var autenticacao : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCadastroBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


    }

    fun cadastrarUsuario(usuario: Usuario){
        autenticacao = ConfiguracaoFirebase.getFirebaseAuth()
        autenticacao.createUserWithEmailAndPassword(usuario.email, usuario.senha).addOnCompleteListener(this){ task ->
            if (task.isSuccessful){
                try {
                    val idUsuario: String = Base64Custom.codificarString(usuario.email)
                    usuario.uid = idUsuario
                    usuario.salvar()
                }catch (e:Exception){
                    e.printStackTrace()
                }

                Toast.makeText(this, "Usuário cadastrado com sucesso!", Toast.LENGTH_SHORT).show()
                UsuarioFirebase.atualizarNomeUsuario(usuario.nome)
                finish()
            }else{
                var excecao = "";
                try{
                    throw task.exception!!
                }catch (e: FirebaseAuthWeakPasswordException){
                    excecao = "Digite uma senha mais forte!"
                } catch (e : FirebaseAuthInvalidCredentialsException){
                    excecao = "Por favor, digite um E-mail válido!"
                }catch (e: FirebaseAuthUserCollisionException){
                    excecao = "Este usuário já existe"
                }catch (e: Exception){
                    excecao = "Erro ao cadastrar usuário " + e.message
                    e.printStackTrace()
                }

                Toast.makeText(this, excecao, Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun validarCadastroUsuario(view: View){
        val textoNome: String = binding.editTextNomeC.text.toString()
        val textoEmail: String = binding.editTextEmailC.text.toString()
        val textoSenha: String = binding.editTextSenhaC.text.toString()

        if (!textoNome.isEmpty()){
            if (!textoEmail.isEmpty()){
                if (!textoSenha.isEmpty()){

                    val usuario = Usuario()
                    usuario.nome = textoNome
                    usuario.email = textoEmail
                    usuario.senha = textoSenha
                    cadastrarUsuario(usuario)

                } else{
                    Toast.makeText(this, "Digite a senha!", Toast.LENGTH_SHORT).show()
                }

            }else{
                Toast.makeText(this, "Digite o E-mail!", Toast.LENGTH_SHORT).show()
            }

        }else{
            Toast.makeText(this, "Digite o nome!", Toast.LENGTH_SHORT).show()
        }
    }
}