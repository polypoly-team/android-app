package com.github.polypoly.app

import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
//import com.github.polypoly.app.settings.SharedInstances.Companion.remoteDB
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import org.hamcrest.Matchers
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.internal.runners.statements.Fail
import org.junit.runner.RunWith
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class FirebaseActivityTest
{
    private val email = "unit_test@gmail.com"
    private val phone = "0120120120"

    init {
        // 10.0.2.2 is the special IP address to connect to the 'localhost' of
        // the host computer from an Android emulator.
//        remoteDB.useEmulator("10.0.2.2", 4400)
    }

    @Test
    fun displaysCorrectEmailFromPhoneNumber() {
        val remoteDB = Firebase.database.reference

        val intent = Intent(ApplicationProvider.getApplicationContext(), FirebaseActivity::class.java)
        ActivityScenario.launch<FirebaseActivity>(intent)

        val ref = remoteDB.child("displaysCorrectEmailFromPhoneNumber")
        val textExpected = "unit_test@gmail.com";
        val preOpTask = ref.setValue(textExpected)

        val timeoutFuture = CompletableFuture<Boolean>()

        preOpTask.continueWith {
            Espresso.onView(ViewMatchers.withId(R.id.editTextPhone))
                .perform(ViewActions.replaceText("0120120120"))
            Espresso.onView(ViewMatchers.withId(R.id.buttonDBGet))
                .perform(ViewActions.click())

            Fail(java.lang.RuntimeException("Debug"))

            Thread.sleep(50) // TODO Bad practice but I see no other way to wait for both DB response and UI update

            Espresso.onView(ViewMatchers.withId(R.id.editTextTextEmailAddress))
                .check(ViewAssertions.matches(ViewMatchers.withText(textExpected)))

            timeoutFuture.complete(true)
        }
        preOpTask.addOnFailureListener(timeoutFuture::completeExceptionally)

        timeoutFuture.orTimeout(5, TimeUnit.SECONDS)
            .join()
    }

    @Test
    fun updatesCorrectlyPhoneEmailPair() {
//        val intent = Intent(ApplicationProvider.getApplicationContext(), FirebaseActivity::class.java)
//        ActivityScenario.launch<FirebaseActivity>(intent)
//
//        val ref = remoteDB.getReference("displaysCorrectEmailFromPhoneNumber")
//
//        val preOpTask = ref.setValue("wrond_email@hotmail.fr")
//        preOpTask.addOnSuccessListener {// Real test starts now
//            Espresso.onView(ViewMatchers.withId(R.id.editTextPhone))
//                .perform(ViewActions.replaceText(phone))
//            Espresso.onView(ViewMatchers.withId(R.id.editTextTextEmailAddress))
//                .perform(ViewActions.replaceText(email))
//            Espresso.onView(ViewMatchers.withId(R.id.buttonDBSet))
//                .perform(ViewActions.click())
//
//            Thread.sleep(50) // TODO Bad practice but I see no other way to wait for both UI update and DB response
//
//            assertEquals(ref.get().toString(), email)
//        }
//        preOpTask.addOnFailureListener {
//            Fail(it)
//        }
    }
}