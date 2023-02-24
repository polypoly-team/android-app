package com.github.polypoly.app

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class GreetingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_greeting)
        val name: String? = intent.getStringExtra("name")
        val greetingMessage: TextView = findViewById(R.id.greetingMessage)
        greetingMessage.text = getString(R.string.greeting_message, name)
    }
}