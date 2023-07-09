package ru.gb.veber.newsapi.common

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import ru.gb.veber.ui_common.coroutine.SingleSharedFlow

class ConnectivityListener(context: Context) {

    private val checkConnectionFlow = SingleSharedFlow<Boolean>()

    init {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val request = NetworkRequest.Builder().build()

        connectivityManager.registerNetworkCallback(request, object :
            ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                checkConnectionFlow.tryEmit(true)
            }

            override fun onUnavailable() {
                checkConnectionFlow.tryEmit(false)
            }

            override fun onLost(network: Network) {
                checkConnectionFlow.tryEmit(false)
            }
        })
    }

    fun status(): SharedFlow<Boolean> = checkConnectionFlow.asSharedFlow()
}



