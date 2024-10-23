package com.example.emprendenow

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.emprendenow.databinding.ActivityCuentaClienBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.FirebaseDatabase
import java.util.logging.Logger

class CuentaClienActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCuentaClienBinding
    private lateinit var mAuth: FirebaseAuth

    var imageViewContainer: ImageView? = null

    companion object {
        val TAG: String = AgregarProductoActivity::class.java.name
    }
    private val logger = Logger.getLogger(TAG)

    private val galleryActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val imageUri: Uri? = result.data!!.data
            imageViewContainer!!.setImageURI(imageUri)
            logger.info("Image loaded successfully")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCuentaClienBinding.inflate(layoutInflater)
        setContentView(binding.root)

        imageViewContainer = binding.profilePic
        val navbar = binding.bottomNavigation
        val name = binding.textName
        val mail = binding.textMail
        val sign_out = binding.signOut
        val change_img = binding.cambiarImagen
        val userId = intent.getStringExtra("user")

        if (userId != null) {
            getUserData(userId)
        }

        mAuth = Firebase.auth
        sign_out.setOnClickListener {
            mAuth.signOut()
            val intent = Intent(this, CrearCuentaActivity::class.java)
            startActivity(intent)
        }

        change_img.setOnClickListener {
            val pickGalleryImage = Intent(Intent.ACTION_PICK)
            pickGalleryImage.type = "image/*"
            galleryActivityResultLauncher.launch(pickGalleryImage)
        }

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

    private fun getUserData(userId: String) {
        val databaseReference = FirebaseDatabase.getInstance().getReference("users/$userId")

        databaseReference.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val userSnapshot = task.result
                if (userSnapshot != null && userSnapshot.exists()) {
                    val name = userSnapshot.child("name").value.toString()
                    val email = userSnapshot.child("email").value.toString()

                    binding.textName.text = name
                    binding.textMail.text = email
                }
            }
        }
    }
}