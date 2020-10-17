package hu.bme.aut.netcar.fragments.map

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap

import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import hu.bme.aut.netcar.R

class MapsFragment : Fragment() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var lastLocation: Location

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    private val callback = OnMapReadyCallback { googleMap ->
        setUpMap(googleMap)

        val bp = LatLng(47.481384, 19.055265)

        googleMap.addMarker(MarkerOptions().position(bp).title("Marker in Budapest"))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this.requireActivity())
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    // Asking for location permission
    private fun setUpMap(googleMap : GoogleMap) {
        if (ContextCompat.checkSelfPermission(this.requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
            return
        }

        googleMap.uiSettings.isZoomControlsEnabled = true

        // If permission was granted, we can see our device's current location
        googleMap.isMyLocationEnabled = true

        fusedLocationClient.lastLocation.addOnSuccessListener(this.requireActivity()) { location ->
            if (location != null) {
                lastLocation = location
                val currentLatLng = LatLng(location.longitude, location.longitude)
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12f))
            }
        }
    }

    // To refresh the actual Fragment
    private fun refreshFragment() {
        activity?.supportFragmentManager?.beginTransaction()?.replace(this.id, MapsFragment())?.commit()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty()) {
                    var success = true
                    for (i in grantResults.indices) {
                        if (grantResults[i] == PackageManager.PERMISSION_DENIED)
                            success = false
                    }
                    if (success)
                        // If the permission was granted, the fragment must be refreshed to see the current location
                        refreshFragment()
                }
            }
            else -> {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            }
        }
    }
}