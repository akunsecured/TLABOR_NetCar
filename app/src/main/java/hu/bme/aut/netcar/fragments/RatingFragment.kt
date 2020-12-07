package hu.bme.aut.netcar.fragments

import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import hu.bme.aut.netcar.R
import hu.bme.aut.netcar.network.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.pow
import kotlin.math.roundToInt

class RatingFragment : Fragment() {

    private var userDataId: Int = -1
    private var userToken: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inflater = TransitionInflater.from(requireContext())
        enterTransition = inflater.inflateTransition(R.transition.slide_right)
        exitTransition = inflater.inflateTransition(R.transition.fade)

        val id = arguments?.getInt("userDataId")
        userDataId = id!!

        val token = arguments?.getString("token")
        userToken = token!!
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_rating, container, false)

        val ratingTextView : TextView = rootView.findViewById(R.id.tvRating)

        var ratingsArray: ArrayList<Int>
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                val userData = Repository.getUser(userDataId, userToken)
                ratingsArray = userData?.ratings!!

                withContext(Dispatchers.Main) {
                    if (!ratingsArray.isNullOrEmpty()) {
                        var sum = 0
                        for (rating in ratingsArray) {
                            sum += rating
                        }
                        val average: Double = (sum.toDouble() / ratingsArray.size.toDouble()).roundTo(2)

                        ratingTextView.text = "$average out of 5 from ${ratingsArray.size} user(s)"
                    }
                    else {
                        ratingTextView.text = "You don't have any ratings yet."
                    }
                }
            }
        }

        return rootView
    }

    fun Double.roundTo(numFractionDigits: Int): Double {
        val factor = 10.0.pow(numFractionDigits.toDouble())
        return (this * factor).roundToInt() / factor
    }
}