package com.example.emprendenow

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.emprendenow.databinding.ActivityAgregarProductoBinding
import com.google.android.material.snackbar.Snackbar
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.logging.Logger

class AgregarProductoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAgregarProductoBinding
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
            logger.info("Image capture successfully")
        } else {
            logger.warning("Capture failed")
        }
    }

    private val galleryActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val imageUri: Uri? = result.data!!.data
            logger.info("Image loaded successfully")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAgregarProductoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val productos: MutableList<Producto> = mutableListOf()

        val addBtn = binding.add
        val back = binding.back
        val camera = binding.buttonTake
        val galery = binding.buttonGalery

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
            productos.add(nuevoProducto)
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

    data class Producto(
        val name: String,
        val price: Double,
        val description: String
    )
}