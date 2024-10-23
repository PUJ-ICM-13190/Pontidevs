package com.example.emprendenow

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
<<<<<<< HEAD
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
=======
import android.widget.Toast

>>>>>>> 595cec57ab0a71b694005f5333f2bdbceb9cca32
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.example.emprendenow.databinding.ActivityMapaClienteBinding
<<<<<<< HEAD
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream
=======
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.logging.Logger
>>>>>>> 595cec57ab0a71b694005f5333f2bdbceb9cca32

class MapaClienteActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapaClienteBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var currentLocation: LatLng? = null // Verificamos si ya tenemos la ubicación

    private val client = OkHttpClient()  // Cliente para hacer peticiones HTTP

    companion object {
        const val REQUEST_LOCATION_PERMISSION = 100
        const val TAG = "MapaClienteActivity"
        const val GOOGLE_API_KEY = "AIzaSyD4eAMvMWh_VT6CptSHX6AdA0xVKyKj41Y"
    }

    companion object {
        private val TAG = MapaEmpresaActivity::class.java.name
        const val REQUEST_CHECK_SETTINGS = 201
    }

    private val logger = Logger.getLogger(TAG)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapaClienteBinding.inflate(layoutInflater)
        setContentView(binding.root)

<<<<<<< HEAD
        // Inicializa el FusedLocationProviderClient para obtener la ubicación del usuario
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
=======
        val empresa = intent.getStringExtra("emprendimiento")
        val navbar = binding.bottomNavigation
        val databaseRef = FirebaseDatabase.getInstance().getReference("users")
>>>>>>> 595cec57ab0a71b694005f5333f2bdbceb9cca32

        // Verificar permisos de ubicación antes de inicializar el mapa
        checkLocationPermission()
    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            initMap()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_LOCATION_PERMISSION)
        }
    }

    private fun initMap() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

<<<<<<< HEAD
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        enableLocation()
=======
        databaseRef.orderByChild("emprendimiento/name").equalTo(empresa)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (empresaSnapshot in dataSnapshot.children) {
                        val latitud = empresaSnapshot.child("location/latitude").getValue(Double::class.java)
                        logger.info("Latitud: $latitud")
                        val longitud = empresaSnapshot.child("location/longitude").getValue(Double::class.java)
                        logger.info("Longitud: $longitud")

                        if (latitud != null && longitud != null) {
                            val empresaLocation = LatLng(latitud, longitud)
                            mMap.addMarker(MarkerOptions().position(empresaLocation).title("Ubicación de $empresa"))
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(empresaLocation, 13f))
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Toast.makeText(this@MapaClienteActivity, "Error al cargar la ubicación", Toast.LENGTH_SHORT).show()
                }
            })

        navbar.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    val intent = Intent(this, ListaEmpresasActivity::class.java)
                    startActivity(intent)
                    true
                }
>>>>>>> 595cec57ab0a71b694005f5333f2bdbceb9cca32

        // Leer las coordenadas de destino desde el archivo JSON
        val destinationLocation = readDestinationFromJson()

        if (destinationLocation != null) {
            createRouteToDestination(destinationLocation)
            // Aquí agregamos el marcador en el destino
            mMap.addMarker(MarkerOptions().position(destinationLocation).title("Destino"))  // Aseguramos agregar el marcador
        } else {
            Toast.makeText(this, "Error al cargar el destino", Toast.LENGTH_SHORT).show()
        }
    }

    private fun enableLocation() {
        // Solo llamamos a esta función si no hemos obtenido la ubicación previamente
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.isMyLocationEnabled = true
            if (currentLocation == null) {
                getCurrentLocation() // Obtener la ubicación actual si ya está habilitada y no ha sido obtenida aún
            }
        }
    }

    private fun getCurrentLocation() {
        try {
            // Verificar si ya tenemos la ubicación antes de obtenerla nuevamente
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    currentLocation = LatLng(location.latitude, location.longitude)
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation!!, 15f))

