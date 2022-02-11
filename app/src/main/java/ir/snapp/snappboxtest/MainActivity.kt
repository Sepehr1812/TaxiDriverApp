package ir.snapp.snappboxtest

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import ir.snapp.snappboxtest.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    // region of properties
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    // END of region of properties

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        (supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment).getMapAsync(this)
    }

    override fun onMapReady(p0: GoogleMap) {

    }
}