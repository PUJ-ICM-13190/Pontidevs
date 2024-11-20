package com.example.emprendenow

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.emprendenow.databinding.ActivityMenuEmprendedorBinding
import com.google.android.material.navigation.NavigationBarView

class MenuEmprendedorActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMenuEmprendedorBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuEmprendedorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val userId = intent.getStringExtra("user")
        val btn = binding.button
        val navbar = binding.bottomNavigation
        val update = binding.settingIcon
        val location = binding.locationIcon

        navbar.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    true
                }

                R.id.chat -> {
                    val intent = Intent(this,ListaChatsEmprendedor::class.java)
                    startActivity(intent)
                    true
                }

                R.id.account -> {
                    val intent = Intent(this, CuentaEmprenActivity::class.java)
                    intent.putExtra("user", userId)
                    startActivity(intent)
                    true
                }

                else -> false
            }
        }

        btn.setOnClickListener {
            val intent = Intent(this, AgregarEmprendimientoActivity::class.java)
            intent.putExtra("user", userId)
            startActivity(intent)
        }

        update.setOnClickListener {
            val intent = Intent(this, ActualizarEmprendimientoActivity::class.java)
            intent.putExtra("user", userId)
            startActivity(intent)
        }

        location.setOnClickListener {
            val intent = Intent(this, MapaEmpresaActivity::class.java)
            intent.putExtra("user", userId)
            startActivity(intent)
        }
    }
}