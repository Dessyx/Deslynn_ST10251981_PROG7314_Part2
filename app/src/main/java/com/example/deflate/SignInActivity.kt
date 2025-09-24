package com.example.deflate

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.firestore.FirebaseFirestore


class SignInActivity : AppCompatActivity() {


    companion object {
        private const val TAG = "SignInActivity"
        private const val RC_SIGN_IN = 9001
        private const val GITHUB_CLIENT_ID = "Ov23liwG3uaDjiDZJnR4"
        private const val GITHUB_REDIRECT_URI = "http://localhost:8080/github-callback"
    }


    //  UI Elements
    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var signinButton: MaterialButton
    private lateinit var signupLink: TextView
    private lateinit var googleSignInButton: MaterialButton
    private lateinit var githubSignInButton: MaterialButton
    private lateinit var facebookSignInButton: MaterialButton
    private lateinit var btnBack: ImageView


    //  Firebase / SDKs
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var callbackManager: CallbackManager


    //  Lifecycle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_signin)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.signin_layout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initiate Firebase
        auth = FirebaseAuth.getInstance()
        configureGoogleSignIn()
        initializeFacebook()


        initViews()
        setupClickListeners()
    }


    // Setup
    private fun initViews() {
        usernameEditText = findViewById(R.id.username_edittext)
        passwordEditText = findViewById(R.id.password_edittext)
        signinButton = findViewById(R.id.login_button)
        googleSignInButton = findViewById(R.id.google_signin_button)
        githubSignInButton = findViewById(R.id.github_signin_button)
        facebookSignInButton = findViewById(R.id.facebook_signin_button)
        btnBack = findViewById(R.id.btnBack)
    }

    private fun setupClickListeners() {
        signinButton.setOnClickListener { handleSignIn() }
        googleSignInButton.setOnClickListener { signInWithGoogle() }
        githubSignInButton.setOnClickListener { signInWithGitHub() }
        facebookSignInButton.setOnClickListener { signInWithFacebook() }
        btnBack.setOnClickListener { navigateToSignUp() }
    }


    //  Email/Password Authentication
    private fun handleSignIn() {
        val input = usernameEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()

        if (!validateInputs(input, password)) return

        signinButton.isEnabled = false
        signinButton.text = "Signing in..."

        if (input.contains("@")) {
            // User entered an email
            signInWithEmail(input, password)
        } else {
            // User entered a username, look up email in Firestore
            val db = FirebaseFirestore.getInstance()
            db.collection("users")
                .whereEqualTo("username", input)
                .get()
                .addOnSuccessListener { docs ->
                    if (!docs.isEmpty) {
                        val email = docs.documents[0].getString("email")
                        if (!email.isNullOrEmpty()) {
                            signInWithEmail(email, password)
                        } else {
                            showError("No email linked to this username")
                        }
                    } else {
                        showError("Username not found")
                    }
                }
                .addOnFailureListener { e ->
                    showError("Error: ${e.message}")
                }
        }
    }

    private fun signInWithEmail(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                signinButton.isEnabled = true
                signinButton.text = "Sign In"

                if (task.isSuccessful) {
                    handleSignInSuccess()
                } else {
                    val error = (task.exception as? FirebaseAuthException)?.errorCode ?: "UNKNOWN"
                    showError("Login failed: $error")
                }
            }
    }

    private fun showError(msg: String) {
        signinButton.isEnabled = true
        signinButton.text = "Sign In"
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
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


    //  Authenticate Result Handlers
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
        startActivity(Intent(this, SignUpActivity::class.java))
        finish()
    }

    private fun navigateToHome() {
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }


    //  Google Authentication
    private fun configureGoogleSignIn() {
        val webClientId = getString(R.string.default_web_client_id)
        Log.d(TAG, "Using web client ID: $webClientId")
        Log.d(TAG, "Package name: ${packageName}")

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(webClientId)
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
        Log.d(TAG, "Google Sign-In client configured successfully")
    }

    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account: GoogleSignInAccount = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Log.e(TAG, "Google Sign-In failed: ${e.statusCode} - ${e.message}")
                val errorMessage = when (e.statusCode) {
                    10 -> "DEVELOPER_ERROR: Check SHA-1 fingerprint and OAuth configuration"
                    7 -> "NETWORK_ERROR: Check internet connection"
                    12501 -> "USER_CANCELLED: Sign-in was cancelled"
                    else -> "Google Sign-In failed: ${e.statusCode} - ${e.message}"
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
                    val user = auth.currentUser
                    handleGoogleSignInSuccess(user)
                } else {
                    Toast.makeText(this, "Authentication failed. Please try again.", Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun handleGoogleSignInSuccess(user: FirebaseUser?) {
        user?.let {
            Toast.makeText(this, "Welcome ${it.displayName ?: "User"}!", Toast.LENGTH_LONG).show()
            navigateToHome()
        }
    }

    //  Facebook Authentication
    private fun initializeFacebook() {
        callbackManager = CallbackManager.Factory.create()
        LoginManager.getInstance().registerCallback(callbackManager,
            object : FacebookCallback<LoginResult> {
                override fun onSuccess(loginResult: LoginResult) {
                    handleFacebookAccessToken(loginResult.accessToken.token)
                }

                override fun onCancel() {
                    Toast.makeText(this@SignInActivity, "Facebook login cancelled", Toast.LENGTH_SHORT).show()
                }

                override fun onError(exception: FacebookException) {
                    Toast.makeText(this@SignInActivity, "Facebook login failed. Please try again.", Toast.LENGTH_LONG).show()
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
                    handleFacebookSignInSuccess(auth.currentUser)
                } else {
                    Toast.makeText(this, "Facebook authentication failed", Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun handleFacebookSignInSuccess(user: FirebaseUser?) {
        user?.let {
            Toast.makeText(this, "Welcome ${it.displayName ?: "User"}!", Toast.LENGTH_LONG).show()
            navigateToHome()
        }
    }


    // GitHub Authentication
    private fun signInWithGitHub() {
        if (GITHUB_CLIENT_ID == "YOUR_GITHUB_CLIENT_ID") {
            Toast.makeText(this, "GitHub Client ID not configured.", Toast.LENGTH_LONG).show()
            return
        }
        showGitHubLoginDialog()
    }

    private fun showGitHubLoginDialog() {
        val webView = WebView(this).apply { settings.javaScriptEnabled = true }
        val githubAuthUrl = "https://github.com/login/oauth/authorize" +
                "?client_id=$GITHUB_CLIENT_ID&redirect_uri=$GITHUB_REDIRECT_URI&scope=user:email"

        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                if (url?.startsWith(GITHUB_REDIRECT_URI) == true) {
                    val uri = Uri.parse(url)
                    val code = uri.getQueryParameter("code")
                    if (code != null) handleGitHubCallback(code)
                    return true
                }
                return false
            }
        }

        webView.loadUrl(githubAuthUrl)

        AlertDialog.Builder(this)
            .setTitle("Sign in with GitHub")
            .setView(webView)
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
                Toast.makeText(this, "GitHub login cancelled", Toast.LENGTH_SHORT).show()
            }
            .create()
            .show()
    }

    private fun handleGitHubCallback(code: String) {
        Log.d(TAG, "GitHub code: $code")
        Toast.makeText(this, "GitHub Sign-In needs backend implementation", Toast.LENGTH_LONG).show()
    }

}
