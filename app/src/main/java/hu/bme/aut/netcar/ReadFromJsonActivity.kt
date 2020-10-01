package hu.bme.aut.netcar

import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_read_from_json.*
import org.json.JSONArray
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class ReadFromJsonActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_read_from_json)

        val url = "https://temalab-291207.ew.r.appspot.com/greeting"

        AsyncTaskHandleJson().execute(url)
    }

    inner class AsyncTaskHandleJson : AsyncTask<String, String, String>() {

        override fun doInBackground(vararg url: String?): String {

            var text: String
            val connection = URL(url[0]).openConnection() as HttpsURLConnection

            try {
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

            while (x < jsonArray.length()) {
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