package com.arc.earthquakemap


import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapFragment : Fragment(R.layout.fragment_map), OnMapReadyCallback {

    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    private var magnitude: Double = 0.0
    private var title = "empty"
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var googleMap: GoogleMap


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Get arguments passed from MainActivity
        arguments?.let {
            latitude = it.getDouble("LATITUDE")
            longitude = it.getDouble("LONGITUDE")
            magnitude = it.getDouble("MAGNITUDE")
            title = it.getString("TITLE", "No title")
        }
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())
    }

    // Move map initialization to onViewCreated to ensure fragment's view is fully created
    override fun onViewCreated(view: android.view.View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize the map fragment
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        if (mapFragment != null) {
            mapFragment.getMapAsync(this)
        } else {
            Log.e("MapFragment", "SupportMapFragment is null!")
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
        val earthquakeLocation = LatLng(latitude, longitude)
        googleMap.addMarker(MarkerOptions().position(earthquakeLocation).title(title))

        // Set the map's camera position
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(earthquakeLocation, 6f))

        // Add a circle around the earthquake location
        googleMap.addCircle(
            CircleOptions()
                .center(earthquakeLocation)
                .radius(calculateRadius(magnitude)) // Radius based on magnitude
                .strokeColor(0x220000FF) // Blue outline
                .fillColor(0x220000FF) // Blue fill
        )
        enableUserLocation()
    }

    private fun enableUserLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request location permissions if not granted
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
            return
        }

        // Enable My Location Layer on the map
        googleMap.isMyLocationEnabled = true

        // Get user's current location and move the camera
        fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val userLatLng = LatLng(location.latitude, location.longitude) //Hold for future location/earthquake interaction.
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Permission was granted, enable the location
            enableUserLocation()
        } else {
            Log.e("MapFragment", "Location permission was denied.")
        }
    }

    // Radius function based on Bulletin of the Seismological Society of America (1990) 80 (4): 757–783.
    private fun calculateRadius(magnitude: Double): Double {
        val A = 0.41 * magnitude
        val attenuationFactor = Math.pow(10.0, A) // Attenuation part of the equation
        val baseDistance = 1000.0 // (in meters)
        return baseDistance * attenuationFactor
    }


    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 5910
        fun newInstance(lat: Double, lon: Double, mag: Double, title: String) = MapFragment().apply {
            arguments = Bundle().apply {
                putDouble("LATITUDE", lat)
                putDouble("LONGITUDE", lon)
                putDouble("MAGNITUDE", mag)
                putString("TITLE", title)
            }
        }
    }
}
