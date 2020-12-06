package hu.bme.aut.netcar.model

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import hu.bme.aut.netcar.R
import hu.bme.aut.netcar.data.SRstatus
import hu.bme.aut.netcar.data.ServiceRequest
import hu.bme.aut.netcar.network.DefaultResponse
import hu.bme.aut.netcar.network.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TripsAdapter(val context: Context,
                   private val isDriverView: Boolean,
                   val userToken: String,
                   private val listener: TripsAdapterListener) : RecyclerView.Adapter<TripsAdapter.TripsViewHolder>() {

    private val serviceRequests: MutableList<ServiceRequest> = mutableListOf()
    private var rating: Int = -1

    interface TripsAdapterListener {
        fun refresh()
        fun onClickItem(position: Int) : Int
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TripsViewHolder {
        return TripsViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.driver_trips_list_item, parent, false))
    }

    override fun onBindViewHolder(holder: TripsViewHolder, position: Int) {
        val serviceRequest = serviceRequests[position]

        if (isDriverView) {
            GlobalScope.launch {
                withContext(Dispatchers.IO) {
                    val imageBase64 = Repository.getUserPicture(serviceRequest.passengerID!!)
                    val contactName = Repository.getUser(serviceRequest.passengerID!!, userToken)?.username
                    withContext(Dispatchers.Main) {
                        holder.contactImage.setImageBitmap(decodePicture(imageBase64!!))
                        holder.startDate.text = serviceRequest.startTime
                        holder.contactUsername.text = contactName
                        holder.payment.text = serviceRequest.payment.toString()
                        if (serviceRequest.finishTime != null) {
                            holder.endDate.text = serviceRequest.finishTime
                        }
                        when (serviceRequest.sRstatus) {
                            SRstatus.PENDING -> {
                                holder.topButton.setImageResource(R.drawable.ic_accept)
                                holder.bottomButton.setImageResource(R.drawable.ic_deny)

                                holder.topButton.setOnClickListener {
                                    GlobalScope.launch {
                                        withContext(Dispatchers.IO) {
                                            serviceRequest.sRstatus = SRstatus.INPROGRESS
                                            val defaultResponse = Repository.updateRequest(serviceRequest, userToken)

                                            for (sr in serviceRequests) {
                                                if (sr.SRID != serviceRequest.SRID && sr.sRstatus == SRstatus.PENDING) {
                                                    sr.sRstatus = SRstatus.DENIED
                                                    Repository.updateRequest(sr, userToken)
                                                }
                                            }

                                            withContext(Dispatchers.Main) {
                                                if (defaultResponse?.message == "Successful update.") {
                                                    Toast.makeText(context, "Request has been successfully accepted", Toast.LENGTH_LONG).show()
                                                    listener.refresh()
                                                }
                                            }
                                        }
                                    }
                                }
                                holder.bottomButton.setOnClickListener {
                                    GlobalScope.launch {
                                        withContext(Dispatchers.IO) {
                                            serviceRequest.sRstatus = SRstatus.DENIED
                                            val defaultResponse = Repository.updateRequest(serviceRequest, userToken)

                                            withContext(Dispatchers.Main) {
                                                if (defaultResponse?.message == "Successful update.") {
                                                    Toast.makeText(context, "Request has been successfully denied", Toast.LENGTH_LONG).show()
                                                    listener.refresh()
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            SRstatus.DENIED -> {
                                holder.topButton.setImageResource(R.drawable.ic_denied)
                                holder.bottomButton.visibility = View.INVISIBLE
                            }
                            SRstatus.INPROGRESS -> {
                                holder.topButton.setImageResource(R.drawable.ic_in_progress)
                                holder.bottomButton.visibility = View.INVISIBLE
                            }
                            SRstatus.FINISHED -> {
                                holder.topButton.setImageResource(R.drawable.ic_finished)
                                holder.bottomButton.visibility = View.INVISIBLE
                            }
                        }
                    }
                }
            }
        } else {
            GlobalScope.launch {
                withContext(Dispatchers.IO) {
                    val imageBase64 = Repository.getUserPicture(serviceRequest.driverID!!)
                    val contactName = Repository.getUser(serviceRequest.driverID!!, userToken)?.username
                    withContext(Dispatchers.Main) {
                        holder.contactImage.setImageBitmap(decodePicture(imageBase64!!))
                        holder.startDate.text = serviceRequest.startTime
                        holder.contactUsername.text = contactName
                        holder.payment.text = serviceRequest.payment.toString()
                        if (serviceRequest.finishTime != null) {
                            holder.endDate.text = serviceRequest.finishTime
                        }
                        when (serviceRequest.sRstatus) {
                            SRstatus.PENDING -> {
                                holder.topButton.setImageResource(R.drawable.ic_pending)
                                holder.bottomButton.visibility = View.INVISIBLE
                            }
                            SRstatus.DENIED -> {
                                holder.topButton.setImageResource(R.drawable.ic_denied)
                                holder.bottomButton.visibility = View.INVISIBLE
                            }
                            SRstatus.INPROGRESS -> {
                                holder.topButton.setImageResource(R.drawable.ic_in_progress)
                                holder.bottomButton.visibility = View.INVISIBLE
                            }
                            SRstatus.FINISHED -> {
                                holder.topButton.setImageResource(R.drawable.ic_finished)
                                holder.bottomButton.visibility = View.INVISIBLE
                            }
                        }
                    }
                }
            }
        }

        holder.serviceRequest = serviceRequest
    }

    private fun decodePicture(imageBase64: String): Bitmap? {
        val imageBytes = Base64.decode(imageBase64, 0)
        return BitmapFactory.decodeByteArray(
            imageBytes, 0, imageBytes.size
        )
    }

    inner class TripsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val contactImage: ImageView = itemView.findViewById(R.id.ivContactImage)
        val topButton: ImageButton = itemView.findViewById(R.id.ibTopButton)
        val bottomButton: ImageButton = itemView.findViewById(R.id.ibBottomButton)
        val contactUsername: TextView = itemView.findViewById(R.id.tvContactUsername)
        val startDate: TextView = itemView.findViewById(R.id.tvStartDate)
        val endDate: TextView = itemView.findViewById(R.id.tvEndDate)
        val payment: TextView = itemView.findViewById(R.id.tvPayment)

        var serviceRequest: ServiceRequest? = null

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            Log.d("adapter", "onClick()")
            val serviceRequest = serviceRequests[adapterPosition]
            if (!isDriverView && serviceRequest.sRstatus == SRstatus.INPROGRESS) {
                rating = listener.onClickItem(adapterPosition)

                GlobalScope.launch {
                    withContext(Dispatchers.IO) {
                        val passengerUserData = Repository.getUser(serviceRequest.passengerID!!, userToken)
                        passengerUserData?.credits = passengerUserData?.credits?.minus(serviceRequest.payment!!)
                        val passengerUpdateResponse =
                            Repository.updateUser(serviceRequest.passengerID!!, passengerUserData!!, userToken)
                        var defaultResponse: DefaultResponse? = null

                        if (passengerUpdateResponse?.message == "USER_SUCCESSFUL_UPDATED") {
                            val driverUserData = Repository.getUser(serviceRequest.driverID!!, userToken)
                            driverUserData?.credits = driverUserData?.credits?.plus(serviceRequest.payment!!)
                            driverUserData?.ratings?.add(rating)
                            val driverUpdateResponse =
                                Repository.updateUser(serviceRequest.driverID!!, driverUserData!!, userToken)
                            if (driverUpdateResponse?.message == "USER_SUCCESSFUL_UPDATED") {
                                serviceRequest.sRstatus = SRstatus.FINISHED
                                defaultResponse = Repository.updateRequest(serviceRequest, userToken)
                            }
                        }

                        withContext(Dispatchers.Main) {
                            if (defaultResponse?.message == "Successful update.") {
                                Toast.makeText(
                                    context,
                                    "Request has been successfully finished",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return serviceRequests.size
    }

    fun deleteAll() {
        serviceRequests.clear()
        notifyDataSetChanged()
    }

    fun addAll(serviceRequest: List<ServiceRequest>) {
        // Just kinda' sort
        val pending: MutableList<ServiceRequest> = mutableListOf()
        val finished: MutableList<ServiceRequest> = mutableListOf()
        val denied: MutableList<ServiceRequest> = mutableListOf()

        for (s in serviceRequest) {
            when (s.sRstatus) {
                SRstatus.INPROGRESS -> serviceRequests.add(s)
                SRstatus.PENDING -> pending.add(s)
                SRstatus.FINISHED -> finished.add(s)
                SRstatus.DENIED -> denied.add(s)
            }
        }

        pending.sortByDescending { it.startTime }
        finished.sortByDescending { it.startTime }
        denied.sortByDescending { it.startTime }

        serviceRequests.addAll(pending)
        serviceRequests.addAll(finished)
        serviceRequests.addAll(denied)

        notifyDataSetChanged()
    }
}