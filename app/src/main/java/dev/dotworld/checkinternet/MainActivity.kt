package dev.dotworld.checkinternet


import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

private val callBackAction: String = "call_back_ui"
class MainActivity : AppCompatActivity() {
    private var TAG = MainActivity::class.java.simpleName
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d(TAG, "onCreate: ")
        val intentFilter = IntentFilter()
        intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION)
        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION)
        intentFilter.addAction(WifiManager.EXTRA_WIFI_STATE)
        intentFilter.addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED)
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION)
        intentFilter.addAction(callBackAction)
        registerReceiver(NetworkBrodcastReceiver(), intentFilter)

        val periodicWorkRequest:PeriodicWorkRequest =PeriodicWorkRequest.Builder(NetworkWorkManager::class.java,15,TimeUnit.MINUTES)
            .build()
        WorkManager.getInstance(this).enqueue(periodicWorkRequest)

    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(NetworkBrodcastReceiver())
    }
}

