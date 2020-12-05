package hu.bme.aut.netcar.model

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import hu.bme.aut.netcar.R
import hu.bme.aut.netcar.data.UserData

class TripsAdapter : RecyclerView.Adapter<TripsAdapter.TripsViewHolder>() {

    private val list: ArrayList<UserData> = ArrayList()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TripsViewHolder {
        return TripsViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.passenger_trips_list_item, parent, false))
    }

    class TripsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        //val carPicture: ImageView = itemView.iv_carImage
        //val iconPicture: ImageView = itemView.iv_tripIcon

        var driver: UserData? = null
    }

    override fun onBindViewHolder(holder: TripsViewHolder, position: Int) {
        val driver = list[position]

        // TODO: carPicture, iconPicture

        holder.driver = driver
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun addUser(userData: UserData) {
        list.add(userData)
        notifyItemInserted(list.size)
        notifyDataSetChanged()
    }
}