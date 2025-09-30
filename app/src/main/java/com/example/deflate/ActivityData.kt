package com.example.deflate

import com.google.firebase.firestore.PropertyName
import java.util.Date

data class ActivityData(
    @PropertyName("userId")
    val userId: String = "",
    
    @PropertyName("weight")
    val weight: Double? = null,
    
    @PropertyName("steps")
    val steps: Int? = null,
    
    @PropertyName("date")
    val date: Date? = null,
    
    @PropertyName("timestamp")
    val timestamp: Long = 0L
)