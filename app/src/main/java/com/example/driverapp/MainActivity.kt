package com.example.driverapp

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Location services disabled")
            builder.setMessage("Please turn on location services to use this feature.")
            builder.setPositiveButton("Go to settings") { _, _ ->
                // Open the device's settings to turn on the location
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
            builder.setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            builder.show()
        }

        val sharedPrefs = applicationContext.getSharedPreferences("myAppPrefs", Context.MODE_PRIVATE)

// Check if the stored credentials are valid
        val savedUsername = sharedPrefs.getString("username", null)
        val savedPassword = sharedPrefs.getString("password", null)

        if (savedUsername != null && savedPassword != null) {
            loginUser(savedUsername,savedPassword)
        }

        val emailEditText = findViewById<EditText>(R.id.email_edit_text)
        val passwordEditText = findViewById<EditText>(R.id.password_edit_text)
        val loginButton = findViewById<Button>(R.id.login_button)

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            val sharedPrefs = applicationContext.getSharedPreferences("myAppPrefs", Context.MODE_PRIVATE)

           // Store the user's credentials securely
            val editor = sharedPrefs.edit()
            editor.putString("username", email)
            editor.putString("password", password)
            editor.apply()

            loginUser(email, password)
        }

    }
    override fun onResume() {
        super.onResume()
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            // Prompt the user to turn on location services again
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Location services disabled")
            builder.setMessage("Please turn on location services to use this feature.")
            builder.setPositiveButton("Go to settings") { _, _ ->
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
            builder.setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            builder.show()
        }
    }

    private fun loginUser(email: String, password: String) {
        val url = "http://139.59.8.85:4444/login"

        val queue = Volley.newRequestQueue(this)

        val requestBody = JSONObject()
        requestBody.put("email", email)
        requestBody.put("password", password)

        val request = JsonObjectRequest(
            Request.Method.POST, url, requestBody,
            { response ->
                if(response["status"] == true) {

                    val sharedPrefs = getSharedPreferences("MY_APP", Context.MODE_PRIVATE)
                    sharedPrefs.edit().putString("MY_STRING", email).apply()


                    println(response)
                    // Successful login, redirect to home page
                    val intent = Intent(this, Home::class.java)
                    startActivity(intent)
                    finish()
                }else Toast.makeText(this, ""+response["message"], Toast.LENGTH_LONG).show()


            },
            { error ->
                println(error)
                // Login failed, display error message
                Toast.makeText(this, "Login failed Try again ", Toast.LENGTH_LONG).show()
            })

        queue.add(request)
    }

}