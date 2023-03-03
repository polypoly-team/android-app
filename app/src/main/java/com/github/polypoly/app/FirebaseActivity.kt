package com.github.polypoly.app

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
//import com.github.polypoly.app.settings.SharedInstances.Companion.remoteDB
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit
import com.github.polypoly.app.settings.SharedInstances.Companion.remoteDB

class FirebaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_firebase)
    }

    operator fun get(view: View?) {
        val email = findViewById<TextView>(R.id.editTextTextEmailAddress)
        val phone = findViewById<TextView>(R.id.editTextPhone)

        val future = CompletableFuture<String>()

        remoteDB.child(phone.text.toString()).get().addOnSuccessListener { it ->
            if (it.exists()) {
                future.complete(it.value as String)
            } else {
                future.completeExceptionally(RuntimeException(it.toString()))
            }
        }.addOnFailureListener { ex: Throwable? -> future.completeExceptionally(ex) }

        future.thenAccept { text: String? ->
            email.text = text
        }
    }

    fun set(view: View?) {
        val email = findViewById<TextView>(R.id.editTextTextEmailAddress)
        val phone = findViewById<TextView>(R.id.editTextPhone)

        val myRef: DatabaseReference = remoteDB.child(phone.text.toString())
        myRef.setValue(email.text.toString()).addOnSuccessListener {
            Log.d("successes", "Set successfully")
        }
    }
}