package com.example.todoapi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.ComponentActivity

class VotingClient : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_voting_client)
        val radioGroup = findViewById<RadioGroup>(R.id.radioGroup)
        val button = findViewById<Button>(R.id.button)
        val intent = intent
        val email = intent.getStringExtra("email").toString()
        val password = intent.getStringExtra("password").toString()
        Toast.makeText(this,email+ password,Toast.LENGTH_SHORT).show()
        button.setOnClickListener {
            // Get the ID of the selected radio button
            val selectedRadioButtonId = radioGroup.checkedRadioButtonId

            if (selectedRadioButtonId != -1) {
                // Find the selected radio button by its ID
                val selectedRadioButton = findViewById<RadioButton>(selectedRadioButtonId)
                // Get the text of the selected radio button
                val selectedText = selectedRadioButton.text.toString()
                // Display the selected item
//                Toast.makeText(this, "Selected: $selectedText", Toast.LENGTH_SHORT).show()

                // Run your custom function with the selected data
                sendVote(selectedText, email,password)
            } else {
                Toast.makeText(this, "Please select an item", Toast.LENGTH_SHORT).show()
            }

        }
    }

//    override fun onDestroy() {
//        super.onDestroy()
//
//    }

    private fun sendVote(selectedItem: String,email:String,password:String) {

         Toast.makeText(this,"SelectedItem "+ selectedItem+email+password,Toast.LENGTH_SHORT).show()
    }
}