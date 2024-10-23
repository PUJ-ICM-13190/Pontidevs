package com.example.emprendenow

import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorManager
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
import kotlin.math.sqrt
import android.hardware.SensorEventListener

class ListaEmpresasActivity : AppCompatActivity(), SensorEventListener {
    private lateinit var binding: ActivityListaEmpresasBinding
    private lateinit var databaseRef: DatabaseReference
    private val listaEmpresas = mutableListOf<Empresa>()
    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private var lastUpdate: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListaEmpresasBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializamos el sensor
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        // Registrar el listener del acelerómetro
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)

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

    override fun onSensorChanged(event: SensorEvent) {
        val currentTime = System.currentTimeMillis()

        // Verificamos el cambio cada 100 ms para evitar mucha sensibilidad
        if ((currentTime - lastUpdate) > 100) {
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]

            // Sacudir el dispositivo para navegar a la cuenta del usuario
            val shakeThreshold = 12.0f
            val movement = sqrt((x * x + y * y + z * z).toDouble()).toFloat()

            if (movement > shakeThreshold) {
                onShakeDetected()
            }

            // Inclinación hacia abajo (y < -7) para cerrar sesión
            if (y < -7) {
                onTiltDownDetected()
            }

            lastUpdate = currentTime
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // No es necesario manejar esto para el acelerómetro
    }

    override fun onResume() {
        super.onResume()
        accelerometer?.also { accel ->
            sensorManager.registerListener(this, accel, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    // Función para manejar la detección de sacudida (ir a la cuenta del usuario)
    private fun onShakeDetected() {
        Toast.makeText(this, "Sacudido: Navegando a la cuenta", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, CuentaClienActivity::class.java)
        startActivity(intent)
    }

    // Función para manejar la inclinación hacia abajo (cerrar sesión)
    private fun onTiltDownDetected() {
        Toast.makeText(this, "Inclinación hacia abajo: Cerrando sesión", Toast.LENGTH_SHORT).show()
        // Aquí puedes cerrar sesión en Firebase y regresar a la pantalla de inicio de sesión
        cerrarSesion()
    }

    // Función para cerrar sesión
    private fun cerrarSesion() {
        // Cerrar la sesión en Firebase (si usas Firebase)
        // FirebaseAuth.getInstance().signOut()

        // Regresar a la pantalla de inicio de sesión
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    data class Empresa (
        val name: String,
        val logo: String,
        val descripcion: String
    )
}