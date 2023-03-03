package com.github.polypoly.app

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.github.polypoly.app.settings.SharedInstances.Companion.remoteDB
import com.google.firebase.database.DatabaseReference
import java.util.concurrent.CompletableFuture

class FirebaseActivity : AppCompatActivity() {
    // TODO: It might be better to limit callback to an instance instead of a companion object, but
    //  it should be fine for now as two activity's instances should not be running at the same time
    companion object {
        private val onGetCompleteCallbacks: ArrayList<() -> Unit> = ArrayList();
        private val onSetCompleteCallbacks: ArrayList<() -> Unit> = ArrayList();

        fun addOnGetCompleteCallback(callback: () -> Unit) {
            onGetCompleteCallbacks.add(callback)
        }

        fun addOnSetCompleteCallback(callback: () -> Unit) {
            onSetCompleteCallbacks.add(callback)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_firebase)
    }

    fun get(view: View?) {
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
            for (callback in onGetCompleteCallbacks) {
                callback.invoke()
            }
            onGetCompleteCallbacks.clear()
        }
    }

    fun set(view: View?) {
        val email = findViewById<TextView>(R.id.editTextTextEmailAddress)
        val phone = findViewById<TextView>(R.id.editTextPhone)

        val myRef: DatabaseReference = remoteDB.child(phone.text.toString())
        myRef.setValue(email.text.toString()).addOnSuccessListener {
            for (callback in onSetCompleteCallbacks) {
                callback.invoke()
            }
            onSetCompleteCallbacks.clear()
        }
    }
}