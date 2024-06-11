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
            if(email == "" || password == ""){
                Toast.makeText(this@MainActivity,"Enter Email and Password",Toast.LENGTH_SHORT).show()
            }else{
            progresBar.visibility = View.VISIBLE
            sendlogin(email, password)
            }
        }

        registerButton.setOnClickListener {
            val i = Intent(
                this@MainActivity,
                Registration::class.java
            )
            startActivity(i)
//            Toast.makeText(this, "Register button clicked", Toast.LENGTH_SHORT).show()
        }


    }

    private fun sendlogin(username: String, password: String) {
        val url = "http://192.168.137.214:3000/login"
        val jsonObject = JSONObject()
        jsonObject.put("email", username)
        jsonObject.put("password", password)


        val jsonMediaType = "application/json; charset=utf-8".toMediaType()
        val requestBody = jsonObject.toString().toRequestBody(jsonMediaType)


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

                        val admin = jsonResponse.getString("admin")
                        val token = jsonResponse.getString("token")
                        Toast.makeText(this@MainActivity, "Login successful $admin $token", Toast.LENGTH_SHORT).show()
                        val sh = getSharedPreferences("MyToken", MODE_PRIVATE)
                        val editor = sh.edit()
                        editor.putString("token", token)
                        editor.apply()
                        if (admin == "true") {
                            val intent = Intent(this@MainActivity, VotingAdmin::class.java)
                            intent.putExtra("email", username)
                            intent.putExtra("password", password)
                            startActivity(intent)
//                            Toast.makeText(this@MainActivity, "Message: Activity Start Faliure", Toast.LENGTH_SHORT).show()

                        } else {
                            val intent = Intent(this@MainActivity, VotingClient::class.java)
                            intent.putExtra("email", username)
                            intent.putExtra("password", password)
                            startActivity(intent)
                        }
                    } else {
                        Toast.makeText(this@MainActivity, "Login unsuccessful", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }


}






