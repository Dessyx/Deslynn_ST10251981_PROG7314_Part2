package com.example.deflate

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

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


    private val prefs by lazy { getSharedPreferences("home_prefs", Context.MODE_PRIVATE) }
    private val df = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private fun todayKey() = df.format(Date())

    private fun applyTodayMood(moodKey: String?, save: Boolean = false) {
        val btn = findViewById<MaterialButton>(R.id.btnTodayMood)

        when (moodKey) {
            "Happy" -> {
                btn.text = ""
                btn.setIconResource(R.drawable.mood_happy)
                btn.iconTint = ColorStateList.valueOf(ContextCompat.getColor(this, android.R.color.black))
                btn.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.yellow))
            }
            "Sad" -> {
                btn.text = ""
                btn.setIconResource(R.drawable.mood_sad)
                btn.iconTint = ColorStateList.valueOf(ContextCompat.getColor(this, android.R.color.black))
                btn.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.blue))
            }
            "Anxious" -> {
                btn.text = ""
                btn.setIconResource(R.drawable.mood_anxious)
                btn.iconTint = ColorStateList.valueOf(ContextCompat.getColor(this, android.R.color.black))
                btn.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.purple))
            }
            "Tired" -> {
                btn.text = ""
                btn.setIconResource(R.drawable.mood_tired)
                btn.iconTint = ColorStateList.valueOf(ContextCompat.getColor(this, android.R.color.black))
                btn.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.green))
            }
            "Excited" -> {
                btn.text = ""
                btn.setIconResource(R.drawable.mood_excited)
                btn.iconTint = ColorStateList.valueOf(ContextCompat.getColor(this, android.R.color.black))
                btn.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.red))
            }
            "Content" -> {
                btn.text = ""
                btn.setIconResource(R.drawable.mood_content)
                btn.iconTint = ColorStateList.valueOf(ContextCompat.getColor(this, android.R.color.black))
                btn.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.orange))
            }
            else -> {
                btn.icon = null
                btn.text = ""
                btn.backgroundTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(this, android.R.color.darker_gray)
                )
            }
        }

        if (save) {
            prefs.edit()
                .putString("MOOD_KEY", moodKey)
                .putString("MOOD_DATE", todayKey())
                .apply()
        }
    }

    private fun loadMoodForToday(): String? {
        val savedDate = prefs.getString("MOOD_DATE", null)
        return if (savedDate == todayKey()) prefs.getString("MOOD_KEY", null) else null
    }

    // steps loaded from activities screen!
    private fun loadStepsForHome() {
        val currentUser = auth.currentUser ?: return
        val tvStepsHome = findViewById<TextView>(R.id.tvStepsHome)

        db.collection("activities")
            .whereEqualTo("userId", currentUser.uid)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(20)
            .get()
            .addOnSuccessListener { snap ->
                val today = todayKey()
                var todaySteps: Int? = null
                var latestStepsOverall: Int? = null

                for (doc in snap) {
                    val steps = (doc.get("steps") as? Number)?.toInt()
                    val dk = doc.getString("dateKey")
                    if (steps != null) {
                        if (latestStepsOverall == null) latestStepsOverall = steps
                        if (dk == today) { todaySteps = steps; break }
                    }
                }

                val stepsToShow = todaySteps ?: latestStepsOverall
                tvStepsHome.text = if (stepsToShow != null) "$stepsToShow steps" else "0 steps"
            }
            .addOnFailureListener {
                findViewById<TextView>(R.id.tvStepsHome).text = "0 steps"
            }
    }

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
        db = FirebaseFirestore.getInstance()


        val currentUser = auth.currentUser
        val email = currentUser?.email
        val username = email?.substringBefore("@") ?: "User"
        findViewById<TextView>(R.id.tvWelcome).text = "Welcome, $username"




        val tvDate = findViewById<TextView>(R.id.tvDate)
        val sdf = SimpleDateFormat("EEEE, MMMM dd", Locale.getDefault())
        tvDate.text = sdf.format(Date())




        applyTodayMood(loadMoodForToday(), save = false)

        // Mood grid
        findViewById<Button>(R.id.btnMoodHappy).setOnClickListener   { setMood("Happy") }
        findViewById<Button>(R.id.btnMoodSad).setOnClickListener     { setMood("Sad") }
        findViewById<Button>(R.id.btnMoodAnxious).setOnClickListener { setMood("Anxious") }
        findViewById<Button>(R.id.btnMoodTired).setOnClickListener   { setMood("Tired") }
        findViewById<Button>(R.id.btnMoodExcited).setOnClickListener { setMood("Excited") }
        findViewById<Button>(R.id.btnMoodContent).setOnClickListener { setMood("Content") }

        // Diary button
        findViewById<Button>(R.id.btnDiary).setOnClickListener {
            startActivity(Intent(this, DiaryActivity::class.java))
        }

        // Steps button → Activities page
        findViewById<MaterialButton>(R.id.btnSteps).setOnClickListener {
            startActivity(Intent(this, ActivitiesActivity::class.java))
        }

        // Bottom nav
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_today -> true
                R.id.nav_diary -> { startActivity(Intent(this, DiaryActivity::class.java)); true }
                R.id.nav_calendar -> { startActivity(Intent(this, CalendarActivity::class.java)); true }
                R.id.nav_insights -> { startActivity(Intent(this, InsightsActivity::class.java)); true }
                R.id.nav_settings -> { startActivity(Intent(this, SettingsActivity::class.java)); true }
                else -> false
            }
        }


        loadStepsForHome()
    }

    override fun onResume() {
        super.onResume()
        // refresh steps when returning from Activities screen
        loadStepsForHome()
    }

    private fun setMood(mood: String) {
        todayMood = mood

        // Update “Today’s mood” circle & save for today
        applyTodayMood(mood, save = true)

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
            override fun onResponse(call: Call<QuoteResponse>, response: Response<QuoteResponse>) {
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
