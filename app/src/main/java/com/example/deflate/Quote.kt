package com.example.deflate

data class Quote(
    val body: String,
    val author: String
)

data class QuoteResponse(
    val quotes: List<Quote>
)
