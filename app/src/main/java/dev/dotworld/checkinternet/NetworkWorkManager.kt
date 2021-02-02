package dev.dotworld.checkinternet

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
private val callBackAction: String = "call_back_ui"
class NetworkWorkManager(context: Context, parameters: WorkerParameters) :
    Worker(context, parameters) {
    private val TAG = NetworkWorkManager::class.java.simpleName
    override fun doWork(): Result {
        Log.d(TAG, "doWork: ")
        var isOnline = isOnline()
        Log.d(TAG, "run NetworkWorkManager: in Online=" + isOnline())
        var localIntent = Intent(callBackAction)
            if (isOnline != null) {
                applicationContext.sendBroadcast(localIntent)
            }
        return Result.success()

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
}