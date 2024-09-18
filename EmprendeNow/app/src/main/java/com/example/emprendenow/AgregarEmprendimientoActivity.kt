package com.example.emprendenow

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.emprendenow.databinding.ActivityAgregarEmprendimientoBinding
import com.example.emprendenow.databinding.ActivityLogIn2Binding

class AgregarEmprendimientoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAgregarEmprendimientoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAgregarEmprendimientoBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }
}