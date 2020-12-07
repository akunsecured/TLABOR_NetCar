package hu.bme.aut.netcar

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Base64
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.view.GravityCompat
import androidx.core.view.isGone
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import hu.bme.aut.netcar.data.CarData
import hu.bme.aut.netcar.data.UserData
import hu.bme.aut.netcar.network.DefaultResponse
import hu.bme.aut.netcar.network.Repository
import hu.bme.aut.netcar.network.RetrofitClientAuth
import kotlinx.android.synthetic.main.activity_navigation.*
import kotlinx.android.synthetic.main.activity_navigation.view.*
import kotlinx.android.synthetic.main.dialog_register_driver.view.*
import kotlinx.android.synthetic.main.nav_header_main.view.*
import kotlinx.coroutines.*
import java.io.ByteArrayOutputStream


@Suppress("DEPRECATION")
class NavigationActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var retrofit: RetrofitClientAuth
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle
    private lateinit var navBtn: FloatingActionButton
    private lateinit var mDrawerLayout: DrawerLayout
    private lateinit var filepath: Uri
    private var bitmap: Bitmap? = null
    private lateinit var mDialogView: View
    private lateinit var navView: NavigationView
    private var isDriverVisible: Boolean = false
    private var userData : UserData? = null
    var userToken: String = ""
    var userDataId: Int = -1

    private val handler: Handler = Handler(Looper.getMainLooper())
    private lateinit var runnable: java.lang.Runnable


    @SuppressLint("InflateParams")
    @Suppress("LABEL_NAME_CLASH")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigation)

        val bundle: Bundle = intent.extras!!

        userToken = bundle.getString("token")!!
        userDataId = bundle.getInt("userDataId")
        userData = bundle.getSerializable("userData") as UserData?

        isDriverVisible = userData?.visible!!

        mDrawerLayout = drawer_layout
        actionBarDrawerToggle = ActionBarDrawerToggle(
            this,
            mDrawerLayout,
            R.string.open,
            R.string.close
        )
        mDrawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

        navBtn = findViewById(R.id.navBtn)
        navBtn.setOnClickListener {
            mDrawerLayout.openDrawer(GravityCompat.START)
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        navView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        navView.setupWithNavController(navController)
        navView.setNavigationItemSelectedListener(this)

        val bundle2 = bundleOf(
            "token" to userToken,
            "userDataId" to userDataId,
        )
        bundle2.putSerializable("userData", userData)
        navController.setGraph(navController.graph, bundle2)

        retrofit = RetrofitClientAuth(userToken)
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                userData = Repository.getUser(userDataId, userToken)
                withContext(Dispatchers.Main) {
                    if (userData != null) {
                        updateHeader()
                    }
                }
            }
        }


        switchDriver.isChecked = isDriverVisible
        switchDriver.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                lifecycleScope.launch {
                    withContext(Dispatchers.IO) {
                        userData?.visible = true
                        Repository.updateUser(userDataId, userData!!, userToken)
                        withContext(Dispatchers.Main) {
                            isDriverVisible = true
                            Toast.makeText(application, "Enabled", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
            } else {
                lifecycleScope.launch {
                    withContext(Dispatchers.IO) {
                        userData?.visible = false
                        Repository.updateUser(userDataId, userData!!, userToken)
                        withContext(Dispatchers.Main) {
                            isDriverVisible = false
                            Toast.makeText(application, "Disabled", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
            }
        }

        navView.btnRegisterAsDriver.setOnClickListener {
            mDialogView = LayoutInflater.from(this).inflate(R.layout.dialog_register_driver, null)
            val mBuilder = AlertDialog.Builder(this)
                .setView(mDialogView)
                .setTitle(getString(R.string.driver_registration))
            val mAlertDialog = mBuilder.show()

            mDialogView.car_image_button.setOnClickListener {
                startFileChooser()
            }

            mDialogView.checkBox_rd.setOnCheckedChangeListener { _, _ ->
                if (mDialogView.etPlaceInBoot.isGone) {
                    mDialogView.etPlaceInBoot.visibility = View.VISIBLE
                }

                else {
                    mDialogView.etPlaceInBoot.visibility = View.GONE
                }
            }

            mDialogView.btnRegister.setOnClickListener {
                val serial = mDialogView.etLicensePlateGiven.text.toString()
                val brand = mDialogView.etCarBrandGiven.text.toString()
                val model = mDialogView.etModelGiven.text.toString()
                val seatsText = mDialogView.etSeatsGiven.text.toString()
                val hasBoot = mDialogView.checkBox_rd.isChecked
                var placeInBoot = 0

                val baos = ByteArrayOutputStream()
                bitmap?.compress(Bitmap.CompressFormat.PNG, 50, baos)
                val b = baos.toByteArray()
                val carImage = Base64.encodeToString(b, Base64.DEFAULT)


                if (serial.isEmpty()) {
                    mDialogView.etLicensePlateGiven.error = "Please give your car's plate"
                    mDialogView.etLicensePlateGiven.requestFocus()
                    return@setOnClickListener
                }

                if (brand.isEmpty()) {
                    mDialogView.etCarBrandGiven.error = "Please give your car's brand"
                    mDialogView.etCarBrandGiven.requestFocus()
                    return@setOnClickListener
                }

                if (model.isEmpty()) {
                    mDialogView.etModelGiven.error = "Please give your car's model"
                    mDialogView.etModelGiven.requestFocus()
                    return@setOnClickListener
                }

                if (seatsText.isEmpty()) {
                    mDialogView.etSeatsGiven.error = "Please give your car's available free seats"
                    mDialogView.etSeatsGiven.requestFocus()
                    return@setOnClickListener
                }

                val seats = seatsText.toInt()

                if (mDialogView.checkBox_rd.isChecked) {
                    val placeInBootText = mDialogView.etPlaceInBoot.text.toString()
                    if (placeInBootText.isEmpty()) {
                        mDialogView.etPlaceInBoot.error = "Please give how many packets can be storaged in the car's boot"
                        mDialogView.etPlaceInBoot.requestFocus()
                        return@setOnClickListener
                    }
                    placeInBoot = placeInBootText.toInt()
                }

                var defaultResponse: DefaultResponse
                lifecycleScope.launch {
                    withContext(Dispatchers.IO) {
                        defaultResponse = Repository.updateCar(
                            userDataId, CarData(userData = userData, brand = brand, model = model, serial = serial, pic = carImage, hasBoot = hasBoot, seats = seats, placeInBoot = placeInBoot), userToken
                        )!!
                        withContext(Dispatchers.Main) {
                            Toast.makeText(application, defaultResponse.message, Toast.LENGTH_LONG)
                                .show()
                            mAlertDialog.dismiss()
                            updateLayout()
                            updateValidUser(userDataId, userData!!)
                        }
                    }
                }
            }
            mDialogView.btnCancel.setOnClickListener {
                mAlertDialog.dismiss()
            }
        }

        updateLayout()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.navigation, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START))
            mDrawerLayout.closeDrawer(GravityCompat.START)
        else {
            AlertDialog.Builder(this)
                .setMessage(getString(R.string.are_you_sure_want_logout))
                .setPositiveButton(getString(R.string.no), null)
                .setNegativeButton(getString(R.string.yes)) { _, _ ->
                    this.finish()
                }
                .show()
        }
    }

    private fun startFileChooser() {
        val intent = Intent().setType("image/*").setAction(Intent.ACTION_GET_CONTENT)
        startActivityForResult(
            Intent.createChooser(intent, getString(R.string.choose_picture)),
            111
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 111 && resultCode == Activity.RESULT_OK && data != null) {
            filepath = data.data!!
            val contentResolver = this.contentResolver
            bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filepath)
            mDialogView.car_image_button.setImageBitmap(bitmap)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        if (item.itemId != R.id.nav_active_driver)
            drawer_layout.closeDrawers()

        if (item.itemId == nav_view.checkedItem?.itemId)
            return false

        Handler().postDelayed({
            when (item.itemId) {
                R.id.nav_map -> {
                    val bundle = bundleOf("userDataId" to userDataId, "token" to userToken)
                    findNavController(R.id.nav_host_fragment).navigate(R.id.nav_map, bundle)
                }

                R.id.nav_credits -> {
                    val bundle = bundleOf("userDataId" to userDataId, "token" to userToken)
                    findNavController(R.id.nav_host_fragment).navigate(R.id.nav_credits, bundle)
                }

                R.id.nav_rating -> {
                    val bundle = bundleOf("userDataId" to userDataId, "token" to userToken)
                    findNavController(R.id.nav_host_fragment).navigate(R.id.nav_rating, bundle)
                }

                R.id.nav_trips -> {
                    val bundle = bundleOf("userDataId" to userDataId, "token" to userToken)
                    //findNavController(R.id.nav_host_fragment).navigate(R.id.nav_trips, bundle)
                    findNavController(R.id.nav_host_fragment).navigate(R.id.nav_trips_activity, bundle)
                }

                R.id.nav_active_driver -> {
                    switchDriver.isChecked = !switchDriver.isChecked
                }

                R.id.nav_settings -> {
                    val bundle = bundleOf("userDataId" to userDataId, "token" to userToken)
                    findNavController(R.id.nav_host_fragment).navigate(R.id.nav_settings, bundle)
                }

                R.id.nav_logout -> {
                    AlertDialog.Builder(this)
                        .setMessage(getString(R.string.are_you_sure_want_logout))
                        .setPositiveButton(getString(R.string.no), null)
                        .setNegativeButton(getString(R.string.yes)) { _, _ ->
                            this.finish()
                        }
                        .show()
                }
            }
        }, 200)

        if (item.itemId == R.id.nav_active_driver)
            return false

        return true
    }

    private fun updateHeader() {
        val headerView: View = navView.getHeaderView(0)

        val headerName: TextView = headerView.header_name

        headerName.text = userData?.username

        val headerPic: ImageView = headerView.header_image
        if (userData?.picture != null) {
            val imageBytes = Base64.decode(userData?.picture, 0)
            headerPic.setImageBitmap(
                BitmapFactory.decodeByteArray(
                    imageBytes, 0, imageBytes.size
                )
            )
        }
    }

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private fun updateLayout() {
        var usersCarData: CarData?

        val btnRegisterAsDriver: Button = findViewById(R.id.btnRegisterAsDriver)
        val activeDriverMenuItem: MenuItem = navView.menu.findItem(R.id.nav_active_driver)
        val ratingMenuItem: MenuItem = navView.menu.findItem(R.id.nav_rating)
        val swSwitchDriver: Switch = findViewById(R.id.switchDriver)

        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                usersCarData = Repository.getCar(userDataId, userToken)
                userData = Repository.getUser(userDataId, userToken)

                withContext(Dispatchers.Main) {
                    if (usersCarData?.serial != null) {
                        btnRegisterAsDriver.visibility = View.GONE
                    }
                    else {
                        btnRegisterAsDriver.visibility = View.VISIBLE
                    }

                    if(userData != null) {
                        if (!userData!!.valid) {
                            activeDriverMenuItem.isVisible = false
                            swSwitchDriver.visibility = View.GONE
                            ratingMenuItem.isVisible = false
                        } else {
                            activeDriverMenuItem.isVisible = true
                            swSwitchDriver.visibility = View.VISIBLE
                            ratingMenuItem.isVisible = true
                        }
                    }
                }
            }
        }
    }

    private fun updateValidUser(userDataId: Int, userData: UserData) {
        userData.valid = false
        var defaultResponse: DefaultResponse?

        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                defaultResponse = Repository.updateUser(userDataId, userData, userToken)
                withContext(Dispatchers.Main) {
                    Toast.makeText(application, defaultResponse?.message, Toast.LENGTH_LONG)
                        .show()
                }
            }
        }
    }

    private fun updateDetailsCyclic() {
        runnable = Runnable {
            updateLayout()
            updateHeader()
            handler.postDelayed(runnable, 5000)
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
}