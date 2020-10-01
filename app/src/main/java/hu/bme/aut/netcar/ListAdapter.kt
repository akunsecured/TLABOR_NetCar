package hu.bme.aut.netcar

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView

class ListAdapter(val context: Context, val list: ArrayList<Car>): BaseAdapter() {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = LayoutInflater.from(context).inflate(R.layout.row_layout, parent, false)

        val carImage = view.findViewById(R.id.car_image) as ImageView
        val carId = view.findViewById(R.id.car_id) as TextView
        val carContent = view.findViewById(R.id.car_content) as TextView
        val carPlate = view.findViewById(R.id.car_plate) as TextView
        val carWheel = view.findViewById(R.id.car_wheels) as TextView
        val carSeat = view.findViewById(R.id.car_seats) as TextView
        val carYear = view.findViewById(R.id.car_year) as TextView
        val carBrand = view.findViewById(R.id.car_brand) as TextView

        carId.text = list[position].id.toString()
        carContent.text = list[position].content
        carPlate.text = list[position].plate
        carWheel.text = list[position].wheel.toString()
        carSeat.text = list[position].seat.toString()
        carYear.text = list[position].year.toString()
        carBrand.text = list[position].brand

        return view
    }

    override fun getItem(position: Int): Any {
        return list[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return list.size
    }

}