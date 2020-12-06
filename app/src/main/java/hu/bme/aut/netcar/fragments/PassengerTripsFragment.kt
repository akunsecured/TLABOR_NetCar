package hu.bme.aut.netcar.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RatingBar
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import hu.bme.aut.netcar.R
import hu.bme.aut.netcar.model.TripsAdapter
import hu.bme.aut.netcar.network.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PassengerTripsFragment : Fragment(), TripsAdapter.TripsAdapterListener {

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
        val view = inflater.inflate(R.layout.fragment_passenger_trips, container, false)

        recyclerView = view.findViewById(R.id.rv_passengerRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(activity)

        setUpData()

        return view
    }

    private fun setUpData() {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                val serviceRequests = Repository.getRequestsByPassenger(userDataId, userToken)


                withContext(Dispatchers.Main) {
                    adapter.addAll(serviceRequests!!)
                    recyclerView.adapter = adapter
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        userDataId = arguments?.getInt("userDataId")!!
        userToken = arguments?.getString("userToken")!!

        adapter = TripsAdapter(requireContext(), false, userToken, this)
    }

    private fun updateAdapterData() {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                val serviceRequests = Repository.getRequestsByPassenger(userDataId, userToken)
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
            // in every 45 sec refresh
            handler.postDelayed(runnable, 45000)
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
        var rateValue = -1

        val dialogLayout = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_rating, null)
        val builder = AlertDialog.Builder(requireContext()).setView(dialogLayout)
        val alertdialog = builder.show()

        val submitButton: Button = dialogLayout.findViewById(R.id.btnSubmit)
        val ratingBar: RatingBar = dialogLayout.findViewById(R.id.rbRating)
        submitButton.setOnClickListener {
            rateValue = (ratingBar.rating).toInt()
            adapter.finishRequest(position, rateValue)
            alertdialog.dismiss()
        }
    }
}