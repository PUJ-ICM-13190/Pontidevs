package com.example.emprendenow

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.emprendenow.databinding.ActivityCuentaClienBinding

class CuentaClienActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCuentaClienBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCuentaClienBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navbar = binding.bottomNavigation

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
                    true
                }
                else -> false
            }
        }
    }
}