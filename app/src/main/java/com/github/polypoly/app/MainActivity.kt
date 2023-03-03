package com.github.polypoly.app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toGreet: Button = findViewById(R.id.button)
        toGreet.setOnClickListener {
            val intent = Intent(this, GreetingActivity::class.java)
            val nameField: EditText = findViewById(R.id.mainName)
            intent.putExtra("name", nameField.text.toString())
            startActivity(intent)
        }

        val toMap: Button = findViewById(R.id.mapButton)
        toMap.setOnClickListener {
            val intent = Intent(this, MapsActivity::class.java)
            startActivity(intent)
        }
    }
}