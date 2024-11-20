package com.example.emprendenow

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.emprendenow.databinding.ActivityAgregarProductoBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.logging.Logger

class AgregarProductoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAgregarProductoBinding
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mStorage: FirebaseStorage
    var pictureImagePath: Uri? = null

    companion object {
        val TAG: String = AgregarProductoActivity::class.java.name
    }
    private val logger = Logger.getLogger(TAG)

    private val getSimplePermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()) {
        updateUI(it)
    }

    private val cameraActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val newUri = Uri.parse(pictureImagePath.toString() + "?time=" + System.currentTimeMillis())
            pictureImagePath = newUri
            logger.info("Image capture successfully")
        } else {
            logger.warning("Capture failed")
        }
    }

    private val galleryActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val imageUri: Uri? = result.data?.data
            if (imageUri != null) {
                logger.info("Image loaded successfully")
                pictureImagePath = imageUri
            } else {
                logger.warning("No image selected")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAgregarProductoBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val userId = intent.getStringExtra("user")
        val addBtn = binding.add
        val back = binding.back
        val camera = binding.buttonTake
        val galery = binding.buttonGalery

        mAuth = Firebase.auth

        camera.setOnClickListener {
            verifyPermissions(this, android.Manifest.permission.CAMERA, "El permiso es requerido para capturar la foto")
        }

        galery.setOnClickListener {
            val pickGalleryImage = Intent(Intent.ACTION_PICK)
            pickGalleryImage.type = "image/*"
            galleryActivityResultLauncher.launch(pickGalleryImage)
        }

        addBtn.setOnClickListener {
            val productName = binding.name.text.toString()
            val productPrice = binding.price.text.toString().toDoubleOrNull() ?: 0.0
            val productDescription = binding.description.text.toString()
            val nuevoProducto = Producto(name = productName, price = productPrice, description = productDescription)
            if (userId != null) {
                agregarProducto(userId, nuevoProducto)
                pictureImagePath?.let { uploadImageToFirebase(it) }
            }
        }

        back.setOnClickListener {
            val intent = Intent(this, AgregarEmprendimientoActivity::class.java)
            startActivity(intent)
        }
    }

    private fun verifyPermissions(context: Context, permission: String, rationale: String) {
        when {
            ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED -> {
                Snackbar.make(binding.root, "Ya tengo los permisos", Snackbar.LENGTH_SHORT).show()
                updateUI(true)
            }
            shouldShowRequestPermissionRationale(permission) -> {
                val snackbar = Snackbar.make(binding.root, rationale, Snackbar.LENGTH_SHORT)
                snackbar.addCallback(object : Snackbar.Callback() {
                    override fun onDismissed(snackbar: Snackbar, event: Int) {
                        if (event == DISMISS_EVENT_TIMEOUT) {
                            getSimplePermission.launch(permission)
                        }
                    }
                })
                snackbar.show()
            }
            else -> {
                getSimplePermission.launch(permission)
            }
        }
    }

    fun updateUI(permission: Boolean) {
        if (permission) {
            logger.info("Permission granted")
            dispatchTakePictureIntent()
        } else {
            logger.warning("Permission denied")
        }
    }

    fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        var imageFile: File? = null
        try {
            imageFile = createImageFile()
        } catch (ex: IOException) {
            logger.warning(ex.message)
        }

        if (imageFile != null) {
            pictureImagePath = FileProvider.getUriForFile(this,"com.example.android.fileprovider2", imageFile)
            logger.info("Ruta: $pictureImagePath")
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, pictureImagePath)
            try {
                cameraActivityResultLauncher.launch(takePictureIntent)
            } catch (e: ActivityNotFoundException) {
                logger.warning("Camera app not found")
            }
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "JPEG_${timeStamp}.jpg"
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File(storageDir, imageFileName)
    }

    private fun agregarProducto(userId: String, producto: Producto) {
        val databaseRef = FirebaseDatabase.getInstance().getReference("users/$userId/producto")

        val datos = hashMapOf(
            "name" to producto.name,
            "description" to producto.description,
            "price" to producto.price
        )

        databaseRef.setValue(datos)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Producto añadido", Toast.LENGTH_SHORT).show()
                    binding.name.text.clear()
                    binding.description.text.clear()
                    binding.price.text.clear()
                } else {
                    Toast.makeText(this, "No se pudo añadir el producto", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
            }
    }

    private fun uploadImageToFirebase(imageUri: Uri) {

        var storageRef = mStorage.reference
        val nombreProducto = binding.name.text.toString()
        val pictureRef = storageRef.child("productos/$nombreProducto.jpg")

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

    data class Producto(
        val name: String,
        val price: Number,
        val description: String
    )
}