package dev.dotworld.checkinternet


import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import org.apache.commons.net.time.TimeTCPClient
import java.io.IOException
import java.util.concurrent.TimeUnit

private val callBackAction: String = "call_back_ui"

interface AsyncResponse {
    fun processFinish(output: String?)
}
class MainActivity : AppCompatActivity() {
    companion object {
        var timeText: TextView? = null
        var TAG = MainActivity::class.java.simpleName

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d(TAG, "onCreate: ")
        timeText?.findViewById<TextView>(R.id.time)
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

        getTime()

    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(NetworkBrodcastReceiver())
    }


    fun getTime() {
        Log.d(TAG, "getTime: ")
        val mainHandler = Handler(Looper.getMainLooper())

        mainHandler.post(object : Runnable {
            override fun run() {
                Log.d(TAG, "run: ")
                MyTask().execute()
                mainHandler.postDelayed(this, 10000)
            }
        })

    }

    private class MyTask() : AsyncTask<Void?, Void?, String?>() {

        private var TAG = MyTask::class.java.simpleName
        var timeDateText: String = "";
        override fun doInBackground(vararg params: Void?): String? {
            try {
                val client = TimeTCPClient()
                try {
                    client.defaultTimeout = 60000
                    // Other time servers can be found at : http://tf.nist.gov/tf-cgi/servers.cgi#
                    // Make sure that your program NEVER queries a server more frequently than once every 4 seconds
                    client.connect("time.nist.gov")
                   //client.connect("time.google.com")
                    Log.d(TAG, "doInBackground: ${client.date.toString()}")
                    timeText?.setText(client.date.toString())

                } finally {
                    client.disconnect()
                }
            } catch (e: IOException) {
                Log.e(TAG, "doInBackground: $e")
                e.printStackTrace()
            }

            return timeDateText
        }

    }


}

