package com.example.todoapi

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
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
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

class Registration: ComponentActivity() {
    lateinit var progresBar: ProgressBar;
    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        val usernameEditText = findViewById<EditText>(R.id.usernameEditText)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)
        val emailEditText = findViewById<EditText>(R.id.emailEditText)
        val phoneEditText = findViewById<EditText>(R.id.phoneEditText)
        val loginButton = findViewById<Button>(R.id.loginButton)
        val registerButton = findViewById<Button>(R.id.registerButton)
//        val progressBar = findViewById<ProgressBar>(R.id.progressBar3)
//        progressBar.visibility = View.GONE
        loginButton.setOnClickListener {
            finish()
        }

        registerButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()
            val email = emailEditText.text.toString()
            val phone = phoneEditText.text.toString()
            handleRegister(username, password, email, phone)
        }

    }

    private fun handleRegister(username: String, password: String, email: String, phone: String) {
        Toast.makeText(this, "Registered with\nUsername: $username\nEmail: $email\nPhone: $phone", Toast.LENGTH_SHORT).show()

            val url = "http://192.168.137.214:3000/register"
            val jsonObject = JSONObject()

            jsonObject.put("name", username)
            jsonObject.put("email", email)
            jsonObject.put("password", password)
            jsonObject.put("phone", phone)



            val jsonMediaType = "application/json; charset=utf-8".toMediaType()
            val requestBody = jsonObject.toString().toRequestBody(jsonMediaType)


            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()


            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    runOnUiThread {
                        Toast.makeText(this@Registration, "Login failed: ${e.message}", Toast.LENGTH_SHORT).show()
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

                            Toast.makeText(this@Registration, message + " registerd successfully" , Toast.LENGTH_SHORT).show()
                            val e = Intent(this@Registration, MainActivity::class.java)
                            startActivity(e)

                        }
                    }
                }
            })
    }
}