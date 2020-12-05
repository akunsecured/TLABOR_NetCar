package hu.bme.aut.netcar.fragments

import android.os.Bundle
import android.transition.TransitionInflater
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import hu.bme.aut.netcar.R
import hu.bme.aut.netcar.data.UserData
import hu.bme.aut.netcar.model.TripsAdapter
import hu.bme.aut.netcar.network.RetrofitClientAuth
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DriverTripsFragment : Fragment() {

    private lateinit var adapter: TripsAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var retrofit: RetrofitClientAuth
    private var userDataId: Int = -1
    private var userToken: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_driver_trips, container, false)

        recyclerView = view.findViewById(R.id.rv_recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(activity)

        //setUpData()
        recyclerView.adapter = adapter

        return view
    }

    private fun setUpData() {

        retrofit.INSTANCE.getUsers()
            .enqueue(object : Callback<List<UserData>> {
                override fun onResponse(
                    call: Call<List<UserData>>,
                    response: Response<List<UserData>>
                ) {
                    val usersFromRetrofit = response.body()
                    if (usersFromRetrofit != null) {
                        for (user in usersFromRetrofit) {
                            adapter.addUser(user)
                        }
                    }
                }

                override fun onFailure(call: Call<List<UserData>>, t: Throwable) {
                    TODO("Not yet implemented")
                }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val inflater = TransitionInflater.from(requireContext())
        enterTransition = inflater.inflateTransition(R.transition.slide_right)
        exitTransition = inflater.inflateTransition(R.transition.fade)

        //retrofit = RetrofitClientAuth()
        adapter = TripsAdapter()

        /*val id = arguments?.getInt("userDataId")
        userDataId = id!!

        val token = arguments?.getString("userToken")
        userToken = token!!*/
    }

}