<<<<<<< HEAD
                    mMap.addMarker(MarkerOptions().position(currentLocation!!).title("Tu ubicación"))
                } else {
                    Toast.makeText(this, "No se pudo obtener la ubicación actual", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener {
                Toast.makeText(this, "Error al obtener la ubicación", Toast.LENGTH_SHORT).show()
            }
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
=======
        val bogota = LatLng(4.60971, -74.08175)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(bogota, 12f))

        mMap.uiSettings.setAllGesturesEnabled(true)
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMyLocationButtonEnabled = true
>>>>>>> 595cec57ab0a71b694005f5333f2bdbceb9cca32
    }

    // Función para leer las coordenadas del destino desde un archivo JSON en res/raw
    private fun readDestinationFromJson(): LatLng? {
        return try {
            val inputStream: InputStream = resources.openRawResource(R.raw.destination)
            val jsonString = inputStream.bufferedReader().use { it.readText() }

            val jsonObject = JSONObject(jsonString)
            val latitude = jsonObject.getDouble("latitude")
            val longitude = jsonObject.getDouble("longitude")

            LatLng(latitude, longitude)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // Función para crear la ruta desde la ubicación actual hasta el destino
    private fun createRouteToDestination(destination: LatLng) {
        if (currentLocation != null) {
            val origin = "${currentLocation!!.latitude},${currentLocation!!.longitude}"
            val dest = "${destination.latitude},${destination.longitude}"

            val url = "https://maps.googleapis.com/maps/api/directions/json?origin=$origin&destination=$dest&key=$GOOGLE_API_KEY"

            // Realizar la petición HTTP usando OkHttp
            val request = Request.Builder().url(url).build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    runOnUiThread {
                        Toast.makeText(this@MapaClienteActivity, "Error al obtener la ruta", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onResponse(call: Call, response: Response) {
                    response.use {
                        if (!response.isSuccessful) {
                            runOnUiThread {
                                Toast.makeText(this@MapaClienteActivity, "Error en la respuesta de la API", Toast.LENGTH_SHORT).show()
                            }
                            return
                        }

                        val responseBody = response.body?.string()
                        if (responseBody != null) {
                            Log.d(TAG, "Respuesta de la API Directions: $responseBody") // Log para ver la respuesta

                            val jsonResponse = JSONObject(responseBody)
                            val routes = jsonResponse.getJSONArray("routes")

                            if (routes.length() > 0) {
                                val polyline = routes.getJSONObject(0).getJSONObject("overview_polyline").getString("points")
                                val points = decodePolyline(polyline)

                                // Verificar cuántos puntos se obtienen
                                Log.d(TAG, "Número de puntos decodificados: ${points.size}")

                                // Dibujar la ruta en el mapa
                                runOnUiThread {
                                    if (points.isNotEmpty()) {
                                        drawRoute(points)
                                        Log.d(TAG, "Ruta dibujada con éxito.")
                                    } else {
                                        Log.e(TAG, "Error al decodificar la polyline. No se obtuvieron puntos.")
                                    }
                                }
                            } else {
                                // Mostrar un mensaje si no se encontró ruta
                                runOnUiThread {
                                    Toast.makeText(this@MapaClienteActivity, "No se encontró ruta", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } else {
                            Log.e(TAG, "Respuesta de la API vacía.")
                        }
                    }
                }
            })
        } else {
            Log.e(TAG, "currentLocation es null, no se puede crear la ruta")
        }
    }


    // Dibujar la ruta en el mapa
    private fun drawRoute(points: List<LatLng>) {
        if (points.isEmpty()) {
            Log.e(TAG, "La lista de puntos está vacía. No se puede dibujar la ruta.")
            return
        }

        // Asegurarse de que el color sea visible y que el ancho sea adecuado
        val polylineOptions = PolylineOptions()
            .addAll(points)
            .color(ContextCompat.getColor(this, android.R.color.holo_blue_dark))  // Cambié a un color visible
            .width(10f)
            .geodesic(true)

        // Dibujar la polyline en el mapa
        mMap.addPolyline(polylineOptions)

        // Ajustar la cámara para incluir toda la ruta
        val boundsBuilder = LatLngBounds.Builder()
        boundsBuilder.include(currentLocation!!) // Incluir ubicación actual
        for (point in points) {
            boundsBuilder.include(point)
        }

        // Animar la cámara para que ajuste la ruta completa
        val bounds = boundsBuilder.build()
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 150))  // Ajuste de padding
    }

    // Decodificar el string polyline a una lista de LatLng
    private fun decodePolyline(encoded: String): List<LatLng> {
        val poly = ArrayList<LatLng>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0

        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat

            shift = 0
            result = 0
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng

            poly.add(LatLng((lat / 1E5), (lng / 1E5)))
        }

        return poly
    }
    // Manejo del resultado de la solicitud de permisos
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initMap()
            } else {
                Toast.makeText(this, "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
