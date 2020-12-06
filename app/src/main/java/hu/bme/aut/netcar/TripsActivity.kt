package hu.bme.aut.netcar

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import androidx.appcompat.app.ActionBar
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import hu.bme.aut.netcar.fragments.DriverTripsFragment
import hu.bme.aut.netcar.fragments.PassengerTripsFragment
import kotlinx.android.synthetic.main.abs_layout.*
import kotlinx.android.synthetic.main.activity_trips.*

@Suppress("DEPRECATION")
class TripsActivity : AppCompatActivity() {
    private var userDataId: Int? = null
    private var userToken: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trips)

        val b = intent.extras
        userDataId = b?.getInt("userDataId")
        userToken = b?.getString("userToken")

        setupViewPager(viewPager)
        tabs.setupWithViewPager(viewPager)

        supportActionBar?.title = Html.fromHtml("<font color=\"#FFFFFF\">Trips</font>")
        supportActionBar?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar?.setCustomView(R.layout.abs_layout)
        supportActionBar?.elevation = 0f

        ibBack.setOnClickListener{
            this.finish()
        }
    }

    private fun setupViewPager(viewPager: ViewPager) {
        val adapter = TripViewPagerAdapter(supportFragmentManager)

        val bundle = bundleOf(
            "userDataId" to userDataId,
            "userToken" to userToken
        )

        val passengerTripsFragment = PassengerTripsFragment()
        val driverTripsFragment = DriverTripsFragment()

        passengerTripsFragment.arguments = bundle
        driverTripsFragment.arguments = bundle

        adapter.addFragment(passengerTripsFragment , " Passenger ")
        adapter.addFragment(driverTripsFragment, " Driver ")

        viewPager.adapter = adapter
    }

    inner class TripViewPagerAdapter(manager: FragmentManager) : FragmentPagerAdapter(manager){

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