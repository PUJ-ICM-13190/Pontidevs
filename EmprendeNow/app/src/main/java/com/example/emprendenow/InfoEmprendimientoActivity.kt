package com.example.emprendenow

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.emprendenow.databinding.ActivityComprarProductoBinding
import com.example.emprendenow.databinding.ActivityInfoEmprendimientoBinding

class InfoEmprendimientoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityInfoEmprendimientoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInfoEmprendimientoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navbar = binding.bottomNavigation
        val location =  binding.locationButton

        navbar.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    val intent = Intent(this, ListaEmpresasActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.chat -> {
                    true
                }
                R.id.account -> {
                    val intent = Intent(this, CuentaClienActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }

        location.setOnClickListener {
            val intent = Intent(this, MapaClienteActivity::class.java)
            startActivity(intent)
        }
    }
}