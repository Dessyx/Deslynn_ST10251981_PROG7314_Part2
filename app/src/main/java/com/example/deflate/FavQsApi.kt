package com.example.deflate

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface FavQsApi {
    @GET("quotes")
    fun getQuotes(
        @Header("Authorization") token: String = "Token token=859ca4293cfbfb632606efa098d93226",
        @Query("filter") mood: String,
        @Query("type") type: String = "tag"
    ): Call<QuoteResponse>
}
