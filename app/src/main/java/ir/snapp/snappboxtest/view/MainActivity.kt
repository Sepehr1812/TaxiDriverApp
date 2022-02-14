package ir.snapp.snappboxtest.view

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.firebase.messaging.FirebaseMessaging
import ir.snapp.snappboxtest.R
import ir.snapp.snappboxtest.data.Offer
import ir.snapp.snappboxtest.databinding.ActivityMainBinding
import ir.snapp.snappboxtest.util.Constants.OFFER

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    // region of properties
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    // END of region of properties

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.e("FCM token", "Fetching FCM registration token failed", task.exception)
                return@addOnCompleteListener
            }

            val token = task.result
            Log.i("FCM token", token)
        }

        (supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment).getMapAsync(this)

        intent.getParcelableExtra<Offer>(OFFER).also { Log.d("FCM Data", "$it") }
    }

    override fun onMapReady(p0: GoogleMap) {

    }
}