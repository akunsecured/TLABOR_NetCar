package hu.bme.aut.netcar

import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_image_view.*
import kotlinx.android.synthetic.main.activity_read_from_json.*
import kotlinx.android.synthetic.main.row_layout.view.*
import org.json.JSONArray
import org.json.JSONObject
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class ReadFromJsonActivity : AppCompatActivity(){


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_read_from_json)

        val url = "https://temalab-291207.ew.r.appspot.com/greeting"

        AsyncTaskHandleJson().execute(url)

        var counter = 0

        /*while(counter < list.size + 1){
            var rowItem = LayoutInflater.from(this).inflate(R.layout.row_layout, null)
            Picasso.get()
                .load("https://www.pngitem.com/pimgs/m/7-77071_transparent-blue-car-clipart-sally-cars-hd-png.png")
                .into(rowItem.car_image)
            rowItem.car_id.text = list.get(counter).id.toString()
            rowItem.car_content.text = list.get(counter).content
            rowItem.car_plate.text = list.get(counter).plate
            rowItem.car_wheels.text = list.get(counter).wheel.toString()
            rowItem.car_seats.text = list.get(counter).seat.toString()
            rowItem.car_year.text = list.get(counter).year.toString()
            rowItem.car_brand.text = list.get(counter).brand
            cars_list.addView(rowItem)

            counter++
        }*/
    }

    inner class AsyncTaskHandleJson : AsyncTask<String, String, String>() {
        override fun doInBackground(vararg url: String?): String {

            var text: String
            val connection = URL(url[0]).openConnection() as HttpsURLConnection
            try{
                connection.connect()
                text = connection.inputStream.use { it.reader().use{reader -> reader.readText()} }
            } finally {
                connection.disconnect()
            }
            return text
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            handleJson(result)
        }

        private fun handleJson(jsonString: String?) {
            val jsonArray = JSONArray(jsonString)
            var x = 0
            val list = ArrayList<Car>()
            while(x < jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(x)
                list.add(
                    Car(
                        jsonObject.getInt("id"),
                        jsonObject.getString("content"),
                        jsonObject.getString("rendszam"),
                        jsonObject.getInt("kerek"),
                        jsonObject.getInt("ules"),
                        jsonObject.getInt("ev"),
                        jsonObject.getString("marka")
                    )
                )
                x++
            }

            val adapter = ListAdapter(this@ReadFromJsonActivity, list)
            cars_list.adapter = adapter
        }
    }
}