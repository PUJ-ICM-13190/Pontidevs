package com.example.emprendenow

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.emprendenow.databinding.ActivityMapaClienteBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.logging.Logger

class MapaClienteActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapaClienteBinding

    companion object {
        private val TAG = MapaEmpresaActivity::class.java.name
        const val REQUEST_CHECK_SETTINGS = 201
    }

    private val logger = Logger.getLogger(TAG)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapaClienteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val empresa = intent.getStringExtra("emprendimiento")
        val navbar = binding.bottomNavigation
        val databaseRef = FirebaseDatabase.getInstance().getReference("users")

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

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

                R.id.chat -> {
                    val intent = Intent(this, ListaChatsActivity::class.java)
                    intent.putExtra("usuario", "user")
                    startActivity(intent)
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

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        val bogota = LatLng(4.60971, -74.08175)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(bogota, 12f))

        mMap.uiSettings.setAllGesturesEnabled(true)
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMyLocationButtonEnabled = true
    }
}