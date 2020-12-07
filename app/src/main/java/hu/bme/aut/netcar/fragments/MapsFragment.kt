package hu.bme.aut.netcar.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.InputFilter
import android.text.InputType
import android.transition.TransitionInflater
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.gson.Gson
import hu.bme.aut.netcar.R
import hu.bme.aut.netcar.data.*
import hu.bme.aut.netcar.directionshelper.GoogleMapDTO
import hu.bme.aut.netcar.network.DefaultResponse
import hu.bme.aut.netcar.network.Repository
import kotlinx.android.synthetic.main.dialog_marker.*
import kotlinx.android.synthetic.main.dialog_marker.view.*
import kotlinx.android.synthetic.main.fragment_maps.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.lang.NullPointerException
import kotlin.math.pow
import kotlin.math.roundToInt

@Suppress("DEPRECATION")
class MapsFragment : Fragment(), GoogleMap.OnMarkerClickListener {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var lastLocation: Location
    private lateinit var userCoords: CoordData
    private lateinit var locationManager: LocationManager
    private lateinit var runnable: Runnable

    lateinit var gMap: GoogleMap
    lateinit var destinationMarker: LatLng
    lateinit var currentLatLng: LatLng

    private var userDataId: Int? = -1
    private var userToken: String? = null
    private var userData: UserData? = null
    private var arrayOfUsers: ArrayList<UserData> = arrayListOf()
    private var arrayOfCars: ArrayList<CarData> = arrayListOf()
    private var driverId: Int? = 1
    private var url: String = ""

    var canPlaceMarker = false

