package com.example.deflate

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import com.google.android.material.button.MaterialButton
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SignUpActivity : AppCompatActivity() {
    
    private lateinit var nameEditText: EditText
    private lateinit var surnameEditText: EditText
    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var registerButton: MaterialButton
    private lateinit var loginLink: TextView
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_signup)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.signup_layout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize views
        initializeViews()
        
        // Set up click listeners
        setupClickListeners()
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
        
        // Simulate registration process (replace with actual API call)
        simulateRegistration(name, surname, username, password)
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
    
    private fun simulateRegistration(name: String, surname: String, username: String, password: String) {
        // Simulate network delay
        registerButton.postDelayed({
            // Simulate successful registration
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
            
        }, 2000) // 2 second delay to simulate network call
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
