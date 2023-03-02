package com.github.polypoly.app

import androidx.lifecycle.LiveData
import androidx.room.*
import dagger.Component
import dagger.Module
import dagger.Provides
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

/**
 * The class that holds response data from the Bored API requests
 */
data class BoredActivity (
    val activity: String,
    val type: String,
    val participants: Int,
    val price: Double,
    val link: String,
    val key: String,
    val accessibility: Double
)

/**
 * The interface to the Bored API
 */
interface BoredApiService {
    @GET("activity")
    fun getActivity(): Call<BoredActivity>
}

/**
 * The activity entity of a bored activities database
 */
@Entity
data class Activity (
    @PrimaryKey(autoGenerate = true) val id: Int,
    val content: String?
)

/**
 * The data access object for activities of a bored activities database
 */
@Dao
interface ActivityDao {
    @Query("SELECT * FROM activity")
    fun getAll(): LiveData<List<Activity>>

    @Insert
    fun insertAll(vararg users: Activity)

    @Delete
    fun delete(user: Activity)
}

/**
 * The database that stores activities from the Bored API
 */
@Database(entities = [Activity::class], version = 1)
abstract class BoredActivityDatabase : RoomDatabase() {
    abstract fun activityDao(): ActivityDao
}

@Module
class BoredApiModule(private val baseUrl: String) {
    @Provides
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    fun provideApiService(retrofit: Retrofit): BoredApiService {
        return retrofit.create(BoredApiService::class.java)
    }
}

@Component(modules = [BoredApiModule::class])
interface BoredApiComponent {
    fun inject(activity: MainActivity)
}
