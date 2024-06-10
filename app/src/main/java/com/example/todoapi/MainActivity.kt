package com.example.todoapi

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.ComponentActivity
import okhttp3.Call
import okhttp3.Callback
import okhttp3.FormBody
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

class MainActivity : ComponentActivity() {
    private val client = OkHttpClient()
    lateinit var progresBar:ProgressBar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val emailEditText = findViewById<EditText>(R.id.emailEditText)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)
        val loginButton = findViewById<Button>(R.id.loginButton)
        val registerButton = findViewById<Button>(R.id.registerButton)
        progresBar = findViewById<ProgressBar>(R.id.progressBar)
        progresBar.visibility = View.GONE
        loginButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            progresBar.visibility = View.VISIBLE
            sendlogin(email, password)
        }

        registerButton.setOnClickListener {
            val i = Intent(
                this@MainActivity,
                Registration::class.java
            )
            startActivity(i)
            Toast.makeText(this, "Register button clicked", Toast.LENGTH_SHORT).show()
        }


    }

    private fun sendlogin(username: String, password: String) {
        val url = "http://192.168.0.108:3000/login" // Replace with your server address
        val jsonObject = JSONObject()
        jsonObject.put("username", username)
        jsonObject.put("password", password)

        // Create JSON request body
        val jsonMediaType = "application/json; charset=utf-8".toMediaType()
        val requestBody = jsonObject.toString().toRequestBody(jsonMediaType)

        // Build request
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()


        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@MainActivity, "Login failed: ${e.message}", Toast.LENGTH_SHORT).show()
                    Log.e("LoginRequest", "Login failed", e)
                    progresBar.visibility = View.GONE
                }
            }

            override fun onResponse(call: Call, response: Response) {
                Log.i("LoginRequest", "Response received: ${response.code}")
                val responseBody = response.body?.string()
                Log.i("LoginRequest", "Response body: $responseBody")
                runOnUiThread {
                    progresBar.visibility = View.GONE
                    if (response.isSuccessful) {
                        val jsonResponse = JSONObject(responseBody.toString())
                        val message = jsonResponse.getString("message")


                        Toast.makeText(this@MainActivity, "Message: $message",  Toast.LENGTH_SHORT).show()
                        val s = Intent(this@MainActivity,VotingClient::class.java)
                        s.putExtra("email",username)
                        s.putExtra("password",password)
                        startActivity(s)

                    } else {
                        Toast.makeText(this@MainActivity, "Login unsuccessful", Toast.LENGTH_SHORT).show()

                    }
                }
            }
        })
    }


}






