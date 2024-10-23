package com.example.emprendenow

import android.content.Intent
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.Toast

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.emprendenow.databinding.ActivityMapaEmpresaBinding
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Marker
import com.google.firebase.database.FirebaseDatabase
import java.io.IOException
import java.util.logging.Logger

class MapaEmpresaActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var mMap: GoogleMap
    private lateinit var mGeocoder: Geocoder
    private lateinit var binding: ActivityMapaEmpresaBinding

    lateinit var mAddress: EditText
    private var lastMarker: Marker? = null

    companion object {
        private val TAG = MapaEmpresaActivity::class.java.name
        const val REQUEST_CHECK_SETTINGS = 201
    }

    private val logger = Logger.getLogger(TAG)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapaEmpresaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navbar = binding.bottomNavigation
        val user = intent.getStringExtra("user")
        val confirmLocation = binding.confirmLocation

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        mAddress = binding.textDirection
        mAddress.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                val addressText = mAddress.text.toString()
                if (addressText.isNotEmpty()) {
                    findAddress()
                } else {
                    Toast.makeText(this@MapaEmpresaActivity, "Enter a direction", Toast.LENGTH_SHORT).show()
                }
                true
            } else {
                false
            }
        }

        navbar.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    val intent = Intent(this, MenuEmprendedorActivity::class.java)
                    startActivity(intent)
                    true
                }

                R.id.chat -> {
                    true
                }

                R.id.account -> {
                    val intent = Intent(this, CuentaEmprenActivity::class.java)
                    startActivity(intent)
                    true
                }

                else -> false
            }
        }

        confirmLocation.setOnClickListener {
            if (lastMarker != null) {
                val latitude = lastMarker?.position?.latitude
                val longitude = lastMarker?.position?.longitude

                if (latitude != null && longitude != null) {
                    val locationData = mapOf(
                        "latitude" to latitude,
                        "longitude" to longitude
                    )

                    val databaseRef = FirebaseDatabase.getInstance().getReference("users/$user/location")
                    databaseRef.setValue(locationData)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Location saved successfully", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Failed to save location", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    Toast.makeText(this, "No location to save", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "No marker placed", Toast.LENGTH_SHORT).show()
            }
        }

        mGeocoder = Geocoder(baseContext)
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

        setLongClickListener()
    }

    private fun findAddress() {
        val addressString = mAddress.text.toString()
        if (addressString.isNotEmpty()) {
            try {
                val addresses = mGeocoder.getFromLocationName(
                    addressString, 2
                )
                if (addresses != null && addresses.isNotEmpty()) {
                    val addressResult = addresses[0]
                    val position = LatLng(addressResult.latitude, addressResult.longitude)
                    lastMarker?.remove()
                    lastMarker = mMap.addMarker(
                        MarkerOptions().position(position)
                            .title(addressResult.featureName)
                            .snippet(addressResult.getAddressLine(0))
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                    )
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 12f))
                } else {
                    Toast.makeText(
                        this@MapaEmpresaActivity,
                        "Direction not found",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        } else {
            Toast.makeText(this@MapaEmpresaActivity, "The direction is empty", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun setLongClickListener() {
        mMap.setOnMapLongClickListener { latLng ->
            val addreses = mGeocoder.getFromLocation(latLng.latitude, latLng.longitude,1)
            if (addreses != null && addreses.isNotEmpty()) {
                val address = addreses[0]
                val name = address.getAddressLine(0)
                lastMarker?.remove()
                lastMarker = mMap.addMarker(
                    MarkerOptions().position(latLng)
                        .title(name)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                )
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12f))

                Toast.makeText(this, "Marcador agregado en: $latLng", Toast.LENGTH_SHORT).show()
            }
        }
    }
}