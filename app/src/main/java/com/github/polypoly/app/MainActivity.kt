package com.github.polypoly.app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.google.firebase.database.FirebaseDatabase

//import com.github.polypoly.app.settings.SharedInstances.Companion.remoteDB

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val button: Button = findViewById(R.id.button)
        button.setOnClickListener {
            val intent = Intent(this, GreetingActivity::class.java)
            val nameField: EditText = findViewById(R.id.mainName)
            intent.putExtra("name", nameField.text.toString())
            startActivity(intent)
        }

        val buttonToDB: Button = findViewById(R.id.buttonToDB)
        buttonToDB.setOnClickListener {
            val intent = Intent(this, FirebaseActivity::class.java)
            startActivity(intent)
        }
    }
}