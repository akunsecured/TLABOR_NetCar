package hu.bme.aut.netcar

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar


class NavigationActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigation)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
        fab.isEnabled = false
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_map, R.id.nav_credits, R.id.nav_rating
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
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
        AlertDialog.Builder(this)
        .setMessage(getString(R.string.are_you_sure_want_logout))
        .setPositiveButton(getString(R.string.no), null)
        .setNegativeButton(getString(R.string.yes)) { dialogInterface, i -> this.finish()
        }
        .show()
    }
}