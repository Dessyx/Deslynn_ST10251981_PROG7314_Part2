package com.example.deflate

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FacebookAuthProvider
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import android.net.Uri
import android.webkit.WebView
import android.webkit.WebViewClient
import android.app.AlertDialog

class SignInActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "SignInActivity"
        private const val RC_SIGN_IN = 9001
        private const val GITHUB_CLIENT_ID = "Ov23liwG3uaDjiDZJnR4"
        private const val GITHUB_REDIRECT_URI = "http://localhost:8080/github-callback"
    }

    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var signinButton: MaterialButton
    private lateinit var signupLink: TextView
    private lateinit var googleSignInButton: MaterialButton
    private lateinit var githubSignInButton: MaterialButton
    private lateinit var facebookSignInButton: MaterialButton
    private lateinit var btnBack: ImageView

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var callbackManager: CallbackManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_signin)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.signin_layout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Configure Google Sign-In
        configureGoogleSignIn()
        
        // Initialize Facebook
        initializeFacebook()

        // Initialize views
        usernameEditText = findViewById(R.id.username_edittext)
        passwordEditText = findViewById(R.id.password_edittext)
        signinButton = findViewById(R.id.login_button)
        googleSignInButton = findViewById(R.id.google_signin_button)
        githubSignInButton = findViewById(R.id.github_signin_button)
        facebookSignInButton = findViewById(R.id.facebook_signin_button)
        btnBack = findViewById(R.id.btnBack)

        // Click listeners
        signinButton.setOnClickListener { handleSignIn() }
        googleSignInButton.setOnClickListener { signInWithGoogle() }
        githubSignInButton.setOnClickListener { signInWithGitHub() }
        facebookSignInButton.setOnClickListener { signInWithFacebook() }
        btnBack.setOnClickListener { navigateToSignUp() }
    }

    private fun handleSignIn() {
        val username = usernameEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()

        if (!validateInputs(username, password)) return

        signinButton.isEnabled = false
        signinButton.text = "Signing in..."

        val email = "$username@deflate.com"
        Log.d(TAG, "Attempting login with email: $email")

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "Sign in successful")
                    handleSignInSuccess()
                } else {
                    val exception = task.exception
                    Log.e(TAG, "Sign in failed", exception)

                    val errorMessage = when (exception) {
                        is FirebaseAuthException -> {
                            when (exception.errorCode) {
                                "ERROR_INVALID_EMAIL" -> "Invalid username"
                                "ERROR_WRONG_PASSWORD" -> "Incorrect password"
                                "ERROR_USER_NOT_FOUND" -> "No account found with this username"
                                "ERROR_NETWORK_REQUEST_FAILED" -> "Network error. Check your internet"
                                else -> "Sign in failed: ${exception.message}"
                            }
                        }
                        else -> "Sign in failed: ${exception?.message ?: "Unknown error"}"
                    }

                    handleSignInError(errorMessage)
                }
            }
    }

    private fun validateInputs(username: String, password: String): Boolean {
        var isValid = true

        if (TextUtils.isEmpty(username)) {
            usernameEditText.error = "Username is required"
            usernameEditText.requestFocus()
            isValid = false
        }

        if (TextUtils.isEmpty(password)) {
            passwordEditText.error = "Password is required"
            if (isValid) passwordEditText.requestFocus()
            isValid = false
        }

        return isValid
    }

    private fun handleSignInSuccess() {
        Toast.makeText(this, "Sign in successful!", Toast.LENGTH_LONG).show()
        signinButton.isEnabled = true
        signinButton.text = "Sign In"
        navigateToHome()
    }

    private fun handleSignInError(errorMessage: String) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
        signinButton.isEnabled = true
        signinButton.text = "Sign In"
    }

    private fun navigateToSignUp() {
        val intent = Intent(this, SignUpActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun navigateToHome() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun configureGoogleSignIn() {
        // Configure Google Sign-In
        val webClientId = getString(R.string.default_web_client_id)
        Log.d(TAG, "Using web client ID: $webClientId")
        
        // Verify the client ID format
        if (webClientId.contains("googleusercontent.com")) {
            Log.d(TAG, "Web client ID format looks correct")
        } else {
            Log.e(TAG, "Web client ID format may be incorrect: $webClientId")
        }
        
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(webClientId)
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
        Log.d(TAG, "Google Sign-In client configured successfully")
        
        // Check if Google Play Services is available
        try {
            val resultCode = com.google.android.gms.common.GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this)
            if (resultCode == com.google.android.gms.common.ConnectionResult.SUCCESS) {
                Log.d(TAG, "Google Play Services is available")
            } else {
                Log.e(TAG, "Google Play Services not available. Result code: $resultCode")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error checking Google Play Services", e)
        }
    }

    private fun signInWithGoogle() {
        try {
            Log.d(TAG, "Starting Google Sign-In process")
            val signInIntent = googleSignInClient.signInIntent
            Log.d(TAG, "Google Sign-In intent created successfully")
            startActivityForResult(signInIntent, RC_SIGN_IN)
        } catch (e: Exception) {
            Log.e(TAG, "Error starting Google Sign-In", e)
            Toast.makeText(this, "Error starting Google Sign-In: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        
        Log.d(TAG, "onActivityResult called with requestCode: $requestCode, resultCode: $resultCode")

        // Handle Facebook callback
        callbackManager.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            Log.d(TAG, "Processing Google Sign-In result")
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign-In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                Log.d(TAG, "Google Sign-In successful for user: ${account.email}")
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign-In failed
                Log.w(TAG, "Google sign in failed with status code: ${e.statusCode}", e)
                Log.w(TAG, "Error message: ${e.message}")
                Log.w(TAG, "Error details: ${e.toString()}")
                val errorMessage = when (e.statusCode) {
                    7 -> "Network error. Please check your internet connection."
                    8 -> "Internal error. Please try again later."
                    10 -> "Developer error. OAuth consent screen may not be properly configured."
                    12501 -> "Sign-in was cancelled."
                    else -> "Google Sign-In failed: ${e.message} (Status: ${e.statusCode})"
                }
                Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser
                    handleGoogleSignInSuccess(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    val errorMessage = when {
                        task.exception?.message?.contains("network") == true -> "Network error. Please check your internet connection."
                        task.exception?.message?.contains("invalid") == true -> "Invalid credentials. Please try again."
                        else -> "Authentication failed. Please try again."
                    }
                    Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun handleGoogleSignInSuccess(user: com.google.firebase.auth.FirebaseUser?) {
        user?.let {
            val displayName = it.displayName ?: "User"
            Toast.makeText(this, "Welcome $displayName!", Toast.LENGTH_LONG).show()
            navigateToHome()
        }
    }

    // Facebook Sign-In methods
    private fun initializeFacebook() {
        callbackManager = CallbackManager.Factory.create()
        
        LoginManager.getInstance().registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                Log.d(TAG, "Facebook login successful")
                handleFacebookAccessToken(loginResult.accessToken.token)
            }

            override fun onCancel() {
                Log.d(TAG, "Facebook login cancelled")
                Toast.makeText(this@SignInActivity, "Facebook login cancelled", Toast.LENGTH_SHORT).show()
            }

            override fun onError(exception: FacebookException) {
                Log.e(TAG, "Facebook login failed", exception)
                val errorMessage = when {
                    exception.message?.contains("network") == true -> "Network error. Please check your internet connection."
                    exception.message?.contains("cancelled") == true -> "Facebook login was cancelled."
                    else -> "Facebook login failed. Please try again."
                }
                Toast.makeText(this@SignInActivity, errorMessage, Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun signInWithFacebook() {
        LoginManager.getInstance().logInWithReadPermissions(this, listOf("email", "public_profile"))
    }

    private fun handleFacebookAccessToken(token: String) {
        val credential = FacebookAuthProvider.getCredential(token)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "Facebook authentication successful")
                    val user = auth.currentUser
                    handleFacebookSignInSuccess(user)
                } else {
                    Log.e(TAG, "Facebook authentication failed", task.exception)
                    val errorMessage = when {
                        task.exception?.message?.contains("network") == true -> "Network error. Please check your internet connection."
                        task.exception?.message?.contains("invalid") == true -> "Invalid Facebook credentials. Please try again."
                        else -> "Facebook authentication failed. Please try again."
                    }
                    Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun handleFacebookSignInSuccess(user: com.google.firebase.auth.FirebaseUser?) {
        user?.let {
            val displayName = it.displayName ?: "User"
            Toast.makeText(this, "Welcome $displayName!", Toast.LENGTH_LONG).show()
            navigateToHome()
        }
    }

    // GitHub Sign-In methods
    private fun signInWithGitHub() {
        if (GITHUB_CLIENT_ID == "YOUR_GITHUB_CLIENT_ID") {
            Toast.makeText(this, "GitHub Client ID not configured. Please add your GitHub Client ID to the code.", Toast.LENGTH_LONG).show()
            Log.e(TAG, "GitHub Client ID not configured")
            return
        }
        
        showGitHubLoginDialog()
    }

    private fun showGitHubLoginDialog() {
        val webView = WebView(this)
        webView.settings.javaScriptEnabled = true
        
        val githubAuthUrl = "https://github.com/login/oauth/authorize?client_id=$GITHUB_CLIENT_ID&redirect_uri=$GITHUB_REDIRECT_URI&scope=user:email"
        
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                if (url?.startsWith(GITHUB_REDIRECT_URI) == true) {
                    val uri = Uri.parse(url)
                    val code = uri.getQueryParameter("code")
                    if (code != null) {
                        handleGitHubCallback(code)
                        return true
                    }
                }
                return false
            }
        }
        
        webView.loadUrl(githubAuthUrl)
        
        val dialog = AlertDialog.Builder(this)
            .setTitle("Sign in with GitHub")
            .setView(webView)
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
                Toast.makeText(this, "GitHub login cancelled", Toast.LENGTH_SHORT).show()
            }
            .create()
        
        dialog.show()
    }

    private fun handleGitHubCallback(code: String) {
        Log.d(TAG, "GitHub authorization code received: $code")
        
        // For sign-in, we'll try to find existing GitHub users
        // In a real implementation, you'd exchange the code for an access token
        // and then check if the user exists in your system
        
        Toast.makeText(this, "GitHub Sign-In: Code received. Check logs for details.", Toast.LENGTH_LONG).show()
        Log.d(TAG, "GitHub sign-in code: $code")
        
        // For now, just show a message - in production you'd implement proper OAuth flow
        Toast.makeText(this, "GitHub Sign-In functionality needs backend implementation", Toast.LENGTH_LONG).show()
    }
}
