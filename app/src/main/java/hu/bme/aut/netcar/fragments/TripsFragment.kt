package hu.bme.aut.netcar.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import hu.bme.aut.netcar.R
import hu.bme.aut.netcar.data.User
import hu.bme.aut.netcar.model.TripsAdapter
import hu.bme.aut.netcar.network.Api
import hu.bme.aut.netcar.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TripsFragment : Fragment() {

    private lateinit var adapter: TripsAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var api: Api

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_trips, container, false)

        recyclerView = view.findViewById(R.id.rv_recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(activity)

        setUpData()
        recyclerView.adapter = adapter

        return view
    }

    private fun setUpData() {

        val dataCall = api.getUsers()
        dataCall.enqueue(object : Callback<List<User>> {
            override fun onResponse(
                call: Call<List<User>>,
                response: Response<List<User>>
            ) {
                val usersFromRetrofit = response.body()
                if (usersFromRetrofit != null) {
                    for (user in usersFromRetrofit) {
                        adapter.addUser(user)
                    }
                }
            }

            override fun onFailure(call: Call<List<User>>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        api = RetrofitClient.INSTANCE
        adapter = TripsAdapter()
    }

}