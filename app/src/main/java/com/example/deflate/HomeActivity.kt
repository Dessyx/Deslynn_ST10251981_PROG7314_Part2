package com.example.deflate

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView


class HomeActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private var todayMood: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.home_layout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        auth = FirebaseAuth.getInstance()

        // Get the logged in Firebase user
        val currentUser = auth.currentUser
        val email = currentUser?.email
        val username = email?.substringBefore("@") ?: "User"
        //  Welcome text
        val tvWelcome = findViewById<TextView>(R.id.tvWelcome)
        tvWelcome.text = "Welcome, $username"
        //  Mood buttons
        val btnMoodHappy = findViewById<Button>(R.id.btnMoodHappy)
        val btnMoodSad = findViewById<Button>(R.id.btnMoodSad)
        val btnMoodAnxious = findViewById<Button>(R.id.btnMoodAnxious)
        val btnMoodTired = findViewById<Button>(R.id.btnMoodTired)
        val btnMoodExcited = findViewById<Button>(R.id.btnMoodExcited)
        val btnMoodContent = findViewById<Button>(R.id.btnMoodContent)

        btnMoodHappy.setOnClickListener {
            todayMood = "Happy"
            Toast.makeText(this, "Mood set to Happy ", Toast.LENGTH_SHORT).show()
        }
        btnMoodSad.setOnClickListener {
            todayMood = "Sad"
            Toast.makeText(this, "Mood set to Sad", Toast.LENGTH_SHORT).show()
        }
        btnMoodAnxious.setOnClickListener {
            todayMood = "Anxious"
            Toast.makeText(this, "Mood set to Anxious ", Toast.LENGTH_SHORT).show()
        }
        btnMoodTired.setOnClickListener {
            todayMood = "Tired"
            Toast.makeText(this, "Mood set to Tired ", Toast.LENGTH_SHORT).show()
        }
        btnMoodExcited.setOnClickListener {
            todayMood = "Excited"
            Toast.makeText(this, "Mood set to Excited ", Toast.LENGTH_SHORT).show()
        }
        btnMoodContent.setOnClickListener {
            todayMood = "Content"
            Toast.makeText(this, "Mood set to Content ", Toast.LENGTH_SHORT).show()
        }
        // Diary button
        val btnDiary = findViewById<Button>(R.id.btnDiary)
        btnDiary.setOnClickListener {
            startActivity(Intent(this, DiaryActivity::class.java))

        }
        // Activities button
        val btnSteps = findViewById<com.google.android.material.button.MaterialButton>(R.id.btnSteps)
        btnSteps.setOnClickListener {
            startActivity(Intent(this, ActivitiesActivity::class.java))
        }

        // Bottom navigation
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_today -> true
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

                R.id.nav_settings -> {
                    startActivity(Intent(this, SettingsActivity::class.java))
                    true
                }

                else -> false
            }
        }
    }
}
