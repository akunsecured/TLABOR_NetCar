package hu.bme.aut.netcar.model

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import hu.bme.aut.netcar.R
import hu.bme.aut.netcar.data.User
import kotlinx.android.synthetic.main.trips_list_item.view.*

class TripsAdapter() : RecyclerView.Adapter<TripsAdapter.TripsViewHolder>() {

    private val list: ArrayList<User> = ArrayList()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TripsViewHolder {
        return TripsViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.trips_list_item, parent, false))
    }

    class TripsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val driverName: TextView = itemView.tv_driverName
        val carPicture: ImageView = itemView.iv_carImage
        val iconPicture: ImageView = itemView.iv_tripIcon

        var driver: User? = null
    }

    override fun onBindViewHolder(holder: TripsAdapter.TripsViewHolder, position: Int) {
        val driver = list[position]

        holder.driverName.text = driver.name
        // TODO: carPicture, iconPicture

        holder.driver = driver
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun addUser(user: User) {
        list.add(user)
        notifyItemInserted(list.size)
    }
}