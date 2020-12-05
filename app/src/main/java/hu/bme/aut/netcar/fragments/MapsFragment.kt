package hu.bme.aut.netcar.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.location.Location
import android.os.AsyncTask
import android.os.Bundle
import android.transition.TransitionInflater
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
import com.google.android.gms.maps.model.*
import com.google.gson.Gson
import hu.bme.aut.netcar.R
import hu.bme.aut.netcar.data.DataManager
import hu.bme.aut.netcar.data.Driver
import hu.bme.aut.netcar.directionshelper.GoogleMapDTO
import kotlinx.android.synthetic.main.dialog_marker.*
import kotlinx.android.synthetic.main.dialog_marker.view.*
import kotlinx.android.synthetic.main.fragment_maps.*
import okhttp3.OkHttpClient
import okhttp3.Request


@Suppress("DEPRECATION")
class MapsFragment : Fragment(), GoogleMap.OnMarkerClickListener {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var lastLocation: Location
    private var userDataId: Int = -1
    lateinit var gMap: GoogleMap
    lateinit var destinationMarker: LatLng
    val driversArray = DataManager.drivers
    var canPlaceMarker = false
    lateinit var currentLatLng: LatLng

    private val callback = OnMapReadyCallback { googleMap ->
        setUpMap(googleMap)
        placeMarkerOnMap(driversArray, googleMap)

        gMap.setOnMapClickListener { point ->
            if(canPlaceMarker) {
                gMap.clear()
                placeMarkerOnMap(driversArray, googleMap)
                destinationMarker = point
                gMap.addMarker(MarkerOptions().position(point))
            }
        }
        btnFinalize.setOnClickListener {
            try {
                val URL = getDirectionURL(currentLatLng, destinationMarker)
                GetDirection(URL).execute()
                canPlaceMarker = false
                btnFinalize.visibility = View.GONE
                tvSelectLocation.visibility = View.GONE
            }
            catch (e: Exception){
                Toast.makeText(context, getString(R.string.give_destination), Toast.LENGTH_LONG).show()
            }
        }
    }


    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inflater = TransitionInflater.from(requireContext())
        enterTransition = inflater.inflateTransition(R.transition.fade)
        exitTransition = inflater.inflateTransition(R.transition.fade)

        if (arguments != null) {
            val id = arguments?.getInt("userDataId")
            userDataId = id!!
        }
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
    private fun setUpMap(googleMap: GoogleMap) {
        gMap = googleMap
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

        val location1 = LatLng(13.0356745, 77.5881522)
        googleMap.addMarker(MarkerOptions().position(location1).title("My Location"))
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location1, 5f))

        // Zoom into last location
        fusedLocationClient.lastLocation.addOnSuccessListener(this.requireActivity()) { location ->
            if (location != null) {
                lastLocation = location
                currentLatLng = LatLng(location.latitude, location.longitude)
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 16.0f))
            }
        }
    }

    fun getDirectionURL(origin: LatLng, dest: LatLng) : String{
        return "https://maps.googleapis.com/maps/api/directions/json?origin=${origin.latitude},${origin.longitude}&destination=${dest.latitude},${dest.longitude}&sensor=false&mode=driving&key=" + getString(
            R.string.google_maps_key
        )
    }

    private inner class GetDirection(val url: String) : AsyncTask<Void, Void, List<List<LatLng>>>(){
        override fun doInBackground(vararg params: Void?): List<List<LatLng>> {
            val client = OkHttpClient()
            val request = Request.Builder().url(url).build()
            val response = client.newCall(request).execute()
            val data = response.body()!!.string()
            Log.d("GoogleMap", " data : $data")
            val result =  ArrayList<List<LatLng>>()
            try{
                val respObj = Gson().fromJson(data, GoogleMapDTO::class.java)

                val path =  ArrayList<LatLng>()

                for (i in 0..(respObj.routes[0].legs[0].steps.size-1)){
//                    val startLatLng = LatLng(respObj.routes[0].legs[0].steps[i].start_location.lat.toDouble()
//                            ,respObj.routes[0].legs[0].steps[i].start_location.lng.toDouble())
//                    path.add(startLatLng)
//                    val endLatLng = LatLng(respObj.routes[0].legs[0].steps[i].end_location.lat.toDouble()
//                            ,respObj.routes[0].legs[0].steps[i].end_location.lng.toDouble())
                    path.addAll(decodePolyline(respObj.routes[0].legs[0].steps[i].polyline.points))
                }
                result.add(path)
            }catch (e: Exception){
                e.printStackTrace()
            }
            return result
        }

        override fun onPostExecute(result: List<List<LatLng>>) {
            val lineoption = PolylineOptions()
            for (i in result.indices){
                lineoption.addAll(result[i])
                lineoption.width(10f)
                lineoption.color(Color.BLUE)
                lineoption.geodesic(true)
            }
            gMap.addPolyline(lineoption)
        }
    }

    fun decodePolyline(encoded: String): List<LatLng> {

        val poly = ArrayList<LatLng>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0

        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat

            shift = 0
            result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng

            val latLng = LatLng((lat.toDouble() / 1E5), (lng.toDouble() / 1E5))
            poly.add(latLng)
        }

        return poly
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
            btnFinalize.visibility = View.VISIBLE
            tvSelectLocation.visibility = View.VISIBLE
            canPlaceMarker = true
            mAlertDialog.dismiss()
        }
        mDialogView.btnCancel.setOnClickListener{
            mAlertDialog.dismiss()
        }
        return true
    }
}