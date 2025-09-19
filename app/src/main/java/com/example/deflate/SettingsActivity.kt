package com.example.deflate

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class SettingsActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var tvUserName: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        // Firebase Auth
        auth = FirebaseAuth.getInstance()

        // UI references
        val btnBack: ImageView = findViewById(R.id.btnBack)
        tvUserName = findViewById(R.id.tvUserName)
        val etName: EditText = findViewById(R.id.etName)
        val etPassword: EditText = findViewById(R.id.etPassword)
        val btnSave: Button = findViewById(R.id.btnSave)
        val btnDeleteAccount: Button = findViewById(R.id.btnDeleteAccount)

        // Show logged-in user name or email
        val user = auth.currentUser
        tvUserName.text = user?.displayName ?: user?.email ?: "User"

        // Go back to home
        btnBack.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Save new details
        btnSave.setOnClickListener {
            val newName = etName.text.toString()
            val newPass = etPassword.text.toString()

            if (newName.isNotEmpty()) {
                tvUserName.text = newName
            }

            Toast.makeText(this, "Saved successfully", Toast.LENGTH_SHORT).show()
        }

        // Delete account
        btnDeleteAccount.setOnClickListener {
            user?.delete()?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Account deleted", Toast.LENGTH_LONG).show()

                    // Redirect to login screen
                    val intent = Intent(this, SignInActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Failed: ${task.exception?.message}", Toast.LENGTH_LONG)
                        .show()
                    //  Bottom navigation
                    val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
                    bottomNav.selectedItemId = R.id.nav_settings // highlight settings tab

                    bottomNav.setOnItemSelectedListener { item ->
                        when (item.itemId) {
                            R.id.nav_today -> {
                                startActivity(Intent(this, HomeActivity::class.java))
                                true
                            }

                            R.id.nav_diary -> {
                                startActivity(Intent(this, DiaryActivity::class.java))
                                true
                            }

                            R.id.nav_calendar -> {
                                startActivity(Intent(this, CalendarActivity::class.java))
                                true
                            }

                            R.id.nav_insights -> {
                                startActivity(Intent(this, InsightsActivity::class.java))
                                true
                            }

                            R.id.nav_settings -> true
                            else -> false
                        }
                    }
                }
            }
        }
    }
}