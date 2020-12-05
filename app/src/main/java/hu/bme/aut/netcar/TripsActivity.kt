package hu.bme.aut.netcar

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.view.Menu
import android.view.View
import androidx.appcompat.app.ActionBar
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import hu.bme.aut.netcar.fragments.DriverTripsFragment
import hu.bme.aut.netcar.fragments.PassengerTripsFragment
import kotlinx.android.synthetic.main.abs_layout.*
import kotlinx.android.synthetic.main.activity_trips.*

@Suppress("DEPRECATION")
class TripsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trips)

        val adapter = MyViewPagerAdapter(supportFragmentManager)
        adapter.addFragment(PassengerTripsFragment() , " Passenger ")
        adapter.addFragment(DriverTripsFragment(), " Driver ")
        viewPager.adapter = adapter
        tabs.setupWithViewPager(viewPager)
        supportActionBar?.title = Html.fromHtml("<font color=\"#FFFFFF\">Trips</font>")
        supportActionBar?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar?.setCustomView(R.layout.abs_layout)
        supportActionBar?.elevation = 0f
        ibBack.setOnClickListener{
            this.finish()
        }
    }

    class MyViewPagerAdapter(manager: FragmentManager) : FragmentPagerAdapter(manager){

        private val fragmentList : MutableList<Fragment> = ArrayList()
        private val titleList : MutableList<String> = ArrayList()

        override fun getItem(position: Int): Fragment {
            return fragmentList[position]
        }

        override fun getCount(): Int {
            return fragmentList.size
        }

        fun addFragment(fragment: Fragment,title:String){
            fragmentList.add(fragment)
            titleList.add(title)
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return titleList[position]
        }

    }
}