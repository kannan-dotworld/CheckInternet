package dev.dotworld.checkinternet

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.*
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity

class NetworkBrodcastReceiver:BroadcastReceiver() {
    private  var TAG =NetworkBrodcastReceiver::class.java.simpleName
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action?.equals(ConnectivityManager.CONNECTIVITY_ACTION) == true || intent?.action?.equals(
                ConnectivityManager.EXTRA_NO_CONNECTIVITY) == true ){
            Log.d(TAG, "CONNECTIVITY_ACTION mobile data: ")
        }
        if (isNetworkAvailable(context) ==1 || (isNetworkAvailable(context) ==2 && isOnline()==true)){
            Log.d(TAG, "onReceive: device ONLINE")
            isonlineFlag=true
        }else {
            Log.d(TAG, "onReceive: device OFFLINE")
            isonlineFlag=false
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
      /* 1== TRANSPORT_CELLULAR
      2== WIFI/ETHERNET
      0== fail */
    fun isNetworkAvailable(context: Context?): Int {
        val connectivityManager = context?.getSystemService(AppCompatActivity.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Log.d(TAG, "Build.VERSION.SDK_INT: ")
            val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    Log.d(TAG, "TRANSPORT_CELLULAR: ")

                    return 1
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)  ) {
                    Log.d(TAG, "TRANSPORT_WIFI: ")
                    return 2
                }
                else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET ) ) {
                    Log.d(TAG, "TRANSPORT_ETHERNET: ")
                    return 2
                }

            }
        } else {
            try {
                val activeNetworkInfo = connectivityManager.activeNetworkInfo
                if (activeNetworkInfo != null && activeNetworkInfo.state== NetworkInfo.State.CONNECTED) {
                    return if (activeNetworkInfo?.typeName == "MOBILE")  1
                    else{2 }

                }
            } catch (e: Exception) {
                Log.e(TAG, " ${e.message}")
            }
        }
        return 0
    }

    fun isOnline(): Boolean? {
        try {
            val p1 = Runtime.getRuntime().exec("/system/bin/ping -c 1 www.google.com")
            val returnVal = p1.waitFor()
            return returnVal == 0
        } catch (e: java.lang.Exception) {
            Log.e(TAG, "isOnline: error -> ${e.message} ")
        }
        return false
    }
    companion object{
        var isonlineFlag:Boolean=false
    }
}