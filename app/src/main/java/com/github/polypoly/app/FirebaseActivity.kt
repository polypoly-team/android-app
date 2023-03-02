package com.github.polypoly.app

import android.os.Build
import android.os.Bundle
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

class FirebaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_firebase)
    }

    operator fun get(view: View?) {
        val email = findViewById<TextView>(R.id.editTextTextEmailAddress)
        val phone = findViewById<TextView>(R.id.editTextPhone)

        val future = CompletableFuture<String>()

        val db = Firebase.database.reference
        db.child(phone.text.toString()).get().addOnSuccessListener { it ->
            if (it.exists()) {
                future.complete(it.getValue() as String)
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

        val db = Firebase.database.reference

        val myRef: DatabaseReference = db.child(phone.text.toString())
        val task = myRef.setValue(email.text.toString())
        task.addOnSuccessListener {
            email.text = "Success"
        }
        task.addOnFailureListener {
            email.text = "Failure"
        }
    }
}