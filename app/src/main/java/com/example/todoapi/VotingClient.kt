package com.example.todoapi

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
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

class VotingClient : ComponentActivity() {
    private val client = OkHttpClient()
    lateinit var token: String
    lateinit var textVote:TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_voting_client)
        val radioGroup = findViewById<RadioGroup>(R.id.radioGroup)
        val button = findViewById<Button>(R.id.button)
        textVote = findViewById(R.id.tv_vote)
        val intent = intent
        val email = intent.getStringExtra("email").toString()
        val password = intent.getStringExtra("password").toString()
        token = getSharedPreferences("MyPrefs", MODE_PRIVATE).getString("token", "").toString()
        checjVote(email)



//        Toast.makeText(this,email+ password,Toast.LENGTH_SHORT).show()
        button.setOnClickListener {

            val selectedRadioButtonId = radioGroup.checkedRadioButtonId

            if (selectedRadioButtonId != -1) {

                val selectedRadioButton = findViewById<RadioButton>(selectedRadioButtonId)

                val selectedText = selectedRadioButton.text.toString()
//                Toast.makeText(this, "Selected: $selectedText", Toast.LENGTH_SHORT).show()

                sendVote(selectedText, email,password)
            } else {
                Toast.makeText(this, "Please select an item", Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun checjVote(mail:String) {
        var out = 0;
        val url = "http://192.168.137.214:3000/record"
        val jsonObject = JSONObject()
        jsonObject.put("email", mail)
        val jsonMediaType = "application/json; charset=utf-8".toMediaType()
        val requestBody = jsonObject.toString().toRequestBody(jsonMediaType)

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@VotingClient, "Login failed: ${e.message}", Toast.LENGTH_SHORT).show()
                    Log.e("LoginRequest", "Login failed", e)
//                        progresBar.visibility = View.GONE
                }
            }

            override fun onResponse(call: Call, response: Response) {
                Log.i("LoginRequest", "Response received: ${response.code}")
                val responseBody = response.body?.string()
                Log.i("LoginRequest", "Response body: $responseBody")
                runOnUiThread {
//                        progresBar.visibility = View.GONE
                    if (response.isSuccessful) {
                        val jsonResponse = JSONObject(responseBody.toString())

                        val message = jsonResponse.getString("party").toString()

                            textVote.text = "You Have Voted to $message"
//                            rad


                    }

                }
            }
        })


    }


//    override fun onDestroy() {
//        super.onDestroy()
//
//    }

    private fun sendVote(selectedItem: String,email:String,password:String) {

//         Toast.makeText(this,"SelectedItem "+ selectedItem+email+password,Toast.LENGTH_SHORT).show()




        val url = "http://192.168.137.214:3000/vote"
            val jsonObject = JSONObject()
            jsonObject.put("email", email)
            jsonObject.put("password", password)
            jsonObject.put("Party", selectedItem)
//            jsonObject.put("token", token)


            val jsonMediaType = "application/json; charset=utf-8".toMediaType()
            val requestBody = jsonObject.toString().toRequestBody(jsonMediaType)


            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()


            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    runOnUiThread {
                        Toast.makeText(this@VotingClient, "Login failed: ${e.message}", Toast.LENGTH_SHORT).show()
                        Log.e("LoginRequest", "Login failed", e)
//                        progresBar.visibility = View.GONE
                    }
                }

                override fun onResponse(call: Call, response: Response) {
                    Log.i("LoginRequest", "Response received: ${response.code}")
                    val responseBody = response.body?.string()
                    Log.i("LoginRequest", "Response body: $responseBody")
                    runOnUiThread {
//                        progresBar.visibility = View.GONE
                        if (response.isSuccessful) {


                            val jsonResponse = JSONObject(responseBody.toString())

                            val message = jsonResponse.getString("message")
                            Toast.makeText(this@VotingClient, message, Toast.LENGTH_SHORT).show()
                            val i = Intent(this@VotingClient, MainActivity::class.java)
                            startActivity(i)
                            finish()

                        }
                    }
                }
            })



    }
}