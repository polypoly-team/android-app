package com.github.polypoly.app

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.concurrent.CompletableFuture

class FirebaseActivity : AppCompatActivity() {

    private val db: FirebaseDatabase = FirebaseDatabase.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_firebase)
    }

    operator fun get(view: View?) {
        val email = findViewById<TextView>(R.id.editTextTextEmailAddress)
        val phone = findViewById<TextView>(R.id.editTextPhone)
        val future = CompletableFuture<String>()
        db.getReference(phone.text.toString()).get().addOnSuccessListener { it ->
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
        val myRef: DatabaseReference = db.getReference(phone.text.toString())
        myRef.setValue(email.text.toString())
    }
}