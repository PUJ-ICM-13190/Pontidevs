package com.example.emprendenow

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.emprendenow.databinding.ActivityAgregarEmprendimientoBinding
import com.example.emprendenow.databinding.ActivityLogIn2Binding
import com.google.firebase.database.FirebaseDatabase
import java.util.logging.Logger

class AgregarEmprendimientoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAgregarEmprendimientoBinding

    companion object {
        val TAG: String = AgregarEmprendimientoActivity::class.java.name
    }
    private val logger = Logger.getLogger(TAG)

    private val galleryActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val imageUri: Uri? = result.data!!.data
            logger.info("Image loaded successfully")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAgregarEmprendimientoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val userId = intent.getStringExtra("user")
        val name = binding.name
        val description = binding.description
        val enviar = binding.enviar
        val add_producto = binding.addProducts
        val add_logo = binding.addLogo

        add_logo.setOnClickListener {
            val pickGalleryImage = Intent(Intent.ACTION_PICK)
            pickGalleryImage.type = "image/*"
            galleryActivityResultLauncher.launch(pickGalleryImage)
        }

        add_producto.setOnClickListener {
            val intent = Intent(this, AgregarProductoActivity::class.java)
            intent.putExtra("user", userId)
            startActivity(intent)
        }

        enviar.setOnClickListener {
            val emprenName = name.text.toString()
            val emprenDescription = description.text.toString()

            if (userId != null && emprenName.isNotBlank() && emprenDescription.isNotBlank()) {
                addEmprendimientoToUser(userId, emprenName, emprenDescription)
            } else {
                Toast.makeText(this, "Llenar todos los campos", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun addEmprendimientoToUser(userId: String, name: String, description: String) {
        val database = FirebaseDatabase.getInstance().getReference("users/$userId/emprendimiento")
        val emprendimientoId = database.push().key

        val datos = hashMapOf(
            "name" to name,
            "description" to description
        )
        database.setValue(datos)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Emprendimiento añadido", Toast.LENGTH_SHORT).show()
                    binding.name.text.clear()
                    binding.description.text.clear()
                } else {
                    Toast.makeText(this, "No se pudo añadir el emprendimiento", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
            }
    }
}