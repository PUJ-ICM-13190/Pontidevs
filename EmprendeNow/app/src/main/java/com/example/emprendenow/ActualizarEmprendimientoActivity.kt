package com.example.emprendenow

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.emprendenow.databinding.ActivityActualizarEmprendimientoBinding
import com.google.firebase.database.FirebaseDatabase

class ActualizarEmprendimientoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityActualizarEmprendimientoBinding
    private val database = FirebaseDatabase.getInstance()
    private val usersRef = database.getReference("users")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityActualizarEmprendimientoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val userId = intent.getStringExtra("user") ?: return

        val nameField = binding.name
        val descriptionField = binding.description

        loadCurrentData(userId)

        // Configurar el botón para guardar los cambios
        binding.enviar.setOnClickListener {
            val newName = nameField.text.toString()
            val newDescription = descriptionField.text.toString()

            // Crear un map con los nuevos datos
            val updatedData = mapOf(
                "emprendimiento/name" to newName,
                "emprendimiento/description" to newDescription
            )

            // Actualizar el emprendimiento del usuario en la Realtime Database
            usersRef.child(userId).updateChildren(updatedData)
                .addOnSuccessListener {
                    Toast.makeText(this, "Emprendimiento actualizado", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error al actualizar: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun loadCurrentData(userId: String) {
        // Cargar los datos actuales del emprendimiento
        usersRef.child(userId).child("emprendimiento").get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    val currentName = snapshot.child("name").getValue(String::class.java)
                    val currentDescription = snapshot.child("description").getValue(String::class.java)

                    // Mostrar los valores en los campos del formulario
                    binding.name.setText(currentName)
                    binding.description.setText(currentDescription)
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al cargar los datos: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}