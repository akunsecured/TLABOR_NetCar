package hu.bme.aut.netcar

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Switch
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
import hu.bme.aut.netcar.data.DataResult
import hu.bme.aut.netcar.network.DriverAPI
import kotlinx.android.synthetic.main.activity_navigation.*
import kotlinx.android.synthetic.main.nav_header_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class NavigationActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle
    private lateinit var navBtn: FloatingActionButton
    private lateinit var mDrawerLayout: DrawerLayout
    private lateinit var driverAPI: DriverAPI
    var userid: Int = 0
    companion object{
        var USER_ID = "USER_ID"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigation)
        userid = this.intent.getIntExtra(USER_ID, -1)
        mDrawerLayout = drawer_layout
        actionBarDrawerToggle = ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close)
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
                .setNegativeButton(getString(R.string.yes)) { dialogInterface, i -> this.finish()
                }
                .show()

            return@setOnMenuItemClickListener true;
        }

        //retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl("https://temalab-291207.ew.r.appspot.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        driverAPI = retrofit.create(DriverAPI::class.java)

        val dataCall = driverAPI.getDetails()
        dataCall.enqueue(object : Callback<List<DataResult>> {
            override fun onFailure(call: Call<List<DataResult>>, t: Throwable) {
                Toast.makeText(application, "Something went wrong", Toast.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call<List<DataResult>>, response: Response<List<DataResult>>) {
                var dataResults = response.body()
                if (dataResults != null) {
                    for(dr : DataResult in dataResults){
                        if(dr.id!! == userid){
                            header_name.text = dr.content
                            header_email.text = dr.rendszam
                        }
                    }
                }
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.navigation, menu)
        val item: MenuItem = menu.findItem(R.id.switchId) as MenuItem
        item.setActionView(R.layout.switch_layout)
        val switchActiveDriver : Switch = item.actionView.findViewById(R.id.switchAB)
        switchActiveDriver.isChecked = false

        switchActiveDriver.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                Toast.makeText(application, "ON", Toast.LENGTH_SHORT)
                    .show()
            } else {
                Toast.makeText(application, "OFF", Toast.LENGTH_SHORT)
                    .show()
            }
        }
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
                .setNegativeButton(getString(R.string.yes)) { dialogInterface, i ->
                    this.finish()
                }
                .show()
        }
    }
}