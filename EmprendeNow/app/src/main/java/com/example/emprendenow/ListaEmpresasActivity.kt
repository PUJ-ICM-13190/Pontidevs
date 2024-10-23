package com.example.emprendenow

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.emprendenow.databinding.ActivityListaEmpresasBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ListaEmpresasActivity : AppCompatActivity() {
    private lateinit var binding: ActivityListaEmpresasBinding
    private lateinit var databaseRef: DatabaseReference
    private val listaEmpresas = mutableListOf<Empresa>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListaEmpresasBinding.inflate(layoutInflater)
        setContentView(binding.root)

        databaseRef = FirebaseDatabase.getInstance().getReference("users")

        listarEmpresas()

        val navbar = binding.bottomNavigation
        val adapter = AdaptadorEmpresa(this, listaEmpresas)
        binding.listView.adapter = adapter

        navbar.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
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
    }

    private fun listarEmpresas() {
        // Lee los datos de la base de datos
        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listaEmpresas.clear()

                for (empresaSnapshot in snapshot.children) {
                    val userType = empresaSnapshot.child("userType").getValue(String::class.java)
                    if (userType == "Empresa") {
                        val name = empresaSnapshot.child("emprendimiento/name").getValue(String::class.java) ?: "Sin nombre"
                        val descripcion = empresaSnapshot.child("emprendimiento/description").getValue(String::class.java) ?: "Sin descripción"
                        val logo = "logo"

                        val empresa = Empresa(
                            name = name,
                            logo = logo,
                            descripcion = descripcion
                        )

                        listaEmpresas.add(empresa)
                    }
                }

                (binding.listView.adapter as AdaptadorEmpresa).notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ListaEmpresasActivity, "Error al leer los datos", Toast.LENGTH_SHORT).show()
            }
        })
    }

    data class Empresa (
        val name: String,
        val logo: String,
        val descripcion: String
    )
}