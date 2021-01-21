package com.rn1.gogoyo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.rn1.gogoyo.databinding.ActivityLoginBinding
import com.rn1.gogoyo.util.LoadStatus
import com.rn1.gogoyo.util.Logger
import com.rn1.gogoyo.util.RC_SIGN_IN
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : AppCompatActivity() {

    private val TAG = this.javaClass.name
    private lateinit var status : LoadStatus
    private lateinit var binding: ActivityLoginBinding
    // FirebaseAuth
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)

        // Initialize Firebase Auth
        auth = Firebase.auth

        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        // Build a GoogleSignInClient with the options specified by gso.
        val mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


        binding.signInButton.setOnClickListener{
            signIn(mGoogleSignInClient)
        }

    }


    private fun signIn(mGoogleSignInClient: GoogleSignInClient) {
        val signInIntent: Intent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        binding.signInButton.isClickable = false
        binding.loginProgressBar.visibility = View.VISIBLE

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Logger.d("firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)

            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
                binding.loginProgressBar.visibility = View.GONE
                binding.signInButton.isClickable = true
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {

                // Sign in success, intent to main activity with the signed-in user's information
                Logger.d( "signInWithCredential:success")
                val user = auth.currentUser
                Logger.d("user = $user")
                if (user != null) {
                    UserManager.userUID = user.uid
                    UserManager.userName = user.displayName
                    UserManager.userPhoto = user.photoUrl!!.toString()
                }
                startActivity(Intent(this, MainActivity::class.java))
                finish()

            } else {
                // If sign in fails, display a message to the user.
                Snackbar.make(container, "Authentication Failed.", Snackbar.LENGTH_SHORT).show()
                Log.w(TAG,"signInWithCredential:failure", task.exception)

                binding.loginProgressBar.visibility = View.GONE
                binding.signInButton.isClickable = true
            }
        }
    }
}