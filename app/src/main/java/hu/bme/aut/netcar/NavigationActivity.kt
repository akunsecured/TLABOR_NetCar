package hu.bme.aut.netcar

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import hu.bme.aut.netcar.data.UserData
import hu.bme.aut.netcar.network.RetrofitClient
import kotlinx.android.synthetic.main.activity_navigation.*
import kotlinx.android.synthetic.main.activity_navigation.view.*
import kotlinx.android.synthetic.main.dialog_register_driver.view.*
import kotlinx.android.synthetic.main.nav_header_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Suppress("DEPRECATION")
class NavigationActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle
    private lateinit var navBtn: FloatingActionButton
    private lateinit var mDrawerLayout: DrawerLayout
    private lateinit var filepath: Uri
    private lateinit var bitmap: Bitmap
    private lateinit var mDialogView: View
    private var userData : UserData? = null
    var userDataId: Int = -1
    companion object{
        var USERDATA_ID = "USERDATA_ID"
    }

    @SuppressLint("InflateParams")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigation)
        userDataId = this.intent.getIntExtra(USERDATA_ID, -1)
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

        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        navView.setupWithNavController(navController)

        // Logout item
        navView.menu.findItem(R.id.nav_logout).setOnMenuItemClickListener {
            AlertDialog.Builder(this)
                .setMessage(getString(R.string.are_you_sure_want_logout))
                .setPositiveButton(getString(R.string.no), null)
                .setNegativeButton(getString(R.string.yes)) { _, _ -> this.finish()
                }
                .show()

            return@setOnMenuItemClickListener true
        }

        // Active Driver item
        navView.menu.findItem(R.id.nav_active_driver).setOnMenuItemClickListener {
            switchDriver.isChecked = !switchDriver.isChecked
            return@setOnMenuItemClickListener true
        }

        //retrofit
        RetrofitClient.INSTANCE.getUserById(userDataId)
            .enqueue(object : Callback<UserData> {
            override fun onFailure(call: Call<UserData>, t: Throwable) {
                Toast.makeText(application, "Something went wrong", Toast.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call<UserData>, response: Response<UserData>) {
                if (response.body() != null) {
                    userData = response.body()
                    header_name.text = userData!!.name
                    header_email.text = userData!!.email
                }
            }
        })

        switchDriver.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                Toast.makeText(application, "Enabled", Toast.LENGTH_SHORT)
                    .show()
            } else {
                Toast.makeText(application, "Disabled", Toast.LENGTH_SHORT)
                    .show()
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

            mDialogView.btnRegister.setOnClickListener{
                mAlertDialog.dismiss()
                /*
                val plate = mDialogView.etLicensePlateGiven.text.toString()
                val brand = mDialogView.etLicensePlateGiven.text.toString()
                val model = mDialogView.etLicensePlateGiven.text.toString()
                val seats = mDialogView.etLicensePlateGiven.text.toString()
                */
            }
            mDialogView.btnCancel.setOnClickListener {
                mAlertDialog.dismiss()
            }
        }
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
}