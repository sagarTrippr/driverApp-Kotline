package com.example.driverapp

import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.IBinder
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

import org.json.JSONObject

class LocationService: Service() {
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private lateinit var locationClient: LocationClient

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }



    override fun onCreate() {
        super.onCreate()
        locationClient= DefaultLocationClient(
            applicationContext,
            LocationServices.getFusedLocationProviderClient(applicationContext)
        )
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when(intent?.action){
            ACTION_START -> start()
            ACTION_STOP -> stop()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun start(){
        val notification = NotificationCompat.Builder(this,"location")
            .setContentTitle("Tracking location...")
            .setContentText("Location: null")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setOngoing(true)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        locationClient
            .getLocationUpdates(10000L)
            .catch { e -> e.printStackTrace() }
            .onEach { location ->

                val lat = location.latitude.toString()
                val long = location.longitude.toString()
                updateUser(lat,long)
                println(lat)
                println(long)
                val updateNotification = notification.setContentText(
                    "Location : ($lat, $long)"
                )
                notificationManager.notify(1, updateNotification.build())
            }
            .launchIn(serviceScope)
        startForeground(1,notification.build())
    }

    private fun stop(){
        stopForeground(STOP_FOREGROUND_DETACH)
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()

        serviceScope.cancel()
    }
    companion object{
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"

    }

    private fun updateUser(lat: String, long: String) {
        val sharedPrefs = getSharedPreferences("MY_APP", Context.MODE_PRIVATE)
        val user = sharedPrefs.getString("MY_STRING", "")

        println(user)

        val url = "http://139.59.8.85:4444/update"

        val queue = Volley.newRequestQueue(this)

        val requestBody = JSONObject()
        requestBody.put("email", user)
        requestBody.put("lat", lat)
        requestBody.put("long", long)

        val request = JsonObjectRequest(
            Request.Method.PUT, url, requestBody,
            { response ->
                if(response["status"] == true) {
                    println(response)
                }
            },
            { error ->
                // Login failed, display error message
                Toast.makeText(this, "Tracking failed try to connect....: ", Toast.LENGTH_LONG).show()
            })

        queue.add(request)
    }
}