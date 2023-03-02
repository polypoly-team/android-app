package com.github.polypoly.app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.room.Room
import dagger.internal.DaggerGenerated
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.random.Random
import kotlinx.coroutines.*
import javax.inject.Inject

class MainActivity : AppCompatActivity() {
    @Inject lateinit var boredApiService: BoredApiService
    @OptIn(DelicateCoroutinesApi::class)
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

        val component = DaggerBoredApiComponent.builder()
            .boredApiModule(BoredApiModule("https://www.boredapi.com/api/"))
            .build()
        component.inject(this)

        // Set up the database for activities caching
        val db = Room.databaseBuilder(
            applicationContext,
            BoredActivityDatabase::class.java, "bored-activity-database"
        ).build()

        val buttonBored: Button = findViewById(R.id.buttonBored)
        buttonBored.setOnClickListener {
            // On click, fetch an activity from the API
            val activity: TextView = findViewById(R.id.boredActivity)
            boredApiService.getActivity().enqueue(object : Callback<BoredActivity> {
                override fun onResponse(call: Call<BoredActivity>, response: Response<BoredActivity>) {
                    // Display activity
                    activity.text = response.body()?.activity

                    // Add activity to database in the background
                    GlobalScope.launch {
                        val activityDao = db.activityDao()
                        activityDao.insertAll(Activity(0, response.body()?.activity))
                    }
                }

                override fun onFailure(call: Call<BoredActivity>, t: Throwable) {
                    val activityDao = db.activityDao()
                    // Query all cached activities asynchronously
                    val getActivities: LiveData<List<Activity>> = activityDao.getAll()
                    getActivities.observe(this@MainActivity, Observer{ activities ->
                        // Display a random cached activity
                        activity.text = getString(R.string.offline_bored_activity, activities[Random.nextInt(activities.size)].content);
                    })
                }
            })
        }
    }
}