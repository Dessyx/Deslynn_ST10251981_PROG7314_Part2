package com.example.deflate

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import com.google.android.material.button.MaterialButton
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FacebookAuthProvider
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import android.net.Uri
import android.webkit.WebView
import android.webkit.WebViewClient
import android.app.AlertDialog
import java.util.*

class SignUpActivity : AppCompatActivity() {
    
    companion object {
        private const val TAG = "SignUpActivity"
        private const val RC_SIGN_IN = 9001
        private const val GITHUB_CLIENT_ID = "Ov23liwG3uaDjiDZJnR4"
        private const val GITHUB_REDIRECT_URI = "http://localhost:8080/github-callback"
    }
    
    private lateinit var nameEditText: EditText
    private lateinit var surnameEditText: EditText
    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var registerButton: MaterialButton
    private lateinit var loginLink: TextView
    private lateinit var googleSignUpButton: MaterialButton
    private lateinit var githubSignUpButton: MaterialButton
    private lateinit var facebookSignUpButton: MaterialButton
    
    // Firebase instances
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var googleSignInClient: GoogleSignInClient
    
    // Facebook instances
    private lateinit var callbackManager: CallbackManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_signup)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.signup_layout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize Firebase
        initializeFirebase()
        
        // Configure Google Sign-In
        configureGoogleSignIn()
        
        // Initialize Facebook
        initializeFacebook()
        
        // Initialize views
        initializeViews()
        
        // Set up click listeners
        setupClickListeners()
    }
    
    private fun initializeFirebase() {
        try {
            auth = FirebaseAuth.getInstance()
            firestore = FirebaseFirestore.getInstance()
            Log.d(TAG, "Firebase initialized successfully")
            Log.d(TAG, "Firebase Auth instance: $auth")
            Log.d(TAG, "Firestore instance: $firestore")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize Firebase", e)
            Toast.makeText(this, "Firebase initialization failed: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
    
    private fun initializeViews() {
        nameEditText = findViewById(R.id.name_edittext)
        surnameEditText = findViewById(R.id.surname_edittext)
        usernameEditText = findViewById(R.id.username_edittext)
        passwordEditText = findViewById(R.id.password_edittext)
        registerButton = findViewById(R.id.register_button)
        loginLink = findViewById(R.id.login_link)
        googleSignUpButton = findViewById(R.id.google_signup_button)
        githubSignUpButton = findViewById(R.id.github_signup_button)
        facebookSignUpButton = findViewById(R.id.facebook_signup_button)
    }
    
    private fun setupClickListeners() {
        // Register button click listener
        registerButton.setOnClickListener {
            handleRegistration()
        }
        
        // Login link click listener
        loginLink.setOnClickListener {
            navigateToLogin()
        }
        
        // Google Sign-Up button click listener
        googleSignUpButton.setOnClickListener {
            signUpWithGoogle()
        }
        
        // GitHub Sign-Up button click listener (placeholder)
        githubSignUpButton.setOnClickListener {
            handleGitHubSignUp()
        }
        
        // Facebook Sign-Up button click listener (placeholder)
        facebookSignUpButton.setOnClickListener {
            handleFacebookSignUp()
        }
    }
    
    private fun handleRegistration() {
        // Get input values
        val name = nameEditText.text.toString().trim()
        val surname = surnameEditText.text.toString().trim()
        val username = usernameEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()
        
        // Validate inputs
        if (!validateInputs(name, surname, username, password)) {
            return
        }
        
        // Show loading state
        registerButton.isEnabled = false
        registerButton.text = "Registering..."
        
        // Register user with Firebase
registerUserWithFirebase(name, surname, username, password)
        
        // TEMPORARY: For testing without Firebase setup
        // Uncomment the line below and comment out the line above if Firebase isn't configured yet
       // simulateRegistrationForTesting(name, surname, username, password)
    }
    
    private fun validateInputs(name: String, surname: String, username: String, password: String): Boolean {
        var isValid = true
        
        // Clear previous errors
        clearErrors()
        
        // Validate name
        if (TextUtils.isEmpty(name)) {
            nameEditText.error = "Name is required"
            nameEditText.requestFocus()
            isValid = false
        } else if (name.length < 2) {
            nameEditText.error = "Name must be at least 2 characters"
            nameEditText.requestFocus()
            isValid = false
        }
        
        // Validate surname
        if (TextUtils.isEmpty(surname)) {
            surnameEditText.error = "Surname is required"
            if (isValid) surnameEditText.requestFocus()
            isValid = false
        } else if (surname.length < 2) {
            surnameEditText.error = "Surname must be at least 2 characters"
            if (isValid) surnameEditText.requestFocus()
            isValid = false
        }
        
        // Validate username
        if (TextUtils.isEmpty(username)) {
            usernameEditText.error = "Username is required"
            if (isValid) usernameEditText.requestFocus()
            isValid = false
        } else if (username.length < 3) {
            usernameEditText.error = "Username must be at least 3 characters"
            if (isValid) usernameEditText.requestFocus()
            isValid = false
        } else if (!isValidUsername(username)) {
            usernameEditText.error = "Username can only contain letters, numbers, and underscores"
            if (isValid) usernameEditText.requestFocus()
            isValid = false
        }
        
        // Validate password
        if (TextUtils.isEmpty(password)) {
            passwordEditText.error = "Password is required"
            if (isValid) passwordEditText.requestFocus()
            isValid = false
        } else if (password.length < 8) {
            passwordEditText.error = "Password must be at least 8 characters"
            if (isValid) passwordEditText.requestFocus()
            isValid = false
        } else if (!isValidPassword(password)) {
            passwordEditText.error = "Password must contain 1 capital letter, 1 symbol, and 8+ characters"
            if (isValid) passwordEditText.requestFocus()
            isValid = false
        }
        
        return isValid
    }
    
    private fun clearErrors() {
        nameEditText.error = null
        surnameEditText.error = null
        usernameEditText.error = null
        passwordEditText.error = null
    }
    
    private fun isValidUsername(username: String): Boolean {
        val usernamePattern = "^[a-zA-Z0-9_]+$"
        return username.matches(usernamePattern.toRegex())
    }
    
    private fun isValidPassword(password: String): Boolean {
        val hasCapitalLetter = password.any { it.isUpperCase() }
        val hasSymbol = password.any { !it.isLetterOrDigit() }
        val hasMinimumLength = password.length >= 8
        return hasCapitalLetter && hasSymbol && hasMinimumLength
    }
    
    private fun registerUserWithFirebase(name: String, surname: String, username: String, password: String) {
        // Create user with email (using username as email for now)
        val email = "$username@deflate.com" // You can modify this logic as needed
        
        Log.d(TAG, "Attempting to register user with email: $email")
        
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "User created successfully")
                    // User created successfully, now save additional data to Firestore
                    val user = auth.currentUser
                    user?.let {
                        saveUserDataToFirestore(it.uid, name, surname, username, email)
                    }
                } else {
                    // Registration failed - get detailed error
                    val exception = task.exception
                    Log.e(TAG, "Registration failed", exception)
                    
                    val errorMessage = when (exception) {
                        is FirebaseAuthException -> {
                            when (exception.errorCode) {
                                "ERROR_EMAIL_ALREADY_IN_USE" -> "This email is already registered"
                                "ERROR_INVALID_EMAIL" -> "Invalid email format"
                                "ERROR_WEAK_PASSWORD" -> "Password is too weak"
                                "ERROR_NETWORK_REQUEST_FAILED" -> "Network error. Please check your internet connection"
                                else -> {
                                    // Handle CONFIGURATION_NOT_FOUND specifically
                                    if (exception.message?.contains("CONFIGURATION_NOT_FOUND") == true) {
                                        "Firebase Authentication not properly configured. Please check Firebase Console settings."
                                    } else {
                                        "Registration failed: ${exception.message}"
                                    }
                                }
                            }
                        }
                        else -> {
                            // Handle general Firebase exceptions
                            val message = exception?.message ?: "Unknown error"
                            when {
                                message.contains("CONFIGURATION_NOT_FOUND") -> "Firebase Authentication not properly configured. Please check Firebase Console settings."
                                message.contains("network error") || message.contains("timeout") -> "Network error. Please check your internet connection and try again."
                                message.contains("PERMISSION_DENIED") -> "Firebase permissions not configured. Please check Firebase Console settings."
                                else -> "Registration failed: $message"
                            }
                        }
                    }
                    
                    handleRegistrationError(errorMessage)
                }
            }
    }
    
    private fun saveUserDataToFirestore(uid: String, name: String, surname: String, username: String, email: String) {
        val userData = hashMapOf(
            "uid" to uid,
            "name" to name,
            "surname" to surname,
            "username" to username,
            "email" to email,
            "createdAt" to Date(),
            "isActive" to true
        )
        
        Log.d(TAG, "Saving user data to Firestore for UID: $uid")
        
        firestore.collection("users")
            .document(uid)
            .set(userData)
            .addOnSuccessListener {
                Log.d(TAG, "User data saved successfully to Firestore")
                // Data saved successfully
                handleRegistrationSuccess(name, surname)
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Failed to save user data to Firestore", exception)
                // Data save failed, but user is created in Auth
                handleRegistrationError("User created but data save failed: ${exception.message}")
            }
    }
    
    private fun handleRegistrationSuccess(name: String, surname: String) {
        val fullName = "$name $surname"
        
        // Show success message
        Toast.makeText(this, "Registration successful! Welcome $fullName", Toast.LENGTH_LONG).show()
        
        // Reset button state
        registerButton.isEnabled = true
        registerButton.text = "Register"
        
        // Clear form
        clearForm()
        
        // Navigate to login screen
        navigateToLogin()
    }
    
    private fun handleRegistrationError(errorMessage: String) {
        // Show error message
        Toast.makeText(this, "Registration failed: $errorMessage", Toast.LENGTH_LONG).show()
        
        // Reset button state
        registerButton.isEnabled = true
        registerButton.text = "Register"
    }
    
    // TEMPORARY: For testing without Firebase setup
    private fun simulateRegistrationForTesting(name: String, surname: String, username: String, password: String) {
        registerButton.postDelayed({
            val fullName = "$name $surname"
            Toast.makeText(this, "TEST MODE: Registration successful! Welcome $fullName", Toast.LENGTH_LONG).show()
            
            registerButton.isEnabled = true
            registerButton.text = "Register"
            
            clearForm()
            navigateToHome()
        }, 2000)
    }
    
    private fun clearForm() {
        nameEditText.text.clear()
        surnameEditText.text.clear()
        usernameEditText.text.clear()
        passwordEditText.text.clear()
        clearErrors()
    }
    
    private fun navigateToHome() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish() // Close signup activity
    }
    
    private fun navigateToLogin() {
        val intent = Intent(this, SignInActivity::class.java)
        startActivity(intent)
        finish() // Close signup activity
    }
    
    private fun configureGoogleSignIn() {
        // Configure Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    private fun signUpWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Handle Facebook callback
        callbackManager.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign-In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign-In failed
                Log.w(TAG, "Google sign in failed", e)
                Toast.makeText(this, "Google Sign-In failed: ${e.message}", Toast.LENGTH_SHORT).show()
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
                    handleGoogleSignUpSuccess(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    Toast.makeText(this, "Authentication failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun handleGoogleSignUpSuccess(user: com.google.firebase.auth.FirebaseUser?) {
        user?.let {
            val displayName = it.displayName ?: "User"
            val email = it.email ?: ""
            
            // Save user data to Firestore
            saveGoogleUserDataToFirestore(it.uid, displayName, email)
            
            Toast.makeText(this, "Welcome $displayName!", Toast.LENGTH_LONG).show()
            navigateToHome()
        }
    }

    private fun saveGoogleUserDataToFirestore(uid: String, displayName: String, email: String) {
        // Split display name into first and last name
        val nameParts = displayName.split(" ")
        val firstName = nameParts.firstOrNull() ?: "User"
        val lastName = nameParts.drop(1).joinToString(" ") ?: ""
        
        // Generate username from email
        val username = email.substringBefore("@")
        
        val userData = hashMapOf(
            "uid" to uid,
            "name" to firstName,
            "surname" to lastName,
            "username" to username,
            "email" to email,
            "createdAt" to Date(),
            "isActive" to true,
            "signUpMethod" to "google"
        )
        
        Log.d(TAG, "Saving Google user data to Firestore for UID: $uid")
        
        firestore.collection("users")
            .document(uid)
            .set(userData)
            .addOnSuccessListener {
                Log.d(TAG, "Google user data saved successfully to Firestore")
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Failed to save Google user data to Firestore", exception)
            }
    }

    // Placeholder methods for GitHub and Facebook sign-up
    private fun handleGitHubSignUp() {
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
        // For now, we'll create a custom token for GitHub users
        // In a real implementation, you'd exchange the code for an access token
        // and then create a custom Firebase token
        
        Log.d(TAG, "GitHub authorization code received: $code")
        
        // Create a custom user for GitHub (this is a simplified approach)
        // In production, you'd want to implement proper OAuth token exchange
        val githubEmail = "github_user_${System.currentTimeMillis()}@github.com"
        val githubPassword = "github_temp_password_${System.currentTimeMillis()}"
        
        auth.createUserWithEmailAndPassword(githubEmail, githubPassword)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    handleGitHubSignUpSuccess(user, code)
                } else {
                    Log.e(TAG, "GitHub user creation failed", task.exception)
                    Toast.makeText(this, "GitHub authentication failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun handleGitHubSignUpSuccess(user: com.google.firebase.auth.FirebaseUser?, githubCode: String) {
        user?.let {
            val displayName = "GitHub User"
            val email = it.email ?: ""
            
            // Save user data to Firestore
            saveGitHubUserDataToFirestore(it.uid, displayName, email, githubCode)
            
            Toast.makeText(this, "Welcome GitHub User!", Toast.LENGTH_LONG).show()
            navigateToHome()
        }
    }

    private fun saveGitHubUserDataToFirestore(uid: String, displayName: String, email: String, githubCode: String) {
        val userData = hashMapOf(
            "uid" to uid,
            "name" to "GitHub",
            "surname" to "User",
            "username" to "github_user_${System.currentTimeMillis()}",
            "email" to email,
            "createdAt" to Date(),
            "isActive" to true,
            "signUpMethod" to "github",
            "githubCode" to githubCode
        )
        
        Log.d(TAG, "Saving GitHub user data to Firestore for UID: $uid")
        
        firestore.collection("users")
            .document(uid)
            .set(userData)
            .addOnSuccessListener {
                Log.d(TAG, "GitHub user data saved successfully to Firestore")
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Failed to save GitHub user data to Firestore", exception)
            }
    }
    
    private fun initializeFacebook() {
        callbackManager = CallbackManager.Factory.create()
        
        LoginManager.getInstance().registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                Log.d(TAG, "Facebook login successful")
                handleFacebookAccessToken(loginResult.accessToken.token)
            }

            override fun onCancel() {
                Log.d(TAG, "Facebook login cancelled")
                Toast.makeText(this@SignUpActivity, "Facebook login cancelled", Toast.LENGTH_SHORT).show()
            }

            override fun onError(exception: FacebookException) {
                Log.e(TAG, "Facebook login failed", exception)
                Toast.makeText(this@SignUpActivity, "Facebook login failed: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun handleFacebookSignUp() {
        LoginManager.getInstance().logInWithReadPermissions(this, listOf("email", "public_profile"))
    }

    private fun handleFacebookAccessToken(token: String) {
        val credential = FacebookAuthProvider.getCredential(token)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "Facebook authentication successful")
                    val user = auth.currentUser
                    handleFacebookSignUpSuccess(user)
                } else {
                    Log.e(TAG, "Facebook authentication failed", task.exception)
                    Toast.makeText(this, "Facebook authentication failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun handleFacebookSignUpSuccess(user: com.google.firebase.auth.FirebaseUser?) {
        user?.let {
            val displayName = it.displayName ?: "User"
            val email = it.email ?: ""
            
            // Save user data to Firestore
            saveFacebookUserDataToFirestore(it.uid, displayName, email)
            
            Toast.makeText(this, "Welcome $displayName!", Toast.LENGTH_LONG).show()
            navigateToHome()
        }
    }

    private fun saveFacebookUserDataToFirestore(uid: String, displayName: String, email: String) {
        // Split display name into first and last name
        val nameParts = displayName.split(" ")
        val firstName = nameParts.firstOrNull() ?: "User"
        val lastName = nameParts.drop(1).joinToString(" ") ?: ""
        
        // Generate username from email
        val username = email.substringBefore("@")
        
        val userData = hashMapOf(
            "uid" to uid,
            "name" to firstName,
            "surname" to lastName,
            "username" to username,
            "email" to email,
            "createdAt" to Date(),
            "isActive" to true,
            "signUpMethod" to "facebook"
        )
        
        Log.d(TAG, "Saving Facebook user data to Firestore for UID: $uid")
        
        firestore.collection("users")
            .document(uid)
            .set(userData)
            .addOnSuccessListener {
                Log.d(TAG, "Facebook user data saved successfully to Firestore")
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Failed to save Facebook user data to Firestore", exception)
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Remove any pending callbacks to prevent memory leaks
        registerButton.removeCallbacks(null)
    }
}
