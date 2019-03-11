package dominando.android.hotel.auth

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.api.ApiException
import com.google.firebase.messaging.FirebaseMessaging
import dominando.android.hotel.R
import dominando.android.hotel.common.HotelActivity
import kotlinx.android.synthetic.main.activity_login.*
import org.koin.android.ext.android.inject

class LoginActivity : AppCompatActivity() {
    private val authManager: AuthManager by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        btnSignIn.setOnClickListener { signIn() }
        checkGooglePlayServices()
    }
    private fun signIn() {
        val signInIntent = authManager.getSignInIntent()
        startActivityForResult(signInIntent, REQUEST_SIGN_IN)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_SIGN_IN) {
            if (resultCode == Activity.RESULT_OK) {
                handleSignInResult(data)
            }
        } else if (requestCode == REQUEST_PLAY_SERVICES) {
            checkGooglePlayServices()
        }
    }
    private fun handleSignInResult(intent: Intent?) {
        try {
            GoogleSignIn.getSignedInAccountFromIntent(intent).getResult(ApiException::class.java)
            startActivity(Intent(this, HotelActivity::class.java))
            finish()
            FirebaseMessaging.getInstance().subscribeToTopic("hotel_sync")
        } catch (e: ApiException) {
            Toast.makeText(this, R.string.error_login_failed, Toast.LENGTH_SHORT).show()
        }
    }
    private fun checkGooglePlayServices() {
        val api = GoogleApiAvailability.getInstance()
        val resultCode = api.isGooglePlayServicesAvailable(this)
        if (resultCode != ConnectionResult.SUCCESS) {
            if (api.isUserResolvableError(resultCode)) {
                val dialog = api.getErrorDialog(this, resultCode, REQUEST_PLAY_SERVICES)
                dialog.setOnCancelListener {
                    finish()
                }
                dialog.show()
            } else {
                Toast.makeText(this,
                    R.string.error_play_services_not_supported,
                    Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
    companion object {
        private const val REQUEST_SIGN_IN = 1000
        private const val REQUEST_PLAY_SERVICES = 2000
    }
}

