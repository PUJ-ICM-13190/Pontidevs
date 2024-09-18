package com.example.emprendenow

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.emprendenow.databinding.ActivityActualizarEmprendimientoBinding

class ActualizarEmprendimientoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityActualizarEmprendimientoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityActualizarEmprendimientoBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}