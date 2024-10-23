package com.example.emprendenow

import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.emprendenow.databinding.ActivityInfoEmprendimientoBinding

class InfoEmprendimientoActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var binding: ActivityInfoEmprendimientoBinding
    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private var lastShakeTime: Long = 0
    private val shakeThreshold = 12.0f // Umbral para detectar sacudida

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInfoEmprendimientoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializar el SensorManager y el acelerómetro
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        // Registrar el listener del sensor
        accelerometer?.also { sensor ->
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        }

        val empresa = intent.getStringExtra("EmprendimientoName")
        val navbar = binding.bottomNavigation
        val location = binding.locationButton
        binding.name.text = empresa

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
                    val intent = Intent(this, CuentaClienActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }

        location.setOnClickListener {
            val intent = Intent(this, MapaClienteActivity::class.java)
            intent.putExtra("emprendimiento", empresa)
            startActivity(intent)
        }
    }

    // Este método se llama cada vez que hay una actualización del sensor
    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            val x = it.values[0]
            val y = it.values[1]
            val z = it.values[2]

            // Aceleración combinada en los tres ejes
            val acceleration = Math.sqrt((x * x + y * y + z * z).toDouble()).toFloat()

            // Detectar si la aceleración supera el umbral definido para la sacudida
            if (acceleration > shakeThreshold) {
                val currentTime = System.currentTimeMillis()

                // Evitar múltiples eventos de sacudida muy cercanos
                if (currentTime - lastShakeTime > 1000) {
                    lastShakeTime = currentTime

                    // Mostrar el "Like" cuando se detecta la sacudida
                    Toast.makeText(this, "👍 ¡Like!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // No se necesita implementar en este caso
    }

    override fun onResume() {
        super.onResume()
        // Volver a registrar el listener cuando la actividad está en primer plano
        accelerometer?.also { sensor ->
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onPause() {
        super.onPause()
        // Detener el listener del sensor para ahorrar batería cuando la actividad está en segundo plano
        sensorManager.unregisterListener(this)
    }
}
