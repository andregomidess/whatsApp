package com.app.whatsapp.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import com.app.whatsapp.R
import com.app.whatsapp.databinding.ActivityPrincipalBinding
import com.app.whatsapp.fragments.ContatosFragment
import com.app.whatsapp.fragments.ConversasFragment
import com.app.whatsapp.helper.ConfiguracaoFirebase
import com.google.firebase.auth.FirebaseAuth
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems

class PrincipalActivity : AppCompatActivity() {

    private lateinit var binding : ActivityPrincipalBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrincipalBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        auth = ConfiguracaoFirebase.getFirebaseAuth()

        val toolbar:Toolbar = binding.toolbar.toolbarPrincipal
        toolbar.title = "WhatsApp"
        setSupportActionBar(toolbar)

        val adapter: FragmentPagerItemAdapter = FragmentPagerItemAdapter(
            supportFragmentManager,
            FragmentPagerItems.with(this)
                .add("Conversas", ConversasFragment::class.java)
                .add("Contatos", ContatosFragment::class.java)
                .create()

        )
        binding.viewpager.adapter = adapter
        binding.viewPagerTab.setViewPager(binding.viewpager)


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        val inflater:MenuInflater = menuInflater

        inflater.inflate(R.menu.menu_main, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId){
            R.id.menuSair -> {
                deslogarUsuario()
                finish()
            }
            R.id.menuConfiguracoes ->{
                abrirConfiguracao()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun abrirConfiguracao() {
        startActivity(Intent(this, ConfiguracoesActivity::class.java))
    }

    private fun deslogarUsuario() {
        try {
            auth.signOut()
        }catch (e: Exception){
            e.printStackTrace()
        }
    }
}