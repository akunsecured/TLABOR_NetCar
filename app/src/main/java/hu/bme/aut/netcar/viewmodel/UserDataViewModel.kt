package hu.bme.aut.netcar.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import hu.bme.aut.netcar.data.UserData
import hu.bme.aut.netcar.network.RetrofitClientAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class UserDataViewModel(private val userId: Int, token: String): ViewModel() {

    private lateinit var userData: UserData
    private val retrofitClientAuth: RetrofitClientAuth = RetrofitClientAuth(token)
    private val viewModelJob = SupervisorJob()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    private val ioScope = CoroutineScope(Dispatchers.IO + viewModelJob)
    lateinit var liveData: LiveData<UserData>

    fun getUserData() : UserData {
        if (userData == null) {
            ioScope.launch {
                userData = retrofitClientAuth.INSTANCE.getUserById(userId)
            }
        }

        return userData
    }

    fun getUserDataLiveData() : LiveData<UserData> {
        val mutableLiveData = MutableLiveData<UserData>()
        if (liveData == null) {
            liveData = MutableLiveData()
            ioScope.launch {
                mutableLiveData.value = retrofitClientAuth.INSTANCE.getUserById(userId)

                uiScope.launch {
                    liveData = mutableLiveData
                }
            }
        }

        return liveData
    }
}