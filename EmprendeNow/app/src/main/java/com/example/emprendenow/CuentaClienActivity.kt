package com.example.emprendenow

import android.content.Intent
import android.net.Uri
import java.io.File
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.emprendenow.databinding.ActivityCuentaClienBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.util.logging.Logger

class CuentaClienActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCuentaClienBinding
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mStorage: FirebaseStorage
    private var imageUri: Uri? = null

    var imageViewContainer: ImageView? = null

    companion object {
        val TAG: String = AgregarProductoActivity::class.java.name
    }
    private val logger = Logger.getLogger(TAG)

    private val galleryActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val imageUri: Uri? = result.data?.data
            if (imageUri != null) {
                imageViewContainer?.setImageURI(imageUri)
                logger.info("Image loaded successfully")

                uploadImageToFirebase(imageUri)
            } else {
                logger.warning("No image selected")
            }
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

        mStorage = FirebaseStorage.getInstance()
        mAuth = Firebase.auth
        loadProfileImage()
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
                    intent.putExtra("user", userId)
                    startActivity(intent)
                    true
                }
                R.id.chat -> {
                    val intent = Intent(this,ListaChatsActivity::class.java)
                    startActivity(intent)
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

    private fun uploadImageToFirebase(imageUri: Uri) {
        val userId = mAuth.currentUser?.uid ?: run {
            logger.warning("El usuario no está autenticado.")
            return
        }
        logger.info("userid: $userId")

        var storageRef = mStorage.reference
        val pictureRef = storageRef.child("profile/$userId.jpg")

        try {
            val fileExists = try {
                contentResolver.openInputStream(imageUri)?.close()
                true
            } catch (e: Exception) {
                logger.warning("No se pudo abrir el archivo: ${e.message}")
                false
            }

            if (!fileExists) {
                logger.warning("El archivo seleccionado no existe.")
                return
            }
            logger.info("URI de la imagen: $imageUri")

            pictureRef.putFile(imageUri)
                .addOnSuccessListener {
                    pictureRef.downloadUrl.addOnSuccessListener { uri ->
                        val downloadUrl = uri.toString()
                        logger.info("Imagen subida con éxito: $downloadUrl")
                    }
                }
                .addOnFailureListener { e ->
                    logger.severe("Fallo al subir la imagen al storage: ${e.message}")
                }
        } catch (e: Exception) {
            logger.severe("Error al intentar subir la imagen: ${e.message}")
        }
    }

    private fun loadProfileImage() {

        val userId = mAuth.currentUser?.uid ?: run {
            logger.warning("El usuario no está autenticado.")
            return
        }
        logger.info("userid: $userId")
        // Referencia al archivo en Firebase Storage
        val storageRef = FirebaseStorage.getInstance().reference.child("profile/$userId.jpg")

        // Intentar obtener la URL de descarga
        storageRef.downloadUrl
            .addOnSuccessListener { uri ->
                // Si se encuentra la imagen, cargarla en el ImageView usando Glide
                Glide.with(this)
                    .load(uri)
                    .placeholder(R.drawable.user_icon) // Imagen mientras se descarga
                    .error(R.drawable.user_icon) // Imagen por defecto si falla
                    .into(binding.profilePic)
            }
            .addOnFailureListener { exception ->
                // Si no se encuentra la imagen o ocurre un error, dejar la imagen por defecto
                logger.warning("No se encontró la imagen: ${exception.message}")
            }
    }
}