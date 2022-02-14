package ir.snapp.snappboxtest.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.messaging.FirebaseMessaging
import ir.snapp.snappboxtest.R
import ir.snapp.snappboxtest.data.Offer
import ir.snapp.snappboxtest.databinding.ActivityMainBinding
import ir.snapp.snappboxtest.util.Constants.OFFER

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    // region of properties
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    private lateinit var map: GoogleMap
    private var currentLocation: LatLng? = null
    private lateinit var locationRequest: LocationRequest
    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            currentLocation = locationResult.lastLocation.run { LatLng(latitude, longitude) }
            currentLocation?.also { moveToLocation(it) }
        }
    }
    private lateinit var locationManager: LocationManager

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {
        // close tha app if location permission is not granted
        if (it.values.any { p -> p == false }) finish()
        else requestLocation()
    }

    // END of region of properties

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initialFirebaseData()
        if (!isLocationGranted()) requestLocationAccess()

        (supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment).getMapAsync(this)
        initialLocationData()

        intent.getParcelableExtra<Offer>(OFFER)
    }

    private fun initialFirebaseData() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.e("FCM token", "Fetching FCM registration token failed", task.exception)
                return@addOnCompleteListener
            }

            val token = task.result
            Log.i("FCM token", token)
        }
    }

    private fun initialLocationData() {
        locationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager

        locationRequest = LocationRequest.create().apply {
            interval = 5000L
            fastestInterval = 5000L
            priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestLocation() {
        if (isLocationGranted()) {
            // getting user location
            LocationServices.getFusedLocationProviderClient(this)
                .requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
        }
    }

    /**
     * Checks if location permission is granted
     */
    private fun isLocationGranted() =
        (ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED)

    private fun requestLocationAccess() {
        permissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    override fun onResume() {
        super.onResume()
        requestLocation()
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        if (isLocationGranted())
            map.apply {
                mapType = GoogleMap.MAP_TYPE_NORMAL

                isMyLocationEnabled = true

                uiSettings.apply {
                    isZoomControlsEnabled = true
                    isZoomGesturesEnabled = true
                    isTrafficEnabled = true
                    isCompassEnabled = true
                    isMyLocationButtonEnabled = true
                }
            }
    }

    /** Moves camera to the specific location */
    private fun moveToLocation(location: LatLng) {
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}