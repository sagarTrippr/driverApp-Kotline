package com.example.driverapp

import android.Manifest
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

class Home : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
            ),
            0
        )

//        val startButton = findViewById<Button>(R.id.start_button)
//        val  stopButton = findViewById<Button>(R.id.stop_button)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

//        startButton.setOnClickListener {
            Intent(applicationContext,LocationService::class.java).apply {
                action= LocationService.ACTION_START
                startService(this)
            }
            Toast.makeText(this, "Location Tracking on", Toast.LENGTH_SHORT).show()
//        }
//        stopButton.setOnClickListener {
//            Intent(applicationContext,LocationService::class.java).apply {
//                action= LocationService.ACTION_STOP
//                startService(this)
//
//            }
//            Toast.makeText(this, "Tracking Disabled", Toast.LENGTH_LONG).show()
//            notificationManager.cancelAll()
//
//        }
    }
}
