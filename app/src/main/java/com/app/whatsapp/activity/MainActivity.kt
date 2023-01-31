package com.app.whatsapp.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.app.whatsapp.databinding.ActivityMainBinding
import com.app.whatsapp.helper.ConfiguracaoFirebase
import com.app.whatsapp.model.Usuario
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseUser

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var autho: FirebaseAuth
    private lateinit var usuario: Usuario

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        autho = ConfiguracaoFirebase.getFirebaseAuth()

        var campoEmail : String = binding.editTextEmailL.text.toString()
        var campoSenha : String = binding.editTextSenhaL.text.toString()

        binding.buttonLogar.setOnClickListener {
            if (campoEmail.isEmpty()){
                if (campoSenha.isEmpty()){
                    usuario = Usuario()
                    usuario.email = campoEmail
                    usuario.senha = campoSenha
                    validarLogin()
                }else{
                    Toast.makeText(this, "Digite a senha!", Toast.LENGTH_SHORT).show()
                }

            }else{
                Toast.makeText(this, "Digite o E-mail!", Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun onStart() {
        super.onStart()
        val user : FirebaseUser? = autho.currentUser
        if (user != null){
            abrirTelaPrincipal()
        }
    }

    private fun validarLogin() {
        autho.signInWithEmailAndPassword(usuario.email, usuario.senha).addOnCompleteListener( OnCompleteListener {

            if (it.isSuccessful){
                abrirTelaPrincipal()
            }else{
                var excecao = "";
                try{
                    throw it.exception!!;
                } catch (e: FirebaseAuthInvalidCredentialsException) {
                    excecao = "E-mail e senha não correspondem a um usuário cadastrado";
                } catch (e: FirebaseAuthInvalidUserException) {
                    excecao = "Usuário não está cadastrado";
                }catch (e: Exception){
                    excecao = "Erro ao cadastrar usuário " + e.message;
                    e.printStackTrace();
                }

                Toast.makeText(this, excecao, Toast.LENGTH_SHORT).show();
            }
        })
    }

    private fun abrirTelaPrincipal() {
        startActivity(Intent(this, PrincipalActivity::class.java))
    }

    fun telaCadasatro(view: View){
        startActivity(Intent(this, CadastroActivity::class.java))
    }
}