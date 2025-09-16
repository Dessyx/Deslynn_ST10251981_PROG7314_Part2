package com.example.deflate

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ActivitiesActivity : AppCompatActivity() {
    
    private lateinit var etWeight: EditText
    private lateinit var etSteps: EditText
    private lateinit var btnReset: Button
    private lateinit var btnSaveActivities: Button
    private lateinit var tvCurrentWeight: TextView
    private lateinit var tvCurrentSteps: TextView
    private lateinit var btnBack: ImageView
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_activities)
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activities_layout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        
        initializeViews()
        setupClickListeners()
    }
    
    private fun initializeViews() {
        etWeight = findViewById(R.id.etWeight)
        etSteps = findViewById(R.id.etSteps)
        btnReset = findViewById(R.id.btnReset)
        btnSaveActivities = findViewById(R.id.btnSaveActivities)
        tvCurrentWeight = findViewById(R.id.tvCurrentWeight)
        tvCurrentSteps = findViewById(R.id.tvCurrentSteps)
        btnBack = findViewById(R.id.btnBack)
    }
    
    private fun setupClickListeners() {
        btnReset.setOnClickListener {
            resetInputs()
        }
        
        btnSaveActivities.setOnClickListener {
            saveActivities()
        }
        
        btnBack.setOnClickListener {
            navigateToHome()
        }
    }
    
    private fun resetInputs() {
        etWeight.text.clear()
        etSteps.text.clear()
        Toast.makeText(this, "Inputs cleared", Toast.LENGTH_SHORT).show()
    }
    
    private fun saveActivities() {
        val weightText = etWeight.text.toString().trim()
        val stepsText = etSteps.text.toString().trim()
        
        if (weightText.isEmpty() && stepsText.isEmpty()) {
            Toast.makeText(this, "Please enter weight or steps", Toast.LENGTH_SHORT).show()
            return
        }
        
        if (weightText.isNotEmpty()) {
            try {
                val weight = weightText.toDouble()
                tvCurrentWeight.text = "${weight.toInt()} kgs"
                etWeight.text.clear()
            } catch (e: NumberFormatException) {
                Toast.makeText(this, "Please enter a valid weight", Toast.LENGTH_SHORT).show()
                return
            }
        }
        
        if (stepsText.isNotEmpty()) {
            try {
                val steps = stepsText.toInt()
                tvCurrentSteps.text = "$steps steps"
                etSteps.text.clear()
            } catch (e: NumberFormatException) {
                Toast.makeText(this, "Please enter a valid number of steps", Toast.LENGTH_SHORT).show()
                return
            }
        }
        
        Toast.makeText(this, "Activities saved successfully!", Toast.LENGTH_SHORT).show()
    }
    
    private fun navigateToHome() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }
}