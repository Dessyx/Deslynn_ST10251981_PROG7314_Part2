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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class HomeActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private var todayMood: String? = null
    private var currentTagIndex = 0
    private var currentMoodTags = listOf<String>()
    
    private val moodToTagMap = mapOf(
        "Happy" to listOf("happiness", "joy", "smile", "positive", "life"),
        "Sad" to listOf("motivation", "hope", "strength", "courage", "wisdom"),
        "Anxious" to listOf("wisdom", "peace", "calm", "strength", "courage"),
        "Tired" to listOf("motivation", "energy", "strength", "perseverance", "success"),
        "Excited" to listOf("inspiration", "enthusiasm", "passion", "adventure", "life"),
        "Content" to listOf("peace", "gratitude", "satisfaction", "harmony", "wisdom")
    )

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
        val updatedName = intent.getStringExtra("UPDATED_NAME")
        val username = updatedName ?: currentUser?.displayName ?: currentUser?.email?.substringBefore("@") ?: "User"

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
            setMood("Happy")
        }
        btnMoodSad.setOnClickListener {
            setMood("Sad")
        }
        btnMoodAnxious.setOnClickListener {
            setMood("Anxious")
        }
        btnMoodTired.setOnClickListener {
            setMood("Tired")
        }
        btnMoodExcited.setOnClickListener {
            setMood("Excited")
        }
        btnMoodContent.setOnClickListener {
            setMood("Content")
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

    private fun setMood(mood: String) {
        todayMood = mood
        val tvMood = findViewById<TextView>(R.id.tvWelcome)
        tvMood.text = "Today's mood: $mood"
        Toast.makeText(this, "Fetching quotes for $mood...", Toast.LENGTH_SHORT).show()

        currentMoodTags = moodToTagMap[mood] ?: listOf(mood.lowercase())
        currentTagIndex = 0

        if (currentMoodTags.isNotEmpty()) {
            fetchQuote(currentMoodTags[0])
        }
    }

    private fun fetchQuote(tag: String) {
        val call = RetrofitClient.instance.getQuotes(mood = tag)

        call.enqueue(object : Callback<QuoteResponse> {
            override fun onResponse(
                call: Call<QuoteResponse>,
                response: Response<QuoteResponse>
            ) {
                if (response.isSuccessful) {
                    val quotes = response.body()?.quotes
                    if (!quotes.isNullOrEmpty()) {
                        val firstQuote = quotes[0]
                        val quoteText = "\"${firstQuote.body}\" \n- ${firstQuote.author}"
                        findViewById<TextView>(R.id.tvQuote).text = quoteText
                    } else {
                        tryNextTag()
                    }
                } else {
                    tryNextTag()
                }
            }

            override fun onFailure(call: Call<QuoteResponse>, t: Throwable) {
                tryNextTag()
            }
        })
    }
    
    private fun tryNextTag() {
        currentTagIndex++
        if (currentTagIndex < currentMoodTags.size) {
            fetchQuote(currentMoodTags[currentTagIndex])
        } else {
            val fallbackTags = listOf("wisdom", "life", "inspiration", "motivation")
            if (fallbackTags.isNotEmpty()) {
                fetchQuote(fallbackTags[0])
            } else {
                findViewById<TextView>(R.id.tvQuote).text = "No quotes found. Please try another mood!"
            }
        }
    }
}
