package ir.snapp.snappboxtest.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
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
            if (moveToCurrentLocationFlag) currentLocation?.also { moveToLocation(it) }
        }
    }
    private lateinit var locationManager: LocationManager

    /** determines moving map camera to current location or not */
    private var moveToCurrentLocationFlag = false

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {
        // close tha app if location permission is not granted
        if (it.values.any { p -> p == false }) finish()
        else requestLocation()
    }

    private val resultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (!Settings.canDrawOverlays(this)) finish()
    }

    // END of region of properties

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initialFirebaseData()
        if (!isLocationGranted()) requestLocationAccess()
        if (!Settings.canDrawOverlays(this)) requestOverlayPermission()

        (supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment).getMapAsync(this)
        initialLocationData()
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

    /**
     * Checks for offer and displays it if there are any.
     */
    private fun checkForOffer() {
        intent.getParcelableExtra<Offer>(OFFER)?.also {
            moveToCurrentLocationFlag = false

            with(binding) {
                // bound map to the top of the offer bottom sheet
                ConstraintSet().apply {
                    clone(constraintLayoutParent)
                    connect(
                        map.id,
                        ConstraintSet.BOTTOM,
                        viewTransparentBorder.id,
                        ConstraintSet.BOTTOM
                    )
                    applyTo(constraintLayoutParent)
                }

                // display offer bottom sheet
                groupOffer.visibility = View.VISIBLE
                tvPrice.text = it.price.toString()
                tvOriginPin.apply {
                    text = getString(R.string.origin_address_placeholder, it.origin.address)
                    setOnClickListener { _ -> moveToLocation(it.origin.latLng) }
                }
                tvDestPin.apply {
                    text = getString(R.string.dest_address_placeholder, it.destination.address)
                    setOnClickListener { _ -> moveToLocation(it.destination.latLng) }
                }

                // to bound the map to the all pins
                val latLngBounds = LatLngBounds.Builder().apply {
                    currentLocation?.also { include(it) }
                }
                // add pins to the map
                arrayOf(it.origin.latLng, it.destination.latLng).forEachIndexed { i, loc ->
                    this@MainActivity.map.addMarker(
                        MarkerOptions().apply {
                            ContextCompat.getDrawable(
                                this@MainActivity,
                                R.drawable.ic_origin_pin,
                            )?.also { icon ->

                                position(loc)
                                latLngBounds.include(loc)

                                // change color for destination pin
                                if (i == 1) icon.setTint(getColor(R.color.dest_blue))
                                icon(BitmapDescriptorFactory.fromBitmap(icon.toBitmap()))
                            }
                        })
                }

                this@MainActivity.map.animateCamera(
                    CameraUpdateFactory.newLatLngBounds(latLngBounds.build(), 200)
                )
            }
        } ?: apply { moveToCurrentLocationFlag = true }
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

    private fun requestOverlayPermission() {
        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:$packageName")
        )
        resultLauncher.launch(intent)
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

        map.setOnMapLoadedCallback { checkForOffer() }
    }

    /** Moves camera to the specific location */
    private fun moveToLocation(location: LatLng) {
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 20f))
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}