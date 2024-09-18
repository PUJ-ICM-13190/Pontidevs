package com.example.emprendenow

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.emprendenow.databinding.ActivityCuentaEmprenBinding

class CuentaEmprenActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCuentaEmprenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCuentaEmprenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navbar = binding.bottomNavigation

        navbar.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    val intent = Intent(this, MenuEmprendedorActivity::class.java)
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