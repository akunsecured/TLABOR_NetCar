package hu.bme.aut.netcar

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import hu.bme.aut.netcar.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_image_view.*
import java.net.HttpURLConnection
import java.net.URL

class ImageViewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_view)

        btnImageView.setOnClickListener {

            Picasso.get()
                .load("https://www.pngitem.com/pimgs/m/7-77071_transparent-blue-car-clipart-sally-cars-hd-png.png")
                .into(imageView)

            /*
            makePicFromUrl("https://www.pngitem.com/pimgs/m/7-77071_transparent-blue-car-clipart-sally-cars-hd-png.png",
                imageView)
            */
        }
    }

    class ImageTask : AsyncTask<String, Void, Bitmap>() {
        override fun doInBackground(vararg urls: String?): Bitmap? {
            try {
                var url = URL(urls[0])
                var connection = url.openConnection() as HttpURLConnection
                connection.connect()
                var inputStream = connection.getInputStream()
                var myBitmap = BitmapFactory.decodeStream(inputStream)
                return  myBitmap
        } catch(e : Exception) {
            e.printStackTrace()
        }

        return null
    }
}

fun makePicFromUrl(url : String?, imageView : ImageView) {
    try {
        var image: Bitmap? = ImageTask().execute(url).get()
            imageView.setImageBitmap(image)
        } catch(e : Exception) {
            e.printStackTrace()
        }
    }
}