package com.github.polypoly.app

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.location.Location
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.ContextCompat.checkSelfPermission
import com.github.polypoly.app.ui.theme.PolypolyTheme
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices.getFusedLocationProviderClient
import com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY

class MainActivity : ComponentActivity() {
    /**
     * The attributes of the class
     */
    private var nameText: String = ""
    private val permissions = arrayOf(ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PolypolyTheme {
                // This is the surface where all the view lies
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    NameForm()
                }
            }
        }
    }

    /**
     * A view where the user can write their name, if the name isn't empty, the button shows
     * a greeting activity
     */
    @Composable
    fun NameForm() {
        val mContext = LocalContext.current
        var warningText by remember { mutableStateOf("") }

        // This column contains the message displayed if the name is empty
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier.offset(y = 200.dp),
                text = warningText,
                style = MaterialTheme.typography.body2
            )
        }
        // Contains all the form, centered in the screen
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            NameTextField(15)
            Spacer(modifier = Modifier.height(10.dp))
            Button(
                shape = CircleShape,
                modifier = Modifier.testTag("greetButton"),
                // When clicking, another Activity is launched (only if the name isn't empty)
                onClick = {
                    if (nameText.isEmpty()) {
                        warningText = "You can't have an empty name!"
                    } else {
                        val greetIntent = Intent(mContext, GreetingActivity::class.java)
                        greetIntent.putExtra("name", nameText)
                        startActivity(greetIntent)
                    }
                }
            ) {
                Text(text = "Greet")
            }
            LocationLogic()
        }
    }

    @Composable
    fun LocationLogic() {
        val mContext = LocalContext.current
        val locationClient = remember { getFusedLocationProviderClient(mContext) }

        val locationRequest = remember {
            LocationRequest.Builder(PRIORITY_HIGH_ACCURACY, 100).build()
        }

        var lastLocation by remember { mutableStateOf(Location("")) }

        if (permissions.all { checkSelfPermission(this@MainActivity, it) != PERMISSION_GRANTED })
            requestPermissions(this@MainActivity, permissions, 1)

        var distanceWalked by remember { mutableStateOf(0f) }
        locationClient.requestLocationUpdates(locationRequest, object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                distanceWalked += lastLocation.distanceTo(locationResult.lastLocation!!)
                lastLocation = locationResult.lastLocation!!
            }
        }, mainLooper)

        Button(
            shape = CircleShape,
            modifier = Modifier.testTag("resetButton"),
            onClick = { distanceWalked = 0f }
        ) {
            Text(text = "Reset")
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(text = "Distance walked: ${formattedDistance(distanceWalked)}")
    }


    private fun formattedDistance(distance: Float): String {
        return if (distance < 1000) "${"%.1f".format(distance)}m"
        else "${"%.1f".format(distance / 1000)}km"
    }

    /**
     * This function returns the TextField where the user prompts their name.
     * @param maxLength (Int): The maximal allowed name
     */
    @Composable
    fun NameTextField(maxLength: Int) {
        var text by remember { mutableStateOf(TextFieldValue("")) }
        OutlinedTextField(
            modifier = Modifier
                .width(200.dp)
                .testTag("nameField"),
            value = text,
            label = { Text("Enter your name") },
            singleLine = true,
            onValueChange = { newText ->
                text = if (newText.text.length > maxLength) text else newText
                nameText = text.text
            })

    }

    // =================================== PREVIEW ==============
    @Preview(name = "Light Mode")
    @Preview(
        name = "Dark Mode",
        uiMode = UI_MODE_NIGHT_YES
    )
    @Composable
    fun NameFormPreview() {
        PolypolyTheme {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colors.background
            ) {
                NameForm()
            }
        }
    }
}
