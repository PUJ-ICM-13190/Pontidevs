package com.example.emprendenow

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.emprendenow.databinding.ActivityLogIn2Binding

class LogInActivity2 : AppCompatActivity() {
    private lateinit var binding: ActivityLogIn2Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLogIn2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        val boton = binding.btnLogin

        boton.setOnClickListener {
            val mail = binding.mail.text.toString()

            if (mail == "user") {
                val intent = Intent(this, ListaEmpresasActivity::class.java)
                startActivity(intent)
            } else if (mail == "emprendedor") {
                val intent = Intent(this, MenuEmprendedorActivity::class.java)
                startActivity(intent)
            }
        }
    }
}