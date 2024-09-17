package com.example.emprendenow

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.emprendenow.databinding.ActivityCrearCuentaBinding

class CrearCuentaActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCrearCuentaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCrearCuentaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val spinner = binding.userType
        val btn_register = binding.btnRegister
        val btn_login = binding.btnLogin

        val tipos_usuarios = resources.getStringArray(R.array.tipos_usuarios)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, tipos_usuarios)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        btn_register.setOnClickListener {

        }

        btn_login.setOnClickListener {
            intent = Intent(this, LogInActivity2::class.java)
            startActivity(intent)
        }
    }
}