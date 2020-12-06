package hu.bme.aut.netcar.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import hu.bme.aut.netcar.R
import hu.bme.aut.netcar.model.TripsAdapter
import hu.bme.aut.netcar.network.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DriverTripsFragment : Fragment(), TripsAdapter.TripsAdapterListener {

    private lateinit var adapter: TripsAdapter
    private lateinit var recyclerView: RecyclerView
    private var userDataId: Int = -1
    private var userToken: String = ""

    private val handler: Handler = Handler(Looper.getMainLooper())
    private lateinit var runnable: Runnable

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_driver_trips, container, false)

        recyclerView = view.findViewById(R.id.rv_driverRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(activity)

        setUpData()

        return view
    }

    private fun setUpData() {
        adapter = TripsAdapter(requireContext(), true, userToken, this)
        recyclerView.adapter = adapter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        userDataId = arguments?.getInt("userDataId")!!
        userToken = arguments?.getString("userToken")!!
    }

    private fun updateAdapterData() {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                val serviceRequests = Repository.getRequestsByDriver(userDataId, userToken)
                withContext(Dispatchers.Main) {
                    adapter.deleteAll()
                    adapter.addAll(serviceRequests!!)
                }
            }
        }
    }

    private fun updateDetailsCyclic() {
        runnable = Runnable {
            updateAdapterData()
            // in every 15 sec refresh
            handler.postDelayed(runnable, 15000)
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

    override fun refresh() {
        updateAdapterData()
    }

    override fun onClickItem(position: Int) {
        // never happens
    }
}