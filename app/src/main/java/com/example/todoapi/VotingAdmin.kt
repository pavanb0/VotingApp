package com.example.todoapi

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
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

class VotingAdmin : ComponentActivity() {
    private val client = OkHttpClient()
    lateinit var tv1: TextView
    lateinit var tv2: TextView
    lateinit var tv3: TextView
    lateinit var tv4: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_voting_admin)
        tv1 = findViewById(R.id.textView)
        tv2 = findViewById(R.id.textView2)
        tv3 = findViewById(R.id.textView3)
        tv4 = findViewById(R.id.textView4)

        val email:String = intent.getStringExtra("email").toString()
        val password:String = intent.getStringExtra("password").toString()
        fetchVoteCount(email,password)



    }

    private fun fetchVoteCount(email: String, password: String) {


        val url = "http://192.168.137.214:3000/viewvotes"
        val jsonObject = JSONObject()
        jsonObject.put("email", email)
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
                    Toast.makeText(this@VotingAdmin, "Login failed: ${e.message}", Toast.LENGTH_SHORT).show()
                    Log.e("LoginRequest", "Login failed", e)

                }
            }

            override fun onResponse(call: Call, response: Response) {
                Log.i("LoginRequest", "Response received: ${response.code}")
                val responseBody = response.body?.string()
                Log.i("LoginRequest", "Response body: $responseBody")
                runOnUiThread {
                    if (response.isSuccessful) {
                        val jsonResponse = JSONObject(responseBody.toString())

                        val part1: String = jsonResponse.getString("part1")
                        val part2: String = jsonResponse.getString("part2")
                        val part3: String = jsonResponse.getString("part3")
                        val part4: String = jsonResponse.getString("part4")

                        tv1.text = "APPDEV Vote Count: $part1"
                        tv2.text = "KOOEF Vote Count: $part2"
                        tv3.text = "fiifj Vote Count: $part3"
                        tv4.text = "fjeifj Vote Count: $part4"

                    }
                }
            }
        })


    }


}