    private val activeVisibleDriversArray = arrayListOf<Driver>()
    private val handler: Handler = Handler(Looper.getMainLooper())
    private val callback = OnMapReadyCallback { googleMap ->
        setUpMap(googleMap)
        placeMarkerOnMap(activeVisibleDriversArray, googleMap)

        gMap.setOnMapClickListener { point ->
            if(canPlaceMarker) {
                gMap.clear()
                placeMarkerOnMap(activeVisibleDriversArray, googleMap)
                destinationMarker = point
                gMap.addMarker(MarkerOptions().position(point))
            }
        }

        btnFinalize.setOnClickListener {
            val builder : AlertDialog.Builder = AlertDialog.Builder(requireContext())
                .setMessage("How much credit do you want to pay?")
            val input = EditText(context)
            val lp = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
            lp.setMargins(4, 2, 4, 2)
            input.layoutParams = lp
            input.inputType = InputType.TYPE_CLASS_NUMBER
            input.filters = arrayOf(InputFilter.LengthFilter(5))
            builder.setView(input)
                .setNeutralButton("OK") { _, _ ->
                    val payment: Int?
                    if(input.text.isNotEmpty()) {
                        payment = input.text.toString().toInt()
                        if (payment <= 0) {
                            Toast.makeText(requireContext(), "You should give something for your travel", Toast.LENGTH_LONG).show()
                        }
                        else if (payment > userData?.credits!!) {
                            Toast.makeText(requireContext(), "You don't have enough credits for your travel", Toast.LENGTH_LONG).show()
                        }
                        else {
                            try {
                                url = getDirectionURL(currentLatLng, destinationMarker)
                                GetDirection(url).execute()
                            }
                            catch (e: Exception) {
                                Toast.makeText(context, getString(R.string.give_destination), Toast.LENGTH_LONG).show()
                            }

                            var defaultResponse: DefaultResponse?
                            lifecycleScope.launch {
                                withContext(Dispatchers.IO) {
                                    defaultResponse = Repository.addRequest(
                                        ServiceRequest(
                                            driverID = driverId,
                                            passengerID = userDataId,
                                            destinationPos = Coord(
                                                x = destinationMarker.latitude,
                                                y = destinationMarker.longitude
                                            ),
                                            payment = payment
                                        ), userToken = userToken!!
                                    )

                                    withContext(Dispatchers.Main) {
                                        canPlaceMarker = false
                                        btnFinalize.visibility = View.GONE
                                        tvSelectLocation.visibility = View.GONE
                                        Toast.makeText(requireContext(), defaultResponse?.message, Toast.LENGTH_LONG)
                                            .show()
                                    }
                                }
                            }
                        }
                    }
                    else {
                        Toast.makeText(requireContext(), "You should give something for your travel", Toast.LENGTH_LONG).show()
                    }
                }

            val dialog = builder.create()

            dialog.show()
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

        userDataId = arguments?.getInt("userDataId")
        userToken = arguments?.getString("token")
        val serializableData = arguments?.getSerializable("userData")
        if (serializableData != null)
            userData = serializableData as UserData
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
                this.requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }

        gettingCoords()

        // Other map control buttons
        googleMap.setOnMarkerClickListener(this)
        googleMap.uiSettings.isZoomControlsEnabled = true
        googleMap.uiSettings.isCompassEnabled = true
        googleMap.uiSettings.isMapToolbarEnabled = true

        // If permission was granted, we can see our device's current location
        googleMap.isMyLocationEnabled = true

        // Zoom into last / current location
        fusedLocationClient.lastLocation.addOnSuccessListener(this.requireActivity()) { location ->
            if (location != null) {
                lastLocation = location
                currentLatLng = LatLng(location.latitude, location.longitude)
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 16.0f))
            }
        }
    }

    private fun getDirectionURL(origin: LatLng, dest: LatLng) : String{
        return "https://maps.googleapis.com/maps/api/directions/json?origin=${origin.latitude},${origin.longitude}&destination=${dest.latitude},${dest.longitude}&sensor=false&mode=driving&key=" + getString(
            R.string.google_maps_key
        )
    }

    @SuppressLint("StaticFieldLeak")
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

                for (i in 0 until respObj.routes[0].legs[0].steps.size){
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

    // Function to place markers of a list on the map
    @SuppressLint("UseCompatLoadingForDrawables")
    private fun placeMarkerOnMap(drivers: ArrayList<Driver>, map: GoogleMap) {
        for (driver in drivers) {
            val markerOptions = MarkerOptions().position(driver.location)
                .title(driver.name + "," + driver.picture + "," + driver.serial + "," + driver.carbrand + "," + driver.carmodel + "," + driver.rating + "," + driver.driverid)
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
        if (!canPlaceMarker) {
            val str: List<String>?
            try {
                str = marker?.title?.split(',')
                str!![0]
            } catch (e: NullPointerException) {
                return false
            }
            val mDialogView = LayoutInflater.from(context).inflate(R.layout.dialog_marker, null)
            val mBuilder = AlertDialog.Builder(requireContext())
                .setView(mDialogView)
                .setTitle(getString(R.string.trip_confirmation))
            val mAlertDialog = mBuilder.show()

            mAlertDialog.driver_name.text = str[0]
            val imageBytes = Base64.decode(str[1], 0)
            mAlertDialog.car_image.setImageBitmap(
                BitmapFactory.decodeByteArray(
                    imageBytes, 0, imageBytes.size
                )
            )
            mAlertDialog.car_plate.text = str[2]
            mAlertDialog.car_brand.text = str[3]
            mAlertDialog.car_model.text = str[4]
            val rating: Float = str[5].toFloat()
            mAlertDialog.user_rating.rating = rating

            mDialogView.checkBox.setOnCheckedChangeListener { _, _ ->
                if (mDialogView.checkbox_editText.isGone) {
                    mDialogView.checkbox_editText.visibility = View.VISIBLE
                } else {
                    mDialogView.checkbox_editText.visibility = View.GONE
                }
            }

            mDialogView.btnAccept.setOnClickListener {
                driverId = str[6].toInt()
                btnFinalize.visibility = View.VISIBLE
                tvSelectLocation.visibility = View.VISIBLE
                canPlaceMarker = true
                mAlertDialog.dismiss()
            }
            mDialogView.btnCancel.setOnClickListener {
                mAlertDialog.dismiss()
            }
            return true
        }
        return true
    }

    private fun updateUserData() {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                if (userDataId != null && userToken != null)
                    userData = Repository.getUser(userDataId!!, userToken!!)
            }
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun gettingActiveVisibleDrivers() {
        activeVisibleDriversArray.clear()
        arrayOfCars.clear()
        arrayOfUsers.clear()
        if (!userData!!.isInProgress) {
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    val allUsers = Repository.getAllUsers(userToken!!)
                    val allCars = Repository.getAllCars(userToken!!)
                    if(!allUsers.isNullOrEmpty()) {
                        arrayOfUsers = ArrayList(allUsers)
                    }
                    if(!allCars.isNullOrEmpty()) {
                        arrayOfCars = ArrayList(allCars)
                    }
                    withContext(Dispatchers.Main) {
                        if(arrayOfUsers.isNotEmpty()) {
                            for (user in arrayOfUsers) {
                                if (user.valid && user.visible && !user.isInProgress && user.userId != userDataId) {
                                    var userCarData: CarData? = null
                                    var sum = 0.0

                                    for (rating in user.ratings) {
                                        sum += rating
                                    }

                                    val rating = (sum / user.ratings.size.toDouble()).roundTo(2)

                                    for (car in arrayOfCars) {
                                        if (car.carId == user.userId) {
                                            userCarData = car
                                            break
                                        }
                                    }

                                    if (user.location.x != null && user.location.y != null && userCarData?.serial != null) {
                                        activeVisibleDriversArray.add(
                                            Driver(
                                                user.username!!,
                                                LatLng(user.location.x!!, user.location.y!!),
                                                userCarData.pic,
                                                userCarData.serial,
                                                userCarData.brand!!,
                                                userCarData.model!!,
                                                userCarData.freePlace!!,
                                                rating,
                                                user.userId!!
                                            )
                                        )
                                    }
                                }
                            }
                            gMap.clear()
                            placeMarkerOnMap(activeVisibleDriversArray, gMap)
                            try {
                                if(url != "") {
                                    GetDirection(url).execute()
                                }
                                gMap.addMarker(MarkerOptions().position(destinationMarker))
                            } catch (e: Exception) {
                                //
                            }
                        }
                    }
                }
            }
        }
        else if (userData!!.isInProgress) {
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    val activeRequest = Repository.getActiveRequest(userDataId!!, userToken!!)
                    var driverCarData: CarData? = null
                    var passengerUserData: UserData? = null
                    if (activeRequest?.passengerID == userDataId) {
                        driverCarData = Repository.getCar(activeRequest?.driverID!!, userToken!!)
                    } else {
                        passengerUserData = Repository.getUser(activeRequest?.driverID!!, userToken!!)
                    }

                    withContext(Dispatchers.Main) {
                        if (driverCarData != null) {
                            val loc = LatLng(
                                driverCarData.userData?.location?.x!!,
                                driverCarData.userData?.location?.y!!
                            )
                            val markerOptions = MarkerOptions().position(loc)
                            val d = resources.getDrawable(R.drawable.ic_map_car)
                            markerOptions.icon(
                                BitmapDescriptorFactory.fromBitmap(
                                    drawableToBitmap(d)
                                )
                            )
                            gMap.addMarker(markerOptions)
                        } else {
                            val loc = LatLng(
                                passengerUserData?.location?.x!!,
                                passengerUserData.location.y!!
                            )
                            val markerOptions = MarkerOptions().position(loc)
                            /*
                            val imageBytes = Base64.decode(passengerUserData.picture, 0)
                            val bitmap = BitmapFactory.decodeByteArray(
                                imageBytes, 0, imageBytes.size
                            )
                            markerOptions.icon(
                                BitmapDescriptorFactory.fromBitmap(
                                    Bitmap.createScaledBitmap(
                                        bitmap, 30, 30, false
                                    )
                                )
                            )*/

                            val d = resources.getDrawable(R.drawable.ic_map_car)
                            markerOptions.icon(
                                BitmapDescriptorFactory.fromBitmap(
                                    drawableToBitmap(d)
                                )
                            )
                            gMap.addMarker(markerOptions)
                        }
                    }
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun gettingCoords() {
        locationManager = requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val hasGps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val hasNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        if (hasGps || hasNetwork) {
            if (hasGps) {
                locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, 10000, 0F, object: LocationListener {
                        override fun onLocationChanged(location: Location) {
                            userCoords = CoordData(x = location.latitude, y = location.longitude)
                            lastLocation = location
                            userData?.location = userCoords
                            if(!userToken.isNullOrBlank())
                                sendingCoords()
                        }
                    }
                )
            }

            if (hasNetwork) {
                locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, 10000, 0F, object: LocationListener {
                        override fun onLocationChanged(location: Location) {
                            userCoords = CoordData(x = location.latitude, y = location.longitude)
                            lastLocation = location
                            userData?.location = userCoords
                            if(!userToken.isNullOrBlank())
                                sendingCoords()
                        }
                    }
                )
            }
        }
    }

    private fun sendingCoords() {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                Repository.updateUser(userDataId!!, userData!!, userToken!!)
            }
        }
    }

    private fun updateDetailsCyclic() {
        runnable = Runnable {
            if(!userToken.isNullOrBlank()){
                updateUserData()
                gettingActiveVisibleDrivers()
            }
            handler.postDelayed(runnable, 10000)
        }
        handler.post(runnable)
    }

    override fun onResume() {
        super.onResume()
        updateDetailsCyclic()
    }

    override fun onPause() {
        handler.removeCallbacks(runnable)
        super.onPause()
    }

    private fun Double.roundTo(numFractionDigits: Int): Double {
        val factor = 10.0.pow(numFractionDigits.toDouble())
        return (this * factor).roundToInt() / factor
    }
}