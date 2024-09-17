package com.example.emprendenow

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.emprendenow.databinding.ActivityListaEmpresasBinding

class ListaEmpresasActivity : AppCompatActivity() {
    private lateinit var binding: ActivityListaEmpresasBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListaEmpresasBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val listaEmpresas = listarEmpresas()

        val adapter = AdaptadorEmpresa(this, listaEmpresas)
        binding.listView.adapter = adapter
    }

    private fun listarEmpresas(): List<Empresa> {
        val empresas = mutableListOf<Empresa>()
        empresas.add(
            Empresa(
                name = "Google",
                logo = "https://img.icons8.com/?size=100&id=17949&format=png&color=000000",
                descripcion = "Google es una motor de busqueda de internet"
            )
        )
        empresas.add(
            Empresa(
                name = "Facebook",
                logo = "logo",
                descripcion = "Facebook es una red social para conectar con tus amigos y familiares"
            )
        )
        return empresas
    }

    data class Empresa (
        val name: String,
        val logo: String,
        val descripcion: String
    )
}