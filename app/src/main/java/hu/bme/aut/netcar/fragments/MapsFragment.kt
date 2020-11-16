package hu.bme.aut.netcar.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.location.Location
import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import hu.bme.aut.netcar.R
import hu.bme.aut.netcar.data.DataManager
import hu.bme.aut.netcar.data.Driver
import kotlinx.android.synthetic.main.dialog_marker.*
import kotlinx.android.synthetic.main.dialog_marker.view.*
import kotlinx.android.synthetic.main.dialog_register_driver.view.btnCancel


@Suppress("DEPRECATION")
class MapsFragment : Fragment(), GoogleMap.OnMarkerClickListener {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var lastLocation: Location

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inflater = TransitionInflater.from(requireContext())
        enterTransition = inflater.inflateTransition(R.transition.fade)
        exitTransition = inflater.inflateTransition(R.transition.fade)
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

    private val callback = OnMapReadyCallback { googleMap ->
        setUpMap(googleMap)

        val driversArray = DataManager.drivers
        placeMarkerOnMap(driversArray, googleMap)
    }

    // Asking for location permission
    private fun setUpMap(googleMap: GoogleMap) {
        if (ContextCompat.checkSelfPermission(
                this.requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }

        // Other map control buttons
        googleMap.setOnMarkerClickListener(this)
        googleMap.uiSettings.isZoomControlsEnabled = true
        googleMap.uiSettings.isCompassEnabled = true
        googleMap.uiSettings.isMapToolbarEnabled = true

        // If permission was granted, we can see our device's current location
        googleMap.isMyLocationEnabled = true

        // Zoom into last location
        fusedLocationClient.lastLocation.addOnSuccessListener(this.requireActivity()) { location ->
            if (location != null) {
                lastLocation = location
                val currentLatLng = LatLng(location.latitude, location.longitude)
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 16.0f))
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
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

    // To refresh the actual Fragment
    private fun refreshFragment() {
        activity?.supportFragmentManager?.beginTransaction()?.replace(this.id, MapsFragment())?.commit()
    }

    // Function to place markers of a list on the map
    @SuppressLint("UseCompatLoadingForDrawables")
    private fun placeMarkerOnMap(drivers: ArrayList<Driver>, map: GoogleMap) {
        for (driver in drivers) {
            val markerOptions = MarkerOptions().position(driver.location)
                .title(driver.name + "," + driver.carbrand + "," + driver.carmodel + "," + driver.serial + "," + driver.seats.toString())
            val d = resources.getDrawable(R.drawable.ic_map_car)
            markerOptions.icon(
                BitmapDescriptorFactory.fromBitmap(
                    drawableToBitmap(d)
                )
            )
            map.addMarker(markerOptions)
        }
    }

    // Function to make bitmap objects of XML
    private fun drawableToBitmap(drawable: Drawable): Bitmap? {
        if (drawable is BitmapDrawable) {
            return drawable.bitmap
        }
        val bitmap =
            Bitmap.createBitmap(
                drawable.intrinsicWidth,
                drawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

    override fun onMarkerClick(marker: Marker?): Boolean {
        val mDialogView = LayoutInflater.from(context).inflate(R.layout.dialog_marker, null)
        val mBuilder = AlertDialog.Builder(requireContext())
            .setView(mDialogView)
            .setTitle(getString(R.string.trip_confirmation))
        val mAlertDialog = mBuilder.show()
        val str = marker?.title?.split(',')
        mAlertDialog.driver_name.text = str!![0]
        mAlertDialog.car_brand.text = str[1]
        mAlertDialog.car_model.text = str[2]
        mAlertDialog.car_plate.text = str[3]

        mDialogView.checkBox.setOnCheckedChangeListener { _, _ ->
            if (mDialogView.checkbox_editText.isGone) {
                mDialogView.checkbox_editText.visibility = View.VISIBLE
            }

            else {
                mDialogView.checkbox_editText.visibility = View.GONE
            }
        }

        mDialogView.btnAccept.setOnClickListener{
            mAlertDialog.dismiss()
        }
        mDialogView.btnCancel.setOnClickListener{
            mAlertDialog.dismiss()
        }
        return true
    }
}