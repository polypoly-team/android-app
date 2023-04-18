import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.polypoly.app.WelcomeActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*

@RunWith(AndroidJUnit4::class)
class WelcomeActivityAuthTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<WelcomeActivity>()

    @Test
    fun signInButtonClicked_startsSignInFlow() {
        val auth = mock(FirebaseAuth::class.java)
        val currentUser = mock(FirebaseUser::class.java)
        when(auth.currentUser).thenReturn(currentUser)

        composeTestRule.setContent {
            WelcomeActivity(
                auth = auth
            )
        }
    }
}