package com.github.polypoly.app

import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.polypoly.app.settings.SharedInstances.Companion.DB
import com.github.polypoly.app.settings.SharedInstances.Companion.remoteDB
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class FirebaseActivityTest
{
    private val email = "unit_test@gmail.com"
    private val fake_email = "fake_unit_test@gmail.com"
    private val phone = "FirebaseActivityTest"

    init {
        // 10.0.2.2 is the special IP address to connect to the 'localhost' of
        // the host computer from an Android emulator.
        try {
            DB.useEmulator("10.0.2.2", 9000)
            remoteDB = DB.reference
        } catch (_: IllegalStateException) { }
        remoteDB.setValue(null)
    }

    @Test
    fun displaysCorrectEmailFromPhoneNumber() {
        val intent = Intent(ApplicationProvider.getApplicationContext(), FirebaseActivity::class.java)
        ActivityScenario.launch<FirebaseActivity>(intent)

        val ref = remoteDB.child(phone)

        val timeoutFuture = CompletableFuture<Boolean>()
        ref.setValue(email).addOnSuccessListener {
            timeoutFuture.complete(true)
        }.addOnFailureListener(timeoutFuture::completeExceptionally)
        timeoutFuture.get(5, TimeUnit.SECONDS)

        val getTimeoutFuture = CompletableFuture<Boolean>()
        FirebaseActivity.addOnGetCompleteCallback {
            getTimeoutFuture.complete(true)
            return@addOnGetCompleteCallback
        }

        Espresso.onView(ViewMatchers.withId(R.id.editTextPhone))
            .perform(ViewActions.replaceText(phone))
        Espresso.onView(ViewMatchers.withId(R.id.buttonDBGet))
            .perform(ViewActions.click())

        getTimeoutFuture.get(5, TimeUnit.SECONDS)

        Espresso.onView(ViewMatchers.withId(R.id.editTextTextEmailAddress))
            .check(ViewAssertions.matches(ViewMatchers.withText(email)))
    }

    @Test
    @RequiresApi(api = Build.VERSION_CODES.S)
    fun updatesCorrectlyPhoneEmailPair() {
        val intent = Intent(ApplicationProvider.getApplicationContext(), FirebaseActivity::class.java)
        ActivityScenario.launch<FirebaseActivity>(intent)

        val ref = remoteDB.child(phone)

        val timeoutFuture = CompletableFuture<Boolean>()
        ref.setValue(fake_email).addOnSuccessListener {
            timeoutFuture.complete(true)
        }.addOnFailureListener(timeoutFuture::completeExceptionally)
        timeoutFuture.get(5, TimeUnit.SECONDS)

        val setTimeoutFuture = CompletableFuture<Boolean>()
        FirebaseActivity.addOnSetCompleteCallback {
            setTimeoutFuture.complete(true)
            return@addOnSetCompleteCallback
        }

        Espresso.onView(ViewMatchers.withId(R.id.editTextPhone))
            .perform(ViewActions.replaceText(phone))
        Espresso.onView(ViewMatchers.withId(R.id.editTextTextEmailAddress))
            .perform(ViewActions.replaceText(email))
        Espresso.onView(ViewMatchers.withId(R.id.buttonDBSet))
            .perform(ViewActions.click())

        setTimeoutFuture.get(5, TimeUnit.SECONDS)

        val retrievedFuture = CompletableFuture<String>()
        ref.get().addOnSuccessListener {
            retrievedFuture.complete(it.value as String)
        }.addOnFailureListener(retrievedFuture::completeExceptionally)

        val result: String = retrievedFuture.get(5, TimeUnit.SECONDS)
        assertEquals(result, email)
    }
}