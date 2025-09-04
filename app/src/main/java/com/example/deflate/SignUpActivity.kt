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
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class SignUpActivity : AppCompatActivity() {
    
    companion object {
        private const val TAG = "SignUpActivity"
    }
    
    private lateinit var nameEditText: EditText
    private lateinit var surnameEditText: EditText
private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var registerButton: MaterialButton
    private lateinit var loginLink: TextView
    
    // Firebase instances
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    
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
        // registerUserWithFirebase(name, surname, username, password)
        
        // TEMPORARY: For testing without Firebase setup
        // Uncomment the line below and comment out the line above if Firebase isn't configured yet
        simulateRegistrationForTesting(name, surname, username, password)
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
        
        // Navigate to home screen
        navigateToHome()
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
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish() // Close signup activity
    }
    
    override fun onDestroy() {
        super.onDestroy()
        // Remove any pending callbacks to prevent memory leaks
        registerButton.removeCallbacks(null)
    }
}
