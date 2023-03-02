package com.github.polypoly.app

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.github.polypoly.app.databinding.ActivityMapsBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        val epflLocation = LatLng(46.520536, 6.568318)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(epflLocation, 15f))

        val satelliteLocation = LatLng(46.520544, 6.567825)
        val satelliteMarkerOptions = MarkerOptions()
            .position(satelliteLocation)
            .title("Satellite")
        mMap.addMarker(satelliteMarkerOptions)

        mMap.setOnInfoWindowClickListener { marker ->
            val coordinates = marker.position
            Toast.makeText(
                applicationContext,
                "Marker clicked at: $coordinates",